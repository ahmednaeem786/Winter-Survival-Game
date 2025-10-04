package game.terrain;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.teleportation.TeleportDestination;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A teleportation circle drawn on the ground.
 * Using the circle burns one random surrounding location at the SOURCE.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.3
 */
public class TeleportationCircle extends Ground {
    private List<TeleportDestination> destinations;
    private static final Random random = new Random();

    /**
     * Constructor for TeleportationCircle.
     */
    public TeleportationCircle() {
        super('O', "Teleportation Circle");
        this.destinations = new ArrayList<>();
    }

    /**
     * Adds a destination to this circle.
     *
     * @param destination the destination to add
     */
    public void addDestination(TeleportDestination destination) {
        destinations.add(destination);
    }

    /**
     * Returns actions available at this circle.
     *
     * @param actor the actor at this location
     * @param location the current location
     * @param direction the direction (unused)
     * @return list of available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        for (TeleportDestination destination : destinations) {
            actions.add(new CircleTeleportAction(destination, location));
        }

        return actions;
    }

    /**
     * Inner class for Circle teleportation that burns source surroundings.
     */
    private class CircleTeleportAction extends TeleportAction {
        private Location sourceLocation;

        public CircleTeleportAction(TeleportDestination destination, Location source) {
            super(destination, "Teleportation Circle");
            this.sourceLocation = source;
        }

        @Override
        public String execute(Actor actor, GameMap map) {
            // Burn one random surrounding at source BEFORE teleporting
            String burnResult = burnRandomSurrounding(sourceLocation);

            String result = super.execute(actor, map);

            return result + " " + burnResult;
        }

        /**
         * Burns one random adjacent location to the given location.
         *
         * @param center the center location
         * @return description of what burned
         */
        private String burnRandomSurrounding(Location center) {
            var exits = center.getExits();
            List<Location> validLocations = new ArrayList<>();

            // Find all valid locations to burn
            for (var exit : exits) {
                Location adjacentLocation = exit.getDestination();
                if (!adjacentLocation.containsAnActor() &&
                        !(adjacentLocation.getGround() instanceof Fire)) {
                    validLocations.add(adjacentLocation);
                }
            }

            // Burn one random location if any valid
            if (!validLocations.isEmpty()) {
                Location toBurn = validLocations.get(random.nextInt(validLocations.size()));
                toBurn.setGround(new Fire());
                return "(Source location catches fire!)";
            }

            return "(No surrounding location could be burned)";
        }
    }
}