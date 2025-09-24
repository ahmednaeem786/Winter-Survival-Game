package game.status;

import edu.monash.fit2099.engine.actors.Actor;
import java.util.Map;
import java.util.WeakHashMap;

public final class StatusRecipientRegistry {

  private static final Map<Actor, StatusRecipient> registry = new WeakHashMap<>();

  private StatusRecipientRegistry() {}

  public static void register (Actor actor, StatusRecipient recipient) {
    if (actor == null || recipient == null) {
      return;
    }
    registry.put(actor, recipient);
  }

  public static void unregister(Actor actor) {
    if (actor == null) {
      return;
    }
    registry.remove(actor);
  }

  public static StatusRecipient getRecipient (Actor actor) {
    return registry.get(actor);
  }

}
