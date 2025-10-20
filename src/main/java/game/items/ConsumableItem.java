package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;

/**
 * Abstract base class for all consumable items.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.1
 */
public abstract class ConsumableItem extends Item {

    /**
     * Constructor for ConsumableItem.
     */
    public ConsumableItem(String name, char displayChar, boolean portable) {
        super(name, displayChar, portable);
    }

    /**
     * Returns the list of allowable actions for this consumable item.
     * Allows the actor to consume the item.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (owner.getItemInventory().contains(this)) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

    /**
     * Abstract method that defines what happens when this item is consumed.
     * Each consumable item must implement its own consumption effects.
     *
     * @param actor the actor consuming the item
     * @param map   the current game map
     * @return description of what happened
     */
    public abstract String consume(Actor actor, GameMap map);
}