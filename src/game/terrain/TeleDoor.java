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

/**
 * A door that allows teleportation between and within maps.
 * Using the door burns the surroundings at the destination.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.9
 */
public class TeleDoor extends Ground {
    private List<TeleportDestination> destinations;

    /**
     * Constructor for TeleDoor.
     */
    public TeleDoor() {
        super('#', "Tele-door");
        this.destinations = new ArrayList<>();
    }

    /**
     * Adds a destination to this door.
     *
     * @param destination the destination to add
     */
    public void addDestination(TeleportDestination destination) {
        destinations.add(destination);
    }

    /**
     * Returns actions available at this door.
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
            actions.add(new TeleDoorTeleportAction(destination));
        }

        return actions;
    }

    /**
     * Inner class for TeleDoor teleportation that burns destination surroundings.
     */
    private class TeleDoorTeleportAction extends TeleportAction {
        private TeleportDestination dest;

        public TeleDoorTeleportAction(TeleportDestination destination) {
            super(destination, "Tele-door");
            this.dest = destination;
        }

        @Override
        public String execute(Actor actor, GameMap map) {
            String result = super.execute(actor, map);

            // Burn surroundings at destination
            Location destinationLocation = dest.getTargetLocation();
            burnSurroundings(destinationLocation);

            return result + " (Destination surroundings ignite!)";
        }

        /**
         * Burns all adjacent locations to the given location.
         *
         * @param center the center location
         */
        private void burnSurroundings(Location center) {
            var exits = center.getExits();
            for (var exit : exits) {
                Location adjacentLocation = exit.getDestination();
                if (!adjacentLocation.containsAnActor() &&
                        !(adjacentLocation.getGround() instanceof Fire)) {
                    adjacentLocation.setGround(new Fire());
                }
            }
        }
    }
}