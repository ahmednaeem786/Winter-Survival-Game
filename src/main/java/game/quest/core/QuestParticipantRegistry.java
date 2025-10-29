package game.quest.core;
import edu.monash.fit2099.engine.actors.Actor;

import java.util.Map;
import java.util.WeakHashMap;
/**
 * Registry mapping engine {@link Actor} instances to {@link QuestParticipant} adaptors.
 * Avoids instanceof/casting in gameplay code.
 */
public final class QuestParticipantRegistry {
    private static final Map<Actor, QuestParticipant> registry = new WeakHashMap<>();
    private QuestParticipantRegistry() {}
    public static void register(Actor actor, QuestParticipant participant) {
        if (actor == null || participant == null) return;
        registry.put(actor, participant);
    }
    public static void unregister(Actor actor) {
        if (actor == null) return;
        registry.remove(actor);
    }
    public static QuestParticipant get(Actor actor) {
        return registry.get(actor);
    }
}
