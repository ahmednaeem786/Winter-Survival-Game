package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.teleportation.TeleportDestination;

/**
 * Action for teleporting an actor to a destination.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 2.0
 */
public class TeleportAction extends Action {
    private TeleportDestination destination;
    private String teleportType;

    /**
     * Constructor for TeleportAction.
     *
     * @param destination the destination to teleport to
     * @param teleportType the type of teleportation (for display)
     */
    public TeleportAction(TeleportDestination destination, String teleportType) {
        this.destination = destination;
        this.teleportType = teleportType;
    }

    /**
     * Executes the teleportation.
     *
     * @param actor the actor performing the action
     * @param map the map the actor is on
     * @return a description of the action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location currentLocation = map.locationOf(actor);
        Location targetLocation = destination.getTargetLocation();

        // Check if trying to teleport to the same location
        if (currentLocation.equals(targetLocation)) {
            return actor.toString() + " cannot teleport to the same location!";
        }

        // Check if target location contains another actor
        if (targetLocation.containsAnActor()) {
            return actor + " cannot teleport to " + destination.getDescription() +
                    " - location is occupied!";
        }

        // Rest of your teleport logic here...
        map.moveActor(actor, targetLocation);
        return actor.toString() + " teleports to " + destination.getDescription();
    }

    /**
     * Returns a description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return a string description
     */
    @Override
    public String menuDescription(Actor actor) {
        Location targetLocation = destination.getTargetLocation();
        return actor + " uses " + teleportType + " to teleport to ("
                + targetLocation.x() + ", " + targetLocation.y() + ") on "
                + destination.getMapName();
    }
}