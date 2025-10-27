package game.quest;
/**
 * Service that can generate quests, using an AI backend.
 */
public interface QuestService {
    /**
     * Generate a new contextual quest. Implementation may use player and world context later.
     */
    Quest generateQuest();
}
