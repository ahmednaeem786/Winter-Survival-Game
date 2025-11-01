package game.quest;

import game.quest.core.QuestTracker;
import game.quest.model.ObjectiveType;
import game.quest.model.Quest;
import game.quest.model.QuestObjective;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests KILL and COLLECT objective progress (typical, boundary, edge conditions).
 */
public class KillAndCollectProgressTest {
    @Test
    void killProgressTypicalAndExcess() {
        Quest q = new Quest("Hunt", "Cull wolves");
        q.addObjective(new QuestObjective(ObjectiveType.KILL, "Wolf", 3));
        QuestTracker t = new QuestTracker();
        t.add(q);

        t.recordKill("Wolf");
        t.recordKill("Bear"); // wrong species ignored
        t.recordKill("Wolf");
        assertFalse(q.isCompleted());
        t.recordKill("Wolf");
        assertTrue(q.isCompleted());
        t.recordKill("Wolf"); // extra shouldn't break
        assertTrue(q.isCompleted());
    }

    @Test
    void collectProgressBoundaryAndCap() {
        Quest q = new Quest("Gather", "Pick nuts");
        q.addObjective(new QuestObjective(ObjectiveType.COLLECT, "Hazelnut", 5));
        QuestTracker t = new QuestTracker();
        t.add(q);

        t.recordCollect("Hazelnut", 0); // zero ignored
        assertFalse(q.isCompleted());
        t.recordCollect("Hazelnut", 3); // partial
        assertFalse(q.isCompleted());
        t.recordCollect("Hazelnut", 10); // capped to 5
        assertTrue(q.isCompleted());
    }
}
