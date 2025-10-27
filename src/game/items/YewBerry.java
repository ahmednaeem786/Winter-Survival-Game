package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.CoatAction;
import game.coating.Coatable;
import game.coating.CoatingType;
import edu.monash.fit2099.engine.items.PickUpAction;
import game.actions.TrackedPickUpAction;

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

    @Override
    public PickUpAction getPickUpAction(Actor actor) {
        return new TrackedPickUpAction(this);
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

    /**
     * Allow the owner to coat any coatable weapon in their inventory with this YewBerry.
     * We add one CoatAction per coatable weapon the owner is carrying.
     * Assumes CoatAction has the constructor:
     *   public CoatAction(Item coatingItem, Coatable weapon, CoatingType coatingType, String weaponName)
     * If your CoatAction signature differs, adapt the arguments accordingly.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();

        for (Item item : owner.getItemInventory()) {
            item.asCapability(Coatable.class).ifPresent(coatable -> actions.add(new CoatAction(this, coatable, CoatingType.YEWBERRY, item.toString())));
        }

        return actions;
    }
}

