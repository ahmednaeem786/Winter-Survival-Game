package game.quest;

import game.quest.api.ApiConfig;
import game.quest.api.GeminiClient;
import game.quest.provider.LocalQuestGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * QuestService implementation backed by Gemini. Builds a strict prompt that asks
 * for a compact JSON structure, then parses it into the in-game Quest model.
 * Falls back to LocalQuestGenerator on failures to preserve gameplay.
 */
public class GeminiQuestGenerator implements QuestService {

    private final GeminiClient client;

    public GeminiQuestGenerator(GeminiClient client) {
        this.client = client;
    }

    @Override
    public Quest generateQuest() {
        if (!ApiConfig.isConfigured()) {
            return new LocalQuestGenerator().generateQuest();
        }
        String key = ApiConfig.apiKey();
        String prompt = buildPrompt();

        String configured = ApiConfig.model();
        String[] candidates = modelCandidates(configured);

        for (String model : candidates) {
            try {
                String text = client.generateText(model, key, prompt);
                text = normalizeResponse(text);
                if (isErrorEnvelope(text)) {
                    continue;
                }
                Quest q = parseQuest(text);
                if (q != null) return q;
            } catch (Exception e) {
                // try next candidate
            }
        }
        // fallback for robustness
        return new LocalQuestGenerator().generateQuest();
    }

    private String buildPrompt() {
        return String.join("\n",
                "You are the Mystical Questmaster in a survival roguelike.",
                "Generate ONE quest suited to a cold forest with wolves, bears, and deer.",
                "Quest types: KILL (Wolf|Bear|Deer), COLLECT (YewBerry|Hazelnut|Apple), VISIT (Cave->Tundra->Meadow).",
                "Return STRICT JSON ONLY with this schema:",
                "{",
                "  \"title\": string,",
                "  \"description\": string,",
                "  \"objectives\": [",
                "    { \"type\": \"KILL|COLLECT|VISIT\", \"target\": string, \"requiredAmount\": number, \"orderedLocations\": [string]? }",
                "  ],",
                "  \"rewards\": [ { \"name\": string, \"description\": string } ]",
                "}",
                "No markdown, no prose, JSON only.");
    }

    private Quest parseQuest(String json) {
        if (json == null || json.isBlank()) return null;
        String title = group(json, Pattern.compile("\"title\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL));
        String desc  = group(json, Pattern.compile("\"description\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL));
        if (title == null || desc == null) return null;
        Quest q = new Quest(title, desc);

        String objs = group(json, Pattern.compile("\"objectives\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL));
        if (objs != null) {
            for (String o : splitObjects(objs)) {
                String type   = group(o, Pattern.compile("\"type\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL));
                String target = group(o, Pattern.compile("\"target\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL));
                String amtS   = group(o, Pattern.compile("\"requiredAmount\"\\s*:\\s*([0-9]+)", Pattern.DOTALL));
                int amt = 1;
                if (amtS != null) { try { amt = Integer.parseInt(amtS.trim()); } catch (Exception ignored) {} }
                ObjectiveType ot = ObjectiveType.KILL;
                if ("COLLECT".equalsIgnoreCase(type)) ot = ObjectiveType.COLLECT; else if ("VISIT".equalsIgnoreCase(type)) ot = ObjectiveType.VISIT;
                if (ot == ObjectiveType.COLLECT) {
                    // Cap collect requirements to max 5 to keep gameplay tight
                    if (amt > 5) amt = 5;
                    if (amt < 1) amt = 1;
                }

                List<String> ordered = new ArrayList<>();
                String list = group(o, Pattern.compile("\"orderedLocations\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL));
                if (list != null) {
                    for (String p : list.split(",")) {
                        String v = p.trim();
                        if (v.startsWith("\"") && v.endsWith("\"")) ordered.add(v.substring(1, v.length()-1));
                    }
                }
                q.addObjective(new QuestObjective(ot, target != null ? target : "", amt, ordered));
            }
        }

        String rewards = group(json, Pattern.compile("\"rewards\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL));
        if (rewards != null) {
            for (String r : splitObjects(rewards)) {
                String name = group(r, Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL));
                String rd   = group(r, Pattern.compile("\"description\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL));
                if (name != null) q.addReward(new QuestReward(name, rd != null ? rd : ""));
            }
        }
        return q;
    }

    private static String normalizeResponse(String text) {
        if (text == null) return null;
        String t = text.trim();

        if (t.contains("\"candidates\"") && t.contains("\"parts\"")) {
            String extracted = extractGeminiText(t);
            if (extracted != null && !extracted.isBlank()) {
                t = extracted.trim();
            }
        }
        // strip ```json ... ``` fences
        if (t.startsWith("```")) {
            t = t.replaceFirst("^```[a-zA-Z0-9]*", "");
            int fence = t.lastIndexOf("```");
            if (fence >= 0) t = t.substring(0, fence);
        }
        t = t.trim();
        if (!t.startsWith("{")) {
            int start = t.indexOf('{');
            int end = t.lastIndexOf('}');
            if (start >= 0 && end > start) t = t.substring(start, end + 1);
        }
        return t.trim();
    }

    private static String extractGeminiText(String response) {
        int idx = response.indexOf("\"text\":");
        if (idx < 0) return null;
        int start = response.indexOf('"', idx + 7);
        if (start < 0) return null;
        start += 1;
        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        for (int i = start; i < response.length(); i++) {
            char c = response.charAt(i);
            if (escape) {
                if (c == 'n') sb.append('\n');
                else if (c == 't') sb.append('\t');
                else if (c == 'r') sb.append('\r');
                else sb.append(c);
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static boolean isErrorEnvelope(String text) {
        if (text == null) return true;
        // crude check: if it looks like an error object
        return text.contains("\"error\"") && text.contains("\"code\"");
    }

    private static String[] modelCandidates(String configured) {
        List<String> out = new ArrayList<>();
        if (configured != null && !configured.isBlank()) out.add(configured);
        String[] common = new String[] {"gemini-2.5-flash", "gemini-2.5-pro", "gemini-2.0-flash"};
        for (String c : common) if (!out.contains(c)) out.add(c);
        return out.toArray(new String[0]);
    }

    private static String group(String text, Pattern p) {
        Matcher m = p.matcher(text);
        if (m.find()) {
            for (int i = m.groupCount(); i >= 1; i--) {
                String g = m.group(i);
                if (g != null) return g;
            }
            return m.group(0);
        }
        return null;
    }

    private static List<String> splitObjects(String block) {
        List<String> out = new ArrayList<>();
        int depth = 0; int start = -1;
        boolean inString = false; boolean escape = false;
        for (int i = 0; i < block.length(); i++) {
            char c = block.charAt(i);
            if (escape) { escape = false; continue; }
            if (c == '\\') { escape = true; continue; }
            if (c == '"') { inString = !inString; continue; }
            if (inString) continue;
            if (c == '{') { if (depth == 0) start = i; depth++; }
            else if (c == '}') { depth--; if (depth == 0 && start >= 0) { out.add(block.substring(start, i+1)); start = -1; } }
        }
        return out;
    }
}

