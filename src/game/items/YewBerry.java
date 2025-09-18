package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Class representing a Yew Berry item.
 * Yew berries are deadly poisonous and will kill the Explorer immediately.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class YewBerry extends ConsumableItem {

    /**
     * Constructor for YewBerry.
     */
    public YewBerry() {
        super("Yew Berry", 'x', true);
    }

    /**
     * Consumes the yew berry with deadly consequences.
     */
    @Override
    public String consume(Actor actor, GameMap map) {
        // Yew Berry: instant death
        actor.modifyAttribute(BaseAttributes.HEALTH, ActorAttributeOperation.DECREASE,
                actor.getAttribute(BaseAttributes.HEALTH));

        // Remove the actor from the map (end the game)
        map.removeActor(actor);
        return actor + " eats the yew berry and collapses! The berry was deadly poisonous!";
    }
}