package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Functional interface for creating animals/actors via dependency injection.
 * 
 * <p>This interface enables polymorphic animal creation without using reflection,
 * allowing for dependency injection and custom initialization logic. Factory instances
 * are registered in {@link AnimalRegistry} and called during spawning.
 * 
 * <p>This approach follows SOLID principles (Dependency Inversion) and enables
 * extensibility (Open/Closed Principle) - new animals can be added by simply
 * registering a factory, without modifying existing spawning code.
 * 
 * <p>Example usage:
 * <pre>
 * AnimalFactory bearFactory = Bear::new;
 * AnimalRegistry.register(Bear.class, bearFactory);
 * </pre>
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
@FunctionalInterface
public interface AnimalFactory {
    /**
     * Creates a new instance of an animal/actor.
     * 
     * @return a new Actor instance
     */
    Actor create();
}
