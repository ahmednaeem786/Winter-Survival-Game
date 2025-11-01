package game.quest.model;

/**
 * Represents a reward granted upon quest completion.
 * Currently a descriptive placeholder; concrete item wiring happens in the distributor.
 *
 * <p>Future versions may include metadata like rarity, value, or item class references.
 */
public class QuestReward {
    private final String name;
    private final String description;

    /**
     * Creates a quest reward.
     *
     * @param name the reward name
     * @param description the reward description
     */
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
