package game.status;

import edu.monash.fit2099.engine.actors.Actor;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Registry that maps engine {@link Actor} instances to {@link StatusRecipient}
 * handlers.
 *
 * <p>Uses a {@link WeakHashMap} so the presence of an entry does not prevent an
 * {@link Actor} from being garbage-collected. External systems (grounds, actions)
 * may query this registry to obtain a {@link StatusRecipient} for an {@link Actor}
 * without relying on instanceof/casting.
 *
 * <p>All methods are static; the class is effectively a global, lightweight
 * lookup utility for the game package.
 *
 * @author Ahmed
 */
public final class StatusRecipientRegistry {

  /**
   * Weak-key map ensuring registry entries do not prevent Actor GC.
   * Key: engine Actor instance. Value: corresponding StatusRecipient.
   */
  private static final Map<Actor, StatusRecipient> registry = new WeakHashMap<>();

  // Preventing instantiation
  private StatusRecipientRegistry() {}

  /**
   * Register a recipient handler for the given actor.
   *
   * @param actor the engine Actor to associate with a recipient
   * @param recipient the StatusRecipient implementation for the actor
   */
  public static void register (Actor actor, StatusRecipient recipient) {
    if (actor == null || recipient == null) {
      return;
    }
    registry.put(actor, recipient);
  }

  /**
   * Remove any registry mapping for the provided actor.
   *
   * @param actor the actor whose mapping should be removed
   */
  public static void unregister(Actor actor) {
    if (actor == null) {
      return;
    }
    registry.remove(actor);
  }

  /**
   * Retrieve the registered {@link StatusRecipient} for the given actor.
   *
   * @param actor the actor to look up
   * @return the registered StatusRecipient, or {@code null} if none is registered
   */
  public static StatusRecipient getRecipient (Actor actor) {
    return registry.get(actor);
  }

}
