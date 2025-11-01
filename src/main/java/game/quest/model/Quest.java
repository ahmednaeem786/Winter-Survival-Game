package game.quest.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a quest with title, description, objectives, and rewards.
 * Tracks completion status and provides builder-style methods for configuration.
 *
 * <p>A quest is considered completed when all its objectives are fulfilled.
 * Rewards can only be claimed once the quest is in COMPLETED status.
 */
public class Quest {
    private final String id;
    private final String title;
    private final String description;
    private final List<QuestObjective> objectives = new ArrayList<>();
    private final List<QuestReward> rewards = new ArrayList<>();
    private QuestStatus status = QuestStatus.ACTIVE;

    /**
     * Creates a new quest with a randomly generated ID.
     *
     * @param title the quest title
     * @param description the quest description
     */
    public Quest(String title, String description) {
        this(UUID.randomUUID().toString(), title, description);
    }

    /**
     * Creates a new quest with a specified ID.
     *
     * @param id the unique quest identifier
     * @param title the quest title
     * @param description the quest description
     */
    public Quest(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public QuestStatus getStatus() { return status; }

    /**
     * Returns an unmodifiable list of quest objectives.
     *
     * @return the list of objectives
     */
    public List<QuestObjective> getObjectives() { return Collections.unmodifiableList(objectives); }

    /**
     * Returns an unmodifiable list of quest rewards.
     *
     * @return the list of rewards
     */
    public List<QuestReward> getRewards() { return Collections.unmodifiableList(rewards); }

    /**
     * Adds an objective to this quest.
     *
     * @param objective the objective to add
     * @return this quest for method chaining
     */
    public Quest addObjective(QuestObjective objective) {
        this.objectives.add(objective);
        return this;
    }

    /**
     * Adds a reward to this quest.
     *
     * @param reward the reward to add
     * @return this quest for method chaining
     */
    public Quest addReward(QuestReward reward) {
        this.rewards.add(reward);
        return this;
    }

    /**
     * Checks if all objectives are complete.
     *
     * @return true if all objectives are fulfilled, false otherwise
     */
    public boolean isCompleted() {
        if (objectives.isEmpty()) return false;
        return objectives.stream().allMatch(QuestObjective::isComplete);
    }

    /**
     * Marks this quest as completed if all objectives are fulfilled.
     * Updates status to COMPLETED.
     */
    public void markCompleted() {
        if (isCompleted()) {
            status = QuestStatus.COMPLETED;
        }
    }

    /**
     * Marks this quest as claimed after rewards have been distributed.
     * Can only be called when status is COMPLETED.
     */
    public void markClaimed() {
        if (status == QuestStatus.COMPLETED) {
            status = QuestStatus.CLAIMED;
        }
    }
}