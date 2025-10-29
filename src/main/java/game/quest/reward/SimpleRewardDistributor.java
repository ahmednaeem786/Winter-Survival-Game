package game.quest.reward;

import edu.monash.fit2099.engine.actors.Actor;
import game.coating.CoatingType;
import game.items.*;
import game.quest.core.QuestParticipant;
import game.quest.model.Quest;
import game.quest.model.QuestReward;

import java.util.Locale;
import java.util.Random;

/**
 * Reward distributor that maps abstract rewards to concrete in-game items.
 * - Performs case-insensitive fuzzy matching for common reward names.
 * - For unknown rewards, grants a sensible random item so players always receive something.
 */
public class SimpleRewardDistributor implements RewardDistributor {
    private static final Random RNG = new Random();

    @Override
    public String distribute(Quest quest, QuestParticipant participant, Actor asActor) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rewards claimed for ").append(quest.getTitle()).append(':');

        for (QuestReward reward : quest.getRewards()) {
            String name = reward.getName() == null ? "" : reward.getName();
            String lower = name.toLowerCase(Locale.ROOT);

            if (containsAny(lower, "poison", "yew")) {
                Axe axe = new Axe();
                axe.setCoating(CoatingType.YEWBERRY);
                asActor.addItemToInventory(axe);
                sb.append("\n - Poisoned Axe granted (yewberry-coated)");
                continue;
            }
            if (containsAny(lower, "frost", "snow", "ice")) {
                Axe axe = new Axe();
                axe.setCoating(CoatingType.SNOW);
                asActor.addItemToInventory(axe);
                sb.append("\n - Snow-Infused Axe granted (frostbite)");
                continue;
            }
            if (lower.contains("axe")) {
                asActor.addItemToInventory(new Axe());
                sb.append("\n - Axe granted");
                continue;
            }
            if (containsAny(lower, "arrow", "arrows", "bow")) {
                Bow bow = new Bow();
                asActor.addItemToInventory(bow);
                sb.append("\n - Bow granted (covers special arrows)");
                continue;
            }
            if (containsAny(lower, "torch", "flame")) {
                asActor.addItemToInventory(new Torch());
                sb.append("\n - Special Torch granted");
                continue;
            }
            if (containsAny(lower, "teleport", "cube", "portal")) {
                asActor.addItemToInventory(new TeleportCube());
                sb.append("\n - Teleport Cube granted");
                continue;
            }
            if (containsAny(lower, "bottle", "flask", "canteen")) {
                asActor.addItemToInventory(new Bottle());
                sb.append("\n - Enhanced Bottle granted");
                continue;
            }
            if (containsAny(lower, "bedroll", "bed", "sleep")) {
                asActor.addItemToInventory(new Bedroll());
                sb.append("\n - Magic Bedroll granted");
                continue;
            }

            // Unknown reward: grant a sensible random item to "mix" novel names with in-game items
            sb.append(mapUnknown(asActor, name));
        }
        return sb.toString();
    }

    private static boolean containsAny(String haystack, String... needles) {
        for (String n : needles) if (haystack.contains(n)) return true;
        return false;
    }

    private static String mapUnknown(Actor asActor, String originalName) {
        int pick = RNG.nextInt(6);
        switch (pick) {
            case 0: {
                Axe axe = new Axe();
                axe.setCoating(CoatingType.YEWBERRY);
                asActor.addItemToInventory(axe);
                return "\n - Unknown reward ('" + originalName + "'): granted Poisoned Axe";
            }
            case 1: {
                Axe axe = new Axe();
                axe.setCoating(CoatingType.SNOW);
                asActor.addItemToInventory(axe);
                return "\n - Unknown reward ('" + originalName + "'): granted Snow-Infused Axe";
            }
            case 2: {
                asActor.addItemToInventory(new Bow());
                return "\n - Unknown reward ('" + originalName + "'): granted Bow";
            }
            case 3: {
                asActor.addItemToInventory(new Torch());
                return "\n - Unknown reward ('" + originalName + "'): granted Torch";
            }
            case 4: {
                asActor.addItemToInventory(new TeleportCube());
                return "\n - Unknown reward ('" + originalName + "'): granted Teleport Cube";
            }
            case 5: default: {
                asActor.addItemToInventory(new Bottle());
                return "\n - Unknown reward ('" + originalName + "'): granted Bottle";
            }
        }
    }
}


