package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.teleportation.TeleportDestination;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A mysterious cube that can teleport the holder.
 * Has a 50% chance to malfunction and teleport to random location.
 * Can only be used when held in inventory.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.8
 */
public class TeleportCube extends Item {
    private List<TeleportDestination> destinations;
    private static final Random random = new Random();

    /**
     * Constructor for TeleportCube.
     */
    public TeleportCube() {
        super("Teleport Cube", '□', true);
        this.destinations = new ArrayList<>();
    }

    /**
     * Adds a destination to this cube.
     *
     * @param destination the destination to add
     */
    public void addDestination(TeleportDestination destination) {
        destinations.add(destination);
    }

    /**
     * Returns actions available for this cube when in inventory.
     *
     * @param owner the actor holding this item
     * @param map the current game map
     * @return list of available actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        // Only allow teleportation if in inventory
        if (owner.getItemInventory().contains(this)) {
            for (TeleportDestination destination : destinations) {
                actions.add(new CubeTeleportAction(destination, map));
            }
        }

        return actions;
    }

    /**
     * Inner class for Cube teleportation with 50% malfunction chance.
     */
    private class CubeTeleportAction extends TeleportAction {
        private GameMap currentMap;

        public CubeTeleportAction(TeleportDestination destination, GameMap map) {
            super(destination, "Teleport Cube");
            this.currentMap = map;
        }

        @Override
        public String execute(Actor actor, GameMap map) {
            // 50% chance to malfunction
            if (random.nextBoolean()) {
                // Malfunction - teleport to random location on current map
                Location randomLocation = getRandomLocation(map, actor);
                map.moveActor(actor, randomLocation);
                return actor + " uses the Teleport Cube but it MALFUNCTIONS!" +
                        "\nTeleported to random location (" +
                        randomLocation.x() + ", " + randomLocation.y() + ")";
            } else {
                // Normal teleportation
                return super.execute(actor, map);
            }
        }

        /**
         * Gets a random valid location on the map.
         *
         * @param map the game map
         * @param actor the actor to check if they can enter
         * @return a random location
         */
        private Location getRandomLocation(GameMap map, Actor actor) {
            int maxRetries = 100;
            int attempts = 0;

            while (attempts < maxRetries) {
                int x = random.nextInt(map.getXRange().max());
                int y = random.nextInt(map.getYRange().max());
                Location loc = map.at(x, y);

                // Check if location is valid (actor can enter)
                if (loc.canActorEnter(actor)) {
                    return loc;
                }
                attempts++;
            }

            // Fallback: return current location if no valid location found
            // Find actor's current location
            for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
                for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                    Location loc = map.at(x, y);
                    if (loc.containsAnActor() && loc.getActor() == actor) {
                        return loc;
                    }
                }
            }

            return map.at(0, 0);
        }
    }
}