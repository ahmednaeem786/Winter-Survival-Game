package game.quest;

import game.quest.core.QuestTracker;
import game.quest.model.ObjectiveType;
import game.quest.model.Quest;
import game.quest.model.QuestObjective;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests VISIT objective progression for ordered, out-of-order, and duplicate inputs.
 */
public class VisitProgressTest {

    @Test
    void orderedVisitsAdvanceAndComplete() {
        Quest q = new Quest("Explorer", "Visit places");
        q.addObjective(new QuestObjective(ObjectiveType.VISIT, "Route", 3,
                Arrays.asList("Cave", "Tundra", "Meadow")));
        QuestTracker t = new QuestTracker();
        t.add(q);

        t.recordVisit("Cave");
        assertFalse(q.isCompleted());
        t.recordVisit("Tundra");
        assertFalse(q.isCompleted());
        t.recordVisit("Meadow");
        assertTrue(q.isCompleted());
    }

    @Test
    void outOfOrderDoesNotAdvance() {
        Quest q = new Quest("Explorer", "Visit places");
        q.addObjective(new QuestObjective(ObjectiveType.VISIT, "Route", 3,
                Arrays.asList("Cave", "Tundra", "Meadow")));
        QuestTracker t = new QuestTracker();
        t.add(q);

        t.recordVisit("Tundra"); // ignored (first must be Cave)
        assertFalse(q.isCompleted());
        t.recordVisit("Cave");
        t.recordVisit("Meadow"); // still needs Tundra next
        assertFalse(q.isCompleted());
        t.recordVisit("Tundra");
        assertFalse(q.isCompleted());
        t.recordVisit("Meadow");
        assertTrue(q.isCompleted());
    }

    @Test
    void duplicateVisitDoesNotOverAdvance() {
        Quest q = new Quest("Explorer", "Visit places");
        q.addObjective(new QuestObjective(ObjectiveType.VISIT, "Route", 3,
                Arrays.asList("Cave", "Tundra", "Meadow")));
        QuestTracker t = new QuestTracker();
        t.add(q);

        t.recordVisit("Cave");
        t.recordVisit("Cave"); // duplicate ignored
        t.recordVisit("Tundra");
        t.recordVisit("Meadow");
        assertTrue(q.isCompleted());
    }
}

