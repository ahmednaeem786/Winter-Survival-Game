package game.quest.provider;

import game.quest.GeminiQuestGenerator;
import game.quest.core.QuestService;
import game.quest.api.ApiConfig;
import game.quest.api.HttpGeminiClient;

/**
 * Factory that returns a QuestService implementation based on configuration.
 * Implements the Factory Method pattern for runtime strategy selection.
 *
 * <p>Uses Gemini AI when API key is present, otherwise falls back to local generator.
 * This allows seamless switching between generation methods without code changes.
 */
public final class QuestServiceFactory {
    private QuestServiceFactory() {}

    /**
     * Creates the appropriate quest service based on API configuration.
     *
     * @return a GeminiQuestGenerator if API is configured, otherwise LocalQuestGenerator
     */
    public static QuestService create() {
        if (ApiConfig.isConfigured()) {
            return new GeminiQuestGenerator(new HttpGeminiClient());
        }
        return new LocalQuestGenerator();
    }
}