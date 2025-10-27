package game.quest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/**
 * Represents a single objective within a quest, such as killing a type of creature,
 * collecting items, or visiting a set of locations.
 */
public class QuestObjective {
    private final ObjectiveType type;
    private final String target; // e.g. "Wolf", "Berry", or a location key like "Cave"
    private final int requiredAmount; // for KILL/COLLECT counts; for VISIT this is number of locations
    private int progress;
    private final List<String> orderedLocations; // used for VISIT objective in order
    private int visitIndex; // current index in orderedLocations for VISIT
    public QuestObjective(ObjectiveType type, String target, int requiredAmount) {
        this(type, target, requiredAmount, new ArrayList<>());
    }
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
    public List<String> getOrderedLocations() { return Collections.unmodifiableList(orderedLocations); }
    public boolean isComplete() {
        if (type == ObjectiveType.VISIT) {
            return visitIndex >= orderedLocations.size() && !orderedLocations.isEmpty();
        }
        return progress >= requiredAmount;
    }
    public void recordKill(String creatureName) {
        if (type == ObjectiveType.KILL && target.equalsIgnoreCase(creatureName) && progress < requiredAmount) {
            progress += 1;
        }
    }
    public void recordCollect(String itemName, int amount) {
        if (type == ObjectiveType.COLLECT && target.equalsIgnoreCase(itemName) && progress < requiredAmount) {
            progress = Math.min(requiredAmount, progress + Math.max(0, amount));
        }
    }
    public void recordVisit(String locationKey) {
        if (type != ObjectiveType.VISIT || orderedLocations.isEmpty()) return;
        if (visitIndex < orderedLocations.size() && orderedLocations.get(visitIndex).equalsIgnoreCase(locationKey)) {
            visitIndex += 1;
        }
    }
    public String progressText() {
        switch (type) {
            case KILL:
            case COLLECT:
                return String.format("%s %d/%d", target, progress, requiredAmount);
            case VISIT:
                int total = Math.max(orderedLocations.size(), requiredAmount);
                return String.format("Visited %d/%d: %s", Math.min(visitIndex, total), total, orderedLocations.toString());
            default:
                return "";
        }
    }
}