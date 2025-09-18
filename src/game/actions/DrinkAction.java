package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.Bottle;
import game.utils.HydrationUtils;

/**
 * Action class that handles the drinking mechanic for the survival game.
 *
 * This action allows players to consume water from a Bottle to restore hydration
 * points, which are essential for survival in the winter environment. The action
 * manages the interaction between the actor and the bottle, including use validation
 * and hydration restoration.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.5
 */
public class DrinkAction extends Action {
    private Bottle bottle;

    /**
     * The bottle that will be used for drinking.
     */
    public DrinkAction(Bottle bottle) {
        this.bottle = bottle;
    }

    /**
     * Executes the drinking action, consuming one use of the bottle and restoring hydration.
     *
     * This method attempts to use the bottle and, if successful, restores hydration
     * to the player.
     *
     * @param actor the Actor performing the drink action (should be a Player)
     * @param map the GameMap where the action takes place (not used in current implementation)
     * @return a String describing the result of the action for display to the user
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (bottle.use()) {
            HydrationUtils.increaseHydration(actor, 4);

            if (bottle.getUsesRemaining() > 0) {
                return actor + " drinks from the " + bottle + ". Hydration restored! (" +
                        bottle.getUsesRemaining() + " uses remaining)";
            } else {
                actor.removeItemFromInventory(bottle);
                return actor + " drinks the last of the " + bottle + ". The bottle is now empty and discarded.";
            }
        } else {
            return "The " + bottle + " is empty!";
        }
    }

    /**
     * Returns the menu description for this action.
     *
     * @param actor the Actor who would perform this action
     * @return a String describing the action for menu display
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " drinks from the " + bottle;
    }

}
