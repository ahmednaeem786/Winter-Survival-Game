package game.abilities;

/**
 * Interface for actors that support hydration mechanics.
 * Actors implementing this interface should also add Abilities.HYDRATION to themselves.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public interface HydrationCapability {
    void increaseHydration(int amount);

}