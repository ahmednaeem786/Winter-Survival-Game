package game.quest;

import game.quest.api.GeminiClient;
import game.quest.model.ObjectiveType;
import game.quest.model.Quest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GeminiQuestGenerator using a stubbed GeminiClient (no network).
 */
public class GeminiQuestGeneratorTest {

    static class FakeGeminiClient implements GeminiClient {
        private final String payload; private final boolean fail;
        FakeGeminiClient(String payload) { this.payload = payload; this.fail = false; }
        FakeGeminiClient(boolean fail) { this.payload = "{}"; this.fail = fail; }
        @Override public String generateText(String model, String apiKey, String prompt) {
            if (fail) throw new RuntimeException("boom");
            return payload;
        }
    }

    @Test
    void parsesVisitQuestFromJson() {
        System.setProperty("GEMINI_API_KEY", "test-key");
        String json = "{\n" +
                "  \"title\": \"Explorer\",\n" +
                "  \"description\": \"Visit places\",\n" +
                "  \"objectives\": [{ \"type\": \"VISIT\", \"target\": \"Route\", \"requiredAmount\": 3, \"orderedLocations\": [\"Cave\", \"Tundra\", \"Meadow\"] }],\n" +
                "  \"rewards\": [{ \"name\": \"Torch\", \"description\": \"light\"}]\n" +
                "}";
        GeminiQuestGenerator gen = new GeminiQuestGenerator(new FakeGeminiClient(json));
        Quest q = gen.generateQuest();
        assertNotNull(q);
        assertFalse(q.getObjectives().isEmpty());
        assertEquals(ObjectiveType.VISIT, q.getObjectives().get(0).getType());
        assertEquals(3, q.getObjectives().get(0).getOrderedLocations().size());
        System.clearProperty("GEMINI_API_KEY");
    }

    @Test
    void parsesKillQuestFromJson() {
        System.setProperty("GEMINI_API_KEY", "test-key");
        String json = "{\n" +
                "  \"title\": \"Hunt\",\n" +
                "  \"description\": \"Cull wolves\",\n" +
                "  \"objectives\": [{ \"type\": \"KILL\", \"target\": \"Wolf\", \"requiredAmount\": 3 }],\n" +
                "  \"rewards\": []\n" +
                "}";
        GeminiQuestGenerator gen = new GeminiQuestGenerator(new FakeGeminiClient(json));
        Quest q = gen.generateQuest();
        assertEquals(ObjectiveType.KILL, q.getObjectives().get(0).getType());
        assertEquals("Wolf", q.getObjectives().get(0).getTarget());
        System.clearProperty("GEMINI_API_KEY");
    }

    @Test
    void fallsBackToLocalOnError() {
        System.setProperty("GEMINI_API_KEY", "test-key");
        GeminiQuestGenerator gen = new GeminiQuestGenerator(new FakeGeminiClient(true));
        Quest q = gen.generateQuest();
        assertNotNull(q.getTitle());
        assertFalse(q.getObjectives().isEmpty());
        System.clearProperty("GEMINI_API_KEY");
    }
}

