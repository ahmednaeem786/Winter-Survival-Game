package game.quest.api;

/**
 * Minimal client abstraction for Gemini text generation API.
 * Implementations handle the HTTP communication and response parsing.
 */

public interface GeminiClient {
    /**
     * Sends a prompt to Gemini and returns the generated text.
     *
     * @param model the Gemini model identifier to use
     * @param apiKey the API key for authentication
     * @param prompt the text prompt to send to the model
     * @return the generated text response from the first candidate
     * @throws Exception if the API call fails or response cannot be parsed
     */
    String generateText(String model, String apiKey, String prompt) throws Exception;
}
