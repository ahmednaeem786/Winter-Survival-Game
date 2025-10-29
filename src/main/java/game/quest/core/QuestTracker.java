package game.quest.core;
import game.quest.model.Quest;
import game.quest.model.QuestObjective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Tracks active and completed quests and provides simple progress update hooks.
 * Persistence can be introduced later via serialization.
 */
public class QuestTracker {
    private final Map<String, Quest> active = new HashMap<>();
    private final Map<String, Quest> completed = new HashMap<>();
    public void add(Quest quest) {
        active.put(quest.getId(), quest);
    }
    public List<Quest> getActive() {
        return Collections.unmodifiableList(new ArrayList<>(active.values()));
    }
    public List<Quest> getCompleted() {
        return Collections.unmodifiableList(new ArrayList<>(completed.values()));
    }
    public void recordKill(String creatureName) {
        for (Quest q : active.values()) {
            for (QuestObjective o : q.getObjectives()) {
                o.recordKill(creatureName);
            }
            if (q.isCompleted()) moveToCompleted(q);
        }
    }
    public void recordCollect(String itemName, int amount) {
        for (Quest q : active.values()) {
            for (QuestObjective o : q.getObjectives()) {
                o.recordCollect(itemName, amount);
            }
            if (q.isCompleted()) moveToCompleted(q);
        }
    }
    public void recordVisit(String locationKey) {
        for (Quest q : active.values()) {
            for (QuestObjective o : q.getObjectives()) {
                o.recordVisit(locationKey);
            }
            if (q.isCompleted()) moveToCompleted(q);
        }
    }
    private void moveToCompleted(Quest q) {
        q.markCompleted();
        active.remove(q.getId());
        completed.put(q.getId(), q);
    }
}