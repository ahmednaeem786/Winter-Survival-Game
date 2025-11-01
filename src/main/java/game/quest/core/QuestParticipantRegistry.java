package game.quest.core;
import edu.monash.fit2099.engine.actors.Actor;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Registry mapping engine Actor instances to QuestParticipant adaptors.
 * Avoids instanceof checks and tight coupling in gameplay code.
 *
 * <p>Uses WeakHashMap to prevent memory leaks when actors are removed from the game.
 */
public final class QuestParticipantRegistry {
    private static final Map<Actor, QuestParticipant> registry = new WeakHashMap<>();
    private QuestParticipantRegistry() {}

    /**
     * Registers an actor as a quest participant.
     *
     * @param actor the actor to register
     * @param participant the quest participant implementation for this actor
     */
    public static void register(Actor actor, QuestParticipant participant) {
        if (actor == null || participant == null) return;
        registry.put(actor, participant);
    }

    /**
     * Unregisters an actor from the quest system.
     *
     * @param actor the actor to unregister
     */
    public static void unregister(Actor actor) {
        if (actor == null) return;
        registry.remove(actor);
    }

    /**
     * Retrieves the quest participant for a given actor.
     *
     * @param actor the actor to look up
     * @return the quest participant, or null if not registered
     */
    public static QuestParticipant get(Actor actor) {
        return registry.get(actor);
    }
}
