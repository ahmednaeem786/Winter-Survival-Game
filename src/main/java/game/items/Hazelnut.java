package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.items.PickUpAction;
import game.actions.TrackedPickUpAction;

/**
 * Class representing a Hazelnut item.
 * Hazelnuts can be consumed to increase maximum health by 1.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 *
 */
public class Hazelnut extends ConsumableItem {

    /**
     * Constructor for Hazelnut.
     */
    public Hazelnut() {
        super("Hazelnut", 'n', true);
    }

    @Override
    public PickUpAction getPickUpAction(Actor actor) {
        return new TrackedPickUpAction(this);
    }

    /**
     * Consumes the hazelnut to increase maximum health.
     */
    @Override
    public String consume(Actor actor, GameMap map) {
        // Hazelnut: increase maximum health by 1
        int beforeMax = actor.getMaximumAttribute(BaseAttributes.HEALTH);

        // Increase the maximum health by 1
        actor.modifyStatsMaximum(BaseAttributes.HEALTH, ActorAttributeOperation.INCREASE, 1);

        int afterMax = actor.getMaximumAttribute(BaseAttributes.HEALTH);

        return actor + " eats the hazelnut and feels stronger! (Max Health: "
                + beforeMax + " → " + afterMax + ")";
    }
}
