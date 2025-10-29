package game.quest.api;

/**
 * Loads API configuration from environment variables or system properties.
 * - GEMINI_API_KEY: required for live calls
 * - GEMINI_MODEL: optional, defaults to a sensible public model
 */
public final class ApiConfig {
    private static final String API_KEY_ENV = "GEMINI_API_KEY";
    private static final String MODEL_ENV = "GEMINI_MODEL";
    private static final String VERSION_ENV = "GEMINI_API_VERSION";

    private ApiConfig() {}

    public static String apiKey() {
        String v = System.getenv(API_KEY_ENV);
        if (v == null || v.isBlank()) {
            v = System.getProperty(API_KEY_ENV, "");
        }
        return v;
    }

    public static String model() {
        String v = System.getenv(MODEL_ENV);
        if (v == null || v.isBlank()) {
            v = System.getProperty(MODEL_ENV, "gemini-2.5-flash");
        }
        return v;
    }

    public static boolean isConfigured() {
        return !apiKey().isBlank();
    }

    public static String apiVersion() {
        String v = System.getenv(VERSION_ENV);
        if (v == null || v.isBlank()) {
            v = System.getProperty(VERSION_ENV, "v1");
        }
        return v;
    }
}
