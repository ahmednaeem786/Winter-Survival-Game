package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.utils.HydrationUtils;
import edu.monash.fit2099.engine.items.PickUpAction;
import game.actions.TrackedPickUpAction;

/**
 * Class representing an Apple item.
 * Apples can be consumed to heal 3 HP and reduce hydration need by 2.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.1
 */
public class Apple extends ConsumableItem {

    /**
     * Constructor for Apple.
     */
    public Apple() {
        super("Apple", 'a', true);
    }

    @Override
    public PickUpAction getPickUpAction(Actor actor) {
        return new TrackedPickUpAction(this);
    }

    /**
     * Consumes the apple with healing and hydration effects.
     */
    @Override
    public String consume(Actor actor, GameMap map) {
        actor.heal(3);
        // Restore 2 hydration
        HydrationUtils.increaseHydration(actor, 2);
        return actor + " eats the apple and feels refreshed! (+3 HP, +2 Hydration)";
    }
}
