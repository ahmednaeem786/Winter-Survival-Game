package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.ConsumableItem;

/**
 * Action for consuming food items like apples, hazelnuts, and yew berries.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.1
 */
public class ConsumeAction extends Action {
    private ConsumableItem item;

    /**
     * Constructor for ConsumeAction.
     *
     * @param item the consumable item to consume
     */
    public ConsumeAction(ConsumableItem item) {
        this.item = item;
    }

    /**
     * Executes the consume action.
     *
     * @param actor the actor consuming the item
     * @param map   the current game map
     * @return description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Remove the item from actor's inventory
        actor.removeItemFromInventory(item);

        return item.consume(actor, map);
    }

    /**
     * Returns a description of this action suitable for displaying in a menu.
     *
     * @param actor the actor that will perform the action
     * @return a string describing the action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " consumes " + item;
    }
}