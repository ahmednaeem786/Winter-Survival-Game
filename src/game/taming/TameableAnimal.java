package game.taming;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.AttackAction;
import game.actions.TameAction;

import game.actors.GameActor;
import java.util.Set;

/**
 * Abstract base class for tameable animals in the winter survival game.
 * This class provides the foundation for creatures that can be tamed by players
 * using specific food items.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.5
 */
public abstract class TameableAnimal extends GameActor implements Tameable {

    /**
     * Whether this animal has been tamed by a player.
     */
    protected boolean tamed = false;

    /**
     * The actor who tamed this animal, or null if untamed.
     */
    protected Actor tamer = null;

    /**
     * Set of item classes that this animal will accept as taming food.
     * Each subclass defines which specific items can be used to tame it.
     */
    protected final Set<Class<? extends Item>> acceptedFoods;

    /**
     * Constructs a new tameable animal with specified characteristics.
     *
     * @param name the display name of the animal
     * @param displayChar the character used to represent this animal on the game map
     * @param hitPoints the starting health points for this animal
     * @param acceptedFoods set of item classes that can be used to tame this animal
     */
    public TameableAnimal(String name, char displayChar, int hitPoints, Set<Class<? extends Item>> acceptedFoods) {
        super(name, displayChar, hitPoints);
        this.acceptedFoods = acceptedFoods;
    }

    /**
     * Checks whether this animal has been tamed.
     *
     * @return true if the animal is tamed, false if still wild
     */
    @Override
    public boolean isTamed() {
        return tamed;
    }

    /**
     * Tames this animal and establishes ownership relationship.
     * When an animal is successfully tamed, it becomes loyal to the tamer,
     * changes its behavior patterns, and triggers any taming-specific effects
     * defined in the subclass.
     *
     * @param tamer the actor who is taming this animal
     */
    @Override
    public void tame(Actor tamer) {
        this.tamed = true;
        this.tamer = tamer;
        onTamed();
    }

    /**
     * Gets the actor who tamed this animal.
     *
     * @return the tamer actor, or null if the animal is not tamed
     */
    @Override
    public Actor getTamer() {
        return tamer;
    }

    /**
     * Determines if this animal can be tamed using the specified item.
     * Animals can only be tamed with specific food items. Each animal species
     * has its own preferences defined in the acceptedFoods set.
     *
     * @param item the item to check for taming compatibility
     * @return true if the item can be used to tame this animal, false otherwise
     */
    @Override
    public boolean canBeTamedWith(Item item) {
        return acceptedFoods.contains(item.getClass());
    }

    /**
     * Called when the animal is successfully tamed.
     * Subclasses should override this method to implement any special effects
     * or behavior changes that occur when the animal becomes tamed, such as
     * gaining special abilities.
     */
    protected abstract void onTamed();

    /**
     * Defines the behavior pattern for wild (untamed) animals.
     * Wild animals typically exhibit aggressive or neutral behaviors such as
     * attacking players, wandering randomly, or fleeing from threats.
     *
     * @param actions the list of available actions for this turn
     * @param lastAction the action performed in the previous turn
     * @param map the current game map
     * @param display the display interface for output
     * @return the action this animal will perform while wild
     */
    protected abstract Action wildBehavior(ActionList actions, Action lastAction, GameMap map, Display display);

    /**
     * Defines the behavior pattern for tamed animals.
     * Tamed animals typically exhibit loyal behaviors such as following their
     * owner, defending them, or staying nearby. They should not attack their
     * tamer or other friendly actors.
     *
     * @param actions the list of available actions for this turn
     * @param lastAction the action performed in the previous turn
     * @param map the current game map
     * @param display the display interface for output
     * @return the action this animal will perform while tamed
     */
    protected abstract Action tamedBehavior(ActionList actions, Action lastAction, GameMap map, Display display);

    /**
     * Determines the animal's action for this turn based on its taming status.
     * This method serves as the main behavioral dispatcher, routing to either
     * wild or tamed behavior patterns depending on the animal's current state.
     *
     * @param actions the list of available actions for this turn
     * @param lastAction the action performed in the previous turn
     * @param map the current game map
     * @param display the display interface for output
     * @return the action this animal will perform this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        tickStatusEffects(map);
        
        // Check if animal has GROUND_CONSUMPTION ability and consume items on ground
        if (this.hasAbility(game.abilities.Abilities.GROUND_CONSUMPTION)) {
            consumeGroundItems(map);
        }
        
        if (tamed) {
            return tamedBehavior(actions, lastAction, map, display);
        } else {
            return wildBehavior(actions, lastAction, map, display);
        }
    }
    
    /**
     * Consumes any consumable items on the ground at the animal's current location.
     * This mimics the behavior of the Explorer consuming ground items.
     * 
     * @param map the current game map
     */
    private void consumeGroundItems(GameMap map) {
        edu.monash.fit2099.engine.positions.Location currentLocation = map.locationOf(this);
        if (currentLocation == null) {
            return;
        }
        
        // Get all items at current location
        java.util.List<edu.monash.fit2099.engine.items.Item> items = new java.util.ArrayList<>(currentLocation.getItems());
        
        for (edu.monash.fit2099.engine.items.Item item : items) {
            // Check if item is consumable using capability pattern (same pattern as player)
            java.util.Optional<game.items.ConsumableItem> consumableOpt = item.asCapability(game.items.ConsumableItem.class);
            if (consumableOpt.isPresent()) {
                game.items.ConsumableItem consumable = consumableOpt.get();
                
                // Remove item from ground and consume it
                currentLocation.removeItem(item);
                String consumeMessage = consumable.consume(this, map);
                System.out.println(consumeMessage);
                
                // Only consume one item per turn
                break;
            }
        }
    }

    /**
     * Generates the list of actions that other actors can perform on this animal.
     * This method controls what interactions are available to players and other
     * actors when they encounter this animal.
     *
     * @param otherActor the actor who might perform actions on this animal
     * @param direction the direction from the other actor to this animal
     * @param map the current game map
     * @return ActionList containing all available actions for the other actor
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = super.allowableActions(otherActor, direction, map);

        // Only untamed animals can be tamed
        if (!tamed) {
            for (Item item : otherActor.getItemInventory()) {
                if (canBeTamedWith(item)) {
                    actions.add(new TameAction(this, item));
                    break;
                }
            }
        }

        if (!tamed && otherActor.getIntrinsicWeapon() != null) {
            actions.add(new AttackAction(this, direction));
        }

        return actions;
    }
}