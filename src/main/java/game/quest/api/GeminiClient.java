package game.quest.api;

/**
 * Minimal client abstraction for Gemini text generation.
 */
public interface GeminiClient {
    /**
     * Sends a prompt and returns raw text from the first candidate.
     */
    String generateText(String model, String apiKey, String prompt) throws Exception;
}
