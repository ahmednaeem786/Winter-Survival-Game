package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.attributes.BaseActorAttribute;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Menu;
import edu.monash.fit2099.engine.positions.GameMap;
import game.abilities.Abilities;
import game.abilities.HydrationCapability;
import game.items.Apple;
import game.items.Bedroll;
import game.items.Bottle;
import game.weapons.BareFist;

/**
 * Class representing the Player (Explorer).
 * The Explorer has hydration and warmth levels that decrease each turn.
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 3.8
 */
public class Player extends GameActor implements HydrationCapability {
    private BaseActorAttribute hydration;
    private BaseActorAttribute warmth;

    /**
     * Constructor for creating a new Player character.
     * Initializes the player with survival attributes, starting equipment,
     * and basic combat capabilities.
     *
     * @param name        Display name for the player character in the UI
     * @param displayChar Single character used to represent the player on the game map
     * @param hitPoints   Starting hit points for the player character
     */
    public Player(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);
        this.setIntrinsicWeapon(new BareFist());

        // Initialize survival attributes
        this.hydration = new BaseActorAttribute(20);
        this.warmth = new BaseActorAttribute(30);

        this.enableAbility(Abilities.HYDRATION);

        // Add starting items
        this.addItemToInventory(new Bedroll());
        this.addItemToInventory(new Bottle());
        this.addItemToInventory(new Apple());
    }

    /**
     * Processes the player's turn in the game loop.
     * The method implements the core survival mechanics by automatically
     * decreasing hydration and warmth each turn. It also provides complete status feedback to
     * help players make informed decisions.
     *
     * @param actions available actions the player can choose from
     * @param lastAction the action performed in the previous turn (may be null)
     * @param map the GameMap containing the player and other game entities
     * @param display the Display object for outputting information to the player
     * @return the Action chosen by the player to execute this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        tickStatusEffects(map);
        // Check if player is unconscious (hydration or warmth at 0)
        if (hydration.get() <= 0 || warmth.get() <= 0) {
            display.println(name + " becomes unconscious! Game Over!");
            display.println("GAME OVER - Survival failed!");
            System.exit(0);
        }

        // Decrease hydration and warmth each turn (unless sleeping)
        if (!this.hasAbility(Abilities.SLEEPING)) {
            hydration.decrease(1);
            warmth.decrease(1);
        }

        display.println(String.format("%s (%d/%d)", name,
                this.getAttribute(BaseAttributes.HEALTH),
                this.getMaximumAttribute(BaseAttributes.HEALTH)));
        display.println("HYDRATION: " + hydration.get());
        display.println("WARMTH: " + warmth.get());

        if (lastAction != null && lastAction.getNextAction() != null)
            return lastAction.getNextAction();

        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }

    /**
     * Increases the player's hydration level by the specified amount.
     * This method is typically called when the player drinks from a bottle
     * or consumes other hydrating items.
     * @param amount number of hydration points to add (should be positive)
     */
    public void increaseHydration(int amount) {
        this.hydration.increase(amount);
    }
}