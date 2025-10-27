package game.quest;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Distributes quest rewards to a quest participant.
 * Implementations map abstract rewards to concrete game items/effects.
 */
public interface RewardDistributor {
    /**
     * Distribute all rewards for the given quest to the provided participant.
     * Returns a human-readable summary.
     */
    String distribute(Quest quest, QuestParticipant participant, Actor asActor);
}
