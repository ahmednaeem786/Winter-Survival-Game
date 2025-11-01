package game.quest.core;

import game.quest.model.Quest;

/**
 * Service that generates quests using various backends.
 * Implementations may use local algorithms or AI-powered generation.
 *
 * <p>This interface follows the Strategy pattern, allowing runtime selection
 * between different quest generation approaches.
 */
public interface QuestService {
    /**
     * Generates a new contextual quest.
     * Future implementations may consider player and world context.
     *
     * @return a newly generated quest
     */
    Quest generateQuest();
}
