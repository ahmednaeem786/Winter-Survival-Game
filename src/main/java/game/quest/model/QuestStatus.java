package game.quest.model;

/**
 * Status of a quest in its lifecycle.
 * Quests progress from ACTIVE to COMPLETED to CLAIMED.
 */
public enum QuestStatus {
    /** Quest is active and objectives are being pursued. */
    ACTIVE,

    /** Quest objectives are complete but rewards not yet claimed. */
    COMPLETED,

    /** Quest rewards have been claimed. */
    CLAIMED
}
