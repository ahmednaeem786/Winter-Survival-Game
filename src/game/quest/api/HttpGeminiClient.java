package game.quest.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Simple HTTP client using JDK HttpClient to call Gemini's generateContent endpoint.
 * Avoids external dependencies as per assignment constraints.
 */
public class HttpGeminiClient implements GeminiClient {

    private final HttpClient http = HttpClient.newHttpClient();

    @Override
    public String generateText(String model, String apiKey, String prompt) throws Exception {
        String url = "https://generativelanguage.googleapis.com/" + ApiConfig.apiVersion() + "/models/" +
                encode(model) + ":generateContent?key=" + encode(apiKey);

        String body = "{\n" +
                "  \"contents\": [ { \"parts\": [ { \"text\": " + jsonString(prompt) + " } ] } ]\n" +
                "}";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        String respBody = resp.body();
        String text = extractFirstText(respBody);
        return (text != null && !text.isBlank()) ? text : respBody;
    }

    private static String encode(String s) {
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String jsonString(String s) {
        if (s == null) return "\"\"";
        StringBuilder out = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int)c));
                    } else {
                        out.append(c);
                    }
            }
        }
        out.append('"');
        return out.toString();
    }

    /**
     * Extremely small parser to extract candidates[0].content.parts[0].text
     * This avoids external JSON libs. Adequate for assignment demo purposes.
     */
    static String extractFirstText(String response) {
        if (response == null) return null;
        // naive search for "text":"..."
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
                // minimal unescape for JSON
                if (c == 'n') sb.append('\n');
                else if (c == 't') sb.append('\t');
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
}


