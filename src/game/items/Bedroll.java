package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.SleepAction;

/**
 * Class representing a bedroll that the Explorer can sleep on.
 * The bedroll must be dropped on the ground before it can be used for sleeping.
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.1
 */
public class Bedroll extends Item {

    /**
     * Constructs a new Bedroll item.
     *
     * Creates a portable bedroll that can be carried by the player and used
     * for sleeping. The bedroll is marked as portable, allowing it to be
     * picked up, dropped, and moved around the game world.
     */
    public Bedroll() {
        super("Bedroll", '=', true); // true means it's portable
    }

    /**
     * Returns the list of actions available for this bedroll at a specific location.
     *
     * The bedroll can only be used for sleeping when it has been dropped on,
     * The method checks the ground type at the bedroll's location and only
     * provides sleeping functionality when placed on appropriate terrain.
     *
     * @param location the Location where this bedroll is currently placed
     * @return ActionList containing SleepAction if on snow, empty list otherwise
     */
    @Override
    public ActionList allowableActions(Location location) {
        ActionList actions = new ActionList();

        // If bedroll is on the ground, allow sleeping
        if (location.getGround().getClass().getSimpleName().equals("Snow")) {
            actions.add(new SleepAction(this));
        }

        return actions;
    }
}
