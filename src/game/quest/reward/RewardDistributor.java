package game.quest.reward;

import edu.monash.fit2099.engine.actors.Actor;
import game.quest.core.QuestParticipant;
import game.quest.model.Quest;

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
