package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for mapping animal classes to their post-spawn effect strategies.
 * 
 * <p>This registry provides a centralized system for managing post-spawn effects,
 * allowing each animal type to have custom behaviors triggered immediately after
 * spawning. Effects are applied automatically by the spawning system, regardless
 * of which spawner type is used (Swamp, Tundra, Meadow, Cave).
 * 
 * <p>The registry follows the Strategy pattern and Open/Closed Principle - new
 * effects can be added by registering them without modifying existing spawning code.
 * 
 * <p>To chain multiple effects for a single animal type, create a composite effect
 * that calls multiple other effects in sequence.
 * 
 * <p>Registration typically occurs during world initialization (in {@link game.Earth}).
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
public class PostSpawnEffectRegistry {
    /** Map of animal classes to their post-spawn effect implementations. */
    private static final Map<Class<? extends Actor>, PostSpawnEffect> effectMap = new HashMap<>();

    /**
     * Registers a post-spawn effect for an animal type.
     * If an effect is already registered for this type, it will be replaced.
     * 
     * @param type the class of the animal to register an effect for
     * @param effect the post-spawn effect strategy to apply when this animal spawns
     */
    public static void register(Class<? extends Actor> type, PostSpawnEffect effect) {
        effectMap.put(type, effect);
    }

    /**
     * Retrieves the post-spawn effect for the given animal class.
     * 
     * @param type the class of the animal to look up
     * @return the registered post-spawn effect, or null if no effect is registered
     */
    public static PostSpawnEffect getFor(Class<? extends Actor> type) {
        return effectMap.get(type);
    }
}
