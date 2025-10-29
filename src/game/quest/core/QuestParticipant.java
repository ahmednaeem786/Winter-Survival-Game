package game.quest.core;

/**
 * Marker interface for actors that can participate in quests.
 * Exposes access to their {@link QuestTracker} without relying on instanceof.
 */
public interface QuestParticipant {
    QuestTracker getQuestTracker();
}