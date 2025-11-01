package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry and factory source for all animal instantiation following SOLID principles.
 * 
 * <p>This registry provides a centralized, extensible system for creating animals without
 * using reflection or instanceof checks. Each animal type is registered with a factory
 * lambda, allowing for dependency injection and custom initialization.
 * 
 * <p>The registry follows the Open/Closed Principle - new animals can be added by
 * registering factories without modifying existing code. All animals are created
 * polymorphically through the same interface.
 * 
 * <p>Registration typically occurs during world initialization (in {@link game.Earth}).
 * 
 * <p>Example:
 * <pre>
 * AnimalRegistry.register(Bear.class, Bear::new);
 * AnimalRegistry.register(Crocodile.class, () -> new Crocodile(...));
 * </pre>
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
public class AnimalRegistry {
    /** Map of animal classes to their factory implementations. */
    private static final Map<Class<? extends Actor>, AnimalFactory> factories = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private AnimalRegistry() {}

    /**
     * Registers a factory for creating instances of the specified animal type.
     * 
     * @param type the class of the animal to register
     * @param factory the factory function that creates instances of this animal type
     */
    public static void register(Class<? extends Actor> type, AnimalFactory factory) {
        factories.put(type, factory);
    }

    /**
     * Creates a new instance of the specified animal type using its registered factory.
     * 
     * @param type the class of the animal to create
     * @return a new instance of the specified animal type
     * @throws IllegalArgumentException if no factory is registered for the given type
     */
    public static Actor create(Class<? extends Actor> type) {
        AnimalFactory factory = factories.get(type);
        if (factory == null)
            throw new IllegalArgumentException("No AnimalFactory registered for: " + type);
        return factory.create();
    }

    /**
     * Returns all registered animal classes.
     * Useful for iteration or debugging purposes.
     * 
     * @return an iterable collection of all registered animal classes
     */
    public static Iterable<Class<? extends Actor>> registeredTypes() {
        return factories.keySet();
    }
}
