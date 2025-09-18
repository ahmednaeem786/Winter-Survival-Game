package game.utils;

import edu.monash.fit2099.engine.actors.Actor;
import game.abilities.Abilities;
import game.abilities.HydrationCapability;

/**
 * Utility class for hydration-related operations.
 * This follows the Single Responsibility Principle by centralizing
 * hydration logic in one place.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.1
 */
public class HydrationUtils {

    /**
     * Increases hydration for actors that have the HYDRATION ability.
     *
     * @param actor the actor to increase hydration for
     * @param amount the amount to increase hydration by
     */
    public static void increaseHydration(Actor actor, int amount) {
        if (actor.hasAbility(Abilities.HYDRATION)) {
            ((HydrationCapability) actor).increaseHydration(amount);
        }
    }
}