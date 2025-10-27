package game.quest;

import edu.monash.fit2099.engine.actors.Actor;
import game.coating.CoatingType;
import game.items.*;

/**
 * Minimal reward distributor mapping abstract rewards to existing concrete items.
 * Keeps behavior simple and dependency-inverted for future expansion.
 */
public class SimpleRewardDistributor implements RewardDistributor {
    @Override
    public String distribute(Quest quest, QuestParticipant participant, Actor asActor) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rewards claimed for ").append(quest.getTitle()).append(':');

        for (QuestReward reward : quest.getRewards()) {
            String name = reward.getName();
            switch (name) {
                case "Poisoned Axe": {
                    Axe axe = new Axe();
                    axe.setCoating(CoatingType.YEWBERRY);
                    asActor.addItemToInventory(axe);
                    sb.append("\n - Poisoned Axe added to inventory");
                    break;
                }
                case "Enhanced Bow": {
                    Bow bow = new Bow();
                    bow.setCoating(CoatingType.SNOW);
                    asActor.addItemToInventory(bow);
                    sb.append("\n - Enhanced Bow added to inventory");
                    break;
                }
                case "Special Arrows": {
                    Bow arrows = new Bow();
                    asActor.addItemToInventory(arrows);
                    sb.append("\n - Special Arrows bundle granted (bow included)");
                    break;
                }
                case "Enhanced Bottle": {
                    // Using normal Bottle for now
                    asActor.addItemToInventory(new Bottle());
                    sb.append("\n - Enhanced Bottle granted");
                    break;
                }
                case "Magic Bedroll": {
                    asActor.addItemToInventory(new Bedroll());
                    sb.append("\n - Magic Bedroll granted");
                    break;
                }
                case "Advanced TeleportCube": {
                    asActor.addItemToInventory(new TeleportCube());
                    sb.append("\n - Advanced TeleportCube granted");
                    break;
                }
                case "Special Torch": {
                    asActor.addItemToInventory(new Torch());
                    sb.append("\n - Special Torch granted");
                    break;
                }
                default: {
                    // Fallback: reward cannot be mapped yet
                    sb.append("\n - Unknown reward: ").append(name);
                }
            }
        }
        return sb.toString();
    }
}

