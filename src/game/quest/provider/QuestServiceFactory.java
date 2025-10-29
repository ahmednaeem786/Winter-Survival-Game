package game.quest.provider;

import game.quest.GeminiQuestGenerator;
import game.quest.QuestService;
import game.quest.api.ApiConfig;
import game.quest.api.HttpGeminiClient;

/**
 * Factory that returns a QuestService implementation based on configuration.
 * Uses Gemini when API key is present, otherwise falls back to local generator.
 */
public final class QuestServiceFactory {
    private QuestServiceFactory() {}

    public static QuestService create() {
        if (ApiConfig.isConfigured()) {
            return new GeminiQuestGenerator(new HttpGeminiClient());
        }
        return new LocalQuestGenerator();
    }
}