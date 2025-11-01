package game.quest.api;

/**
 * Loads API configuration from environment variables or system properties.
 * Provides access to Gemini API credentials and settings.
 *
 * <p>Configuration is read in the following order:
 * <ol>
 *   <li>Environment variables</li>
 *   <li>System properties</li>
 *   <li>Default values</li>
 * </ol>
 */
public final class ApiConfig {
    private static final String API_KEY_ENV = "GEMINI_API_KEY";
    private static final String MODEL_ENV = "GEMINI_MODEL";
    private static final String VERSION_ENV = "GEMINI_API_VERSION";

    private ApiConfig() {}

    /**
     * Returns the Gemini API key from environment or system properties.
     *
     * @return the API key, or empty string if not configured
     */
    public static String apiKey() {
        String v = System.getenv(API_KEY_ENV);
        if (v == null || v.isBlank()) {
            v = System.getProperty(API_KEY_ENV, "");
        }
        return v;
    }

    /**
     * Returns the Gemini model name from environment or system properties.
     *
     * @return the model name, defaults to "gemini-2.5-flash"
     */
    public static String model() {
        String v = System.getenv(MODEL_ENV);
        if (v == null || v.isBlank()) {
            v = System.getProperty(MODEL_ENV, "gemini-2.5-flash");
        }
        return v;
    }

    /**
     * Checks whether the API is properly configured with a valid key.
     *
     * @return true if API key is present and non-blank, false otherwise
     */
    public static boolean isConfigured() {
        return !apiKey().isBlank();
    }

    /**
     * Returns the Gemini API version from environment or system properties.
     *
     * @return the API version, defaults to "v1"
     */
    public static String apiVersion() {
        String v = System.getenv(VERSION_ENV);
        if (v == null || v.isBlank()) {
            v = System.getProperty(VERSION_ENV, "v1");
        }
        return v;
    }
}
