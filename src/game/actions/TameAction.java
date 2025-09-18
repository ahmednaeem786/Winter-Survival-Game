package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.taming.Tameable;

/**
 * Action for taming wild animals using food items.
 *
 * Consumes the food item and establishes a taming relationship if successful.
 * Tamed animals become loyal companions and cannot be attacked.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class TameAction extends Action {

    /**
     * The animal to be tamed.
     */
    private final Tameable target;

    /**
     * The food item used for taming.
     */
    private final Item food;

    /**
     * Creates a taming action with the specified target and food.
     *
     * @param target the animal to tame
     * @param food the food item to use
     */
    public TameAction(Tameable target, Item food) {
        this.target = target;
        this.food = food;
    }

    /**
     * Attempts to tame the target animal using the food item.
     * Removes the food from inventory if successful.
     *
     * @param actor the actor performing the taming
     * @param map the current game map
     * @return message describing the result of the taming attempt
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (target.canBeTamedWith(food)) {
            actor.removeItemFromInventory(food);
            target.tame(actor);
            return actor + " tames " + target + " with " + food;
        }
        return actor + " cannot tame " + target + " with " + food;
    }

    /**
     * Returns the menu description for this taming action.
     *
     * @param actor the actor who would perform the action
     * @return description shown in the action menu
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Tame " + target + " with " + food;
    }

}