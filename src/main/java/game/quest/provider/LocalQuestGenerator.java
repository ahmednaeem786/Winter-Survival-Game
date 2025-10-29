package game.quest.provider;
import game.quest.core.QuestService;
import game.quest.model.ObjectiveType;
import game.quest.model.Quest;
import game.quest.model.QuestObjective;
import game.quest.model.QuestReward;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
/**
 * Placeholder AI-backed quest generator.
 * For now, produces deterministic sample quests across the three types.
 */
public class LocalQuestGenerator implements QuestService {
    private final Random rng;
    public LocalQuestGenerator() {
        this(new Random());
    }
    public LocalQuestGenerator(Random rng) {
        this.rng = rng;
    }
    @Override
    public Quest generateQuest() {
        int pick = rng.nextInt(3);
        switch (pick) {
            case 0: return huntersChallenge();
            case 1: return gatherersTask();
            default: return explorersJourney();
        }
    }
    private Quest huntersChallenge() {
        List<String> creatures = Arrays.asList("Wolf", "Bear", "Deer");
        String target = creatures.get(rng.nextInt(creatures.size()));
        int count = 2 + rng.nextInt(3); // 2..4
        Quest q = new Quest("Hunter's Challenge",
                "Cull the roaming " + target + " population.");
        q.addObjective(new QuestObjective(ObjectiveType.KILL, target, count));
        q.addReward(new QuestReward("Poisoned Axe", "A vicious edge laced with venom."));
        q.addReward(new QuestReward("Enhanced Bow", "Better draw and range."));
        q.addReward(new QuestReward("Special Arrows", "Piercing tips crafted by the Questmaster."));
        return q;
    }
    private Quest gatherersTask() {
        List<String> items = Arrays.asList("YewBerry", "Hazelnut", "Apple");
        String item = items.get(rng.nextInt(items.size()));
        int count = 3 + rng.nextInt(3); // 3..5
        Quest q = new Quest("Gatherer's Task",
                "Collect provisions: " + item + ".");
        q.addObjective(new QuestObjective(ObjectiveType.COLLECT, item, count));
        q.addReward(new QuestReward("Enhanced Bottle", "Greater capacity for precious water."));
        q.addReward(new QuestReward("Magic Bedroll", "Warms even in the coldest tundra."));
        return q;
    }
    private Quest explorersJourney() {
        List<String> route = Arrays.asList("Cave", "Tundra", "Meadow");
        Quest q = new Quest("Explorer's Journey",
                "Walk the ancient path through cave, tundra, and meadow.");
        q.addObjective(new QuestObjective(ObjectiveType.VISIT, "Route", route.size(), route));
        q.addReward(new QuestReward("Advanced TeleportCube", "Attuned to distant ley-lines."));
        q.addReward(new QuestReward("Special Torch", "Burns bright against the storm."));
        return q;
    }
}