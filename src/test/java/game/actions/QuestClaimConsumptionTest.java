package game.actions;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actors.Player;
import game.actors.Questmaster;
import game.items.YewBerry;
import game.quest.provider.LocalQuestGenerator;
import game.quest.model.ObjectiveType;
import game.quest.model.Quest;
import game.quest.model.QuestObjective;
import game.quest.core.QuestTracker;
import game.testing.TestFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ensures collected items are consumed on claim (exact required amount).
 */
public class QuestClaimConsumptionTest {

    @Test
    void consumeCollectedItemsOnClaim() throws GameEngineException {
        GameMap map = TestFactory.createSnowMap("Test",
                "..........",
                "..........",
                "..........");

        // Attach map to a World so engine internals (actorLocations) are initialised
        World testWorld = new World(new Display()) { };
        testWorld.addGameMap(map);

        Player player = new Player("Tester", '@', 100);
        map.addActor(player, map.at(1,1));

        Quest q = new Quest("Gather", "Collect berries");
        q.addObjective(new QuestObjective(ObjectiveType.COLLECT, "YewBerry", 3));
        QuestTracker tracker = player.getQuestTracker();
        tracker.add(q);

        for (int i = 0; i < 5; i++) player.addItemToInventory(new YewBerry());

        tracker.recordCollect("YewBerry", 3);
        assertTrue(q.isCompleted());

        String msg = new QuestAction(new Questmaster(), new LocalQuestGenerator()).execute(player, map);
        assertNotNull(msg);

        long remaining = player.getItemInventory().stream()
                .filter(i -> i.getClass().getSimpleName().equals("YewBerry")).count();
        assertEquals(2, remaining);
    }
}


