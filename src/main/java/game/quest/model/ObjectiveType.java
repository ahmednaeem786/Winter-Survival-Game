package game.quest.model;

/**
 * Types of quest objectives supported by the system.
 * Each type requires different tracking mechanisms and completion criteria.
 */
public enum ObjectiveType {
    /** Objective to kill a specified number of creatures. */
    KILL,

    /** Objective to collect a specified quantity of items. */
    COLLECT,

    /** Objective to visit locations in a specific order. */
    VISIT
}