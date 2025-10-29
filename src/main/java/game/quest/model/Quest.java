package game.quest.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a quest with title, description, objectives, and rewards.
 */
public class Quest {
    private final String id;
    private final String title;
    private final String description;
    private final List<QuestObjective> objectives = new ArrayList<>();
    private final List<QuestReward> rewards = new ArrayList<>();
    private QuestStatus status = QuestStatus.ACTIVE;

    public Quest(String title, String description) {
        this(UUID.randomUUID().toString(), title, description);
    }

    public Quest(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public QuestStatus getStatus() { return status; }
    public List<QuestObjective> getObjectives() { return Collections.unmodifiableList(objectives); }
    public List<QuestReward> getRewards() { return Collections.unmodifiableList(rewards); }

    public Quest addObjective(QuestObjective objective) {
        this.objectives.add(objective);
        return this;
    }

    public Quest addReward(QuestReward reward) {
        this.rewards.add(reward);
        return this;
    }

    public boolean isCompleted() {
        if (objectives.isEmpty()) return false;
        return objectives.stream().allMatch(QuestObjective::isComplete);
    }

    public void markCompleted() {
        if (isCompleted()) {
            status = QuestStatus.COMPLETED;
        }
    }

    public void markClaimed() {
        if (status == QuestStatus.COMPLETED) {
            status = QuestStatus.CLAIMED;
        }
    }
}