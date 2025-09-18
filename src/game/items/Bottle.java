package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.DrinkAction;

/**
 * Class representing a portable water bottle that the Explorer can drink from to restore hydration.
 *
 * The bottle is a consumable item with limited uses that helps maintain the player's survival
 * in the winter environment. Each use of the bottle restores 4 hydration points
 * and can be carried in the player's inventory.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.8
 */
public class Bottle extends Item {

    /**
     * The number of times this bottle can still be used for drinking.
     * Starts at 5 uses and decrements with each drink action.
     */
    private int usesRemaining;

    /**
     * The maximum number of times a bottle can be used.
     * This constant defines the initial value for usesRemaining.
     */
    private static final int MAX_USES = 5;

    /**
     * Constructs a new Bottle with maximum uses available.
     *
     * Creates a portable bottle item that can be picked up, carried, and used
     * by the player. The bottle starts with 5 uses available.
     */
    public Bottle() {
        super("Bottle", 'o', true);
        this.usesRemaining = MAX_USES;
    }

    /**
     * Returns the list of actions that can be performed with this bottle.
     *
     * The bottle allows drinking if it has uses remaining. Once empty,
     * no drink actions are available, but the bottle can still be dropped.
     *
     * @param owner the Actor who owns this bottle
     * @param map the GameMap where the bottle is located
     * @return ActionList containing available actions for this bottle
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        if (usesRemaining > 0) {
            actions.add(new DrinkAction(this));
        }

        return actions;
    }

    /**
     * Consumes one use of the bottle.
     *
     * This method is called when the player drinks from the bottle.
     *
     * @return true if the bottle was successfully used (had uses remaining),
     *         false if the bottle is empty and cannot be used
     */
    public boolean use() {
        if (usesRemaining > 0) {
            usesRemaining--;
            return true;
        }
        return false;
    }

    /**
     * Gets the number of uses remaining for this bottle.
     *
     * @return the number of times this bottle can still be used for drinking
     */
    public int getUsesRemaining() {
        return usesRemaining;
    }

    /**
     * Returns a string representation of the bottle including usage information.
     *
     * @return a string in the format "Bottle (X uses left)" where X is the remaining uses
     */
    @Override
    public String toString() {
        return super.toString() + " (" + usesRemaining + " uses left)";
    }
}
