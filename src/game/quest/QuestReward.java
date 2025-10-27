package game.quest;
/**
 * Represents a reward granted upon quest completion.
 * For now, this is a descriptive placeholder; wiring to concrete items happens later.
 */
public class QuestReward {
    private final String name;
    private final String description;
    public QuestReward(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    @Override
    public String toString() {
        return name + ": " + description;
    }
}
