package game.quest.core;

/**
 * Marker interface for actors that can participate in quests.
 * Provides access to quest tracking without relying on instanceof checks.
 *
 * <p>Actors implementing this interface can accept, track, and complete quests.
 * This design decouples the quest system from the Actor hierarchy.
 */
public interface QuestParticipant {
    /**
     * Returns the quest tracker for this participant.
     *
     * @return the quest tracker managing this participant's quests
     */
    QuestTracker getQuestTracker();
}