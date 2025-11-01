package game.quest.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single objective within a quest.
 * Supports kill, collection, and sequential visit objectives.
 *
 * <p>Each objective tracks its own progress and determines when it's complete
 * based on its type and requirements.
 */
public class QuestObjective {
    private final ObjectiveType type;
    private final String target; // e.g. "Wolf", "Berry", or a location key like "Cave"
    private final int requiredAmount; // for KILL/COLLECT counts; for VISIT this is number of locations
    private int progress;
    private final List<String> orderedLocations; // used for VISIT objective in order

    private int visitIndex; // current index in orderedLocations for VISIT

    /**
     * Creates a quest objective without ordered locations.
     *
     * @param type the objective type
     * @param target the target (creature name, item name, or location key)
     * @param requiredAmount the required count or number of locations
     */
    public QuestObjective(ObjectiveType type, String target, int requiredAmount) {
        this(type, target, requiredAmount, new ArrayList<>());
    }

    /**
     * Creates a quest objective with ordered locations for VISIT type.
     *
     * @param type the objective type
     * @param target the target description
     * @param requiredAmount the required count
     * @param locationsInOrder the ordered list of locations to visit
     */
    public QuestObjective(ObjectiveType type, String target, int requiredAmount, List<String> locationsInOrder) {
        this.type = Objects.requireNonNull(type);
        this.target = Objects.requireNonNull(target);
        this.requiredAmount = Math.max(1, requiredAmount);
        this.progress = 0;
        this.orderedLocations = new ArrayList<>(locationsInOrder);
        this.visitIndex = 0;
    }

    public ObjectiveType getType() { return type; }
    public String getTarget() { return target; }
    public int getRequiredAmount() { return requiredAmount; }
    public int getProgress() { return progress; }

    /**
     * Returns the ordered list of locations for VISIT objectives.
     *
     * @return unmodifiable list of location identifiers
     */
    public List<String> getOrderedLocations() { return Collections.unmodifiableList(orderedLocations); }

    /**
     * Checks if this objective is complete.
     *
     * @return true if the objective requirements are met
     */
    public boolean isComplete() {
        if (type == ObjectiveType.VISIT) {
            return visitIndex >= orderedLocations.size() && !orderedLocations.isEmpty();
        }
        return progress >= requiredAmount;
    }

    /**
     * Records a creature kill and updates progress if applicable.
     *
     * @param creatureName the name of the killed creature
     */
    public void recordKill(String creatureName) {
        if (type == ObjectiveType.KILL && target.equalsIgnoreCase(creatureName) && progress < requiredAmount) {
            progress += 1;
        }
    }

    /**
     * Records item collection and updates progress if applicable.
     *
     * @param itemName the name of the collected item
     * @param amount the quantity collected
     */
    public void recordCollect(String itemName, int amount) {
        if (type == ObjectiveType.COLLECT && target.equalsIgnoreCase(itemName) && progress < requiredAmount) {
            progress = Math.min(requiredAmount, progress + Math.max(0, amount));
        }
    }

    /**
     * Records a location visit and updates progress if it matches the next expected location.
     *
     * @param locationKey the identifier of the visited location
     */
    public void recordVisit(String locationKey) {
        if (type != ObjectiveType.VISIT || orderedLocations.isEmpty()) return;
        if (visitIndex < orderedLocations.size() && orderedLocations.get(visitIndex).equalsIgnoreCase(locationKey)) {
            visitIndex += 1;
        }
    }

    /**
     * Returns a human-readable progress string for this objective.
     *
     * @return formatted progress text showing current status
     */
    public String progressText() {
        switch (type) {
            case KILL:
            case COLLECT:
                return String.format("%s %d/%d", target, progress, requiredAmount);
            case VISIT:
                int total = Math.max(orderedLocations.size(), requiredAmount);
                int done = Math.min(visitIndex, total);
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("Visited %d/%d", done, total));
                for (int i = 0; i < orderedLocations.size(); i++) {
                    String step = orderedLocations.get(i);
                    String mark = (i < visitIndex) ? "[x]" : (i == visitIndex ? "[>]" : "[ ]");
                    sb.append("\n      - ").append(step).append(" ").append(mark);
                }
                return sb.toString();
            default:
                return "";
        }
    }
}