package game.quest.core;
import game.quest.model.Quest;
import game.quest.model.QuestObjective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks active and completed quests for a participant.
 * Provides hooks for updating quest progress based on game events.
 *
 * <p>Automatically moves quests to completed status when all objectives
 * are fulfilled. Future versions may add persistence support.
 */
public class QuestTracker {
    private final Map<String, Quest> active = new HashMap<>();
    private final Map<String, Quest> completed = new HashMap<>();

    /**
     * Adds a quest to the active quest list.
     *
     * @param quest the quest to add
     */
    public void add(Quest quest) {
        active.put(quest.getId(), quest);
    }

    /**
     * Returns an unmodifiable list of active quests.
     *
     * @return the list of active quests
     */
    public List<Quest> getActive() {
        return Collections.unmodifiableList(new ArrayList<>(active.values()));
    }

    /**
     * Returns an unmodifiable list of completed quests.
     *
     * @return the list of completed quests
     */
    public List<Quest> getCompleted() {
        return Collections.unmodifiableList(new ArrayList<>(completed.values()));
    }

    /**
     * Records a creature kill and updates relevant quest objectives.
     * Automatically completes quests when all objectives are met.
     *
     * @param creatureName the name of the killed creature
     */
    public void recordKill(String creatureName) {
        for (Quest q : active.values()) {
            for (QuestObjective o : q.getObjectives()) {
                o.recordKill(creatureName);
            }
            if (q.isCompleted()) moveToCompleted(q);
        }
    }

    /**
     * Records item collection and updates relevant quest objectives.
     * Automatically completes quests when all objectives are met.
     *
     * @param itemName the name of the collected item
     * @param amount the quantity collected
     */
    public void recordCollect(String itemName, int amount) {
        for (Quest q : active.values()) {
            for (QuestObjective o : q.getObjectives()) {
                o.recordCollect(itemName, amount);
            }
            if (q.isCompleted()) moveToCompleted(q);
        }
    }

    /**
     * Records a location visit and updates relevant quest objectives.
     * Automatically completes quests when all objectives are met.
     *
     * @param locationKey the identifier of the visited location
     */
    public void recordVisit(String locationKey) {
        for (Quest q : active.values()) {
            for (QuestObjective o : q.getObjectives()) {
                o.recordVisit(locationKey);
            }
            if (q.isCompleted()) moveToCompleted(q);
        }
    }

    /**
     * Moves a quest from active to completed status.
     *
     * @param q the quest to move
     */
    private void moveToCompleted(Quest q) {
        q.markCompleted();
        active.remove(q.getId());
        completed.put(q.getId(), q);
    }
}