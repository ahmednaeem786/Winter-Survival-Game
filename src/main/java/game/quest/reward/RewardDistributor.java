package game.quest.reward;

import edu.monash.fit2099.engine.actors.Actor;
import game.quest.core.QuestParticipant;
import game.quest.model.Quest;

/**
 * Distributes quest rewards to quest participants.
 * Implementations map abstract reward descriptions to concrete game items and effects.
 *
 * <p>This interface allows different reward distribution strategies to be implemented,
 * supporting both predefined and AI-generated reward names.
 */
public interface RewardDistributor {
    /**
     * Distributes all rewards for a quest to the specified participant.
     * Adds concrete items to the actor's inventory based on reward descriptions.
     *
     * @param quest the completed quest containing rewards
     * @param participant the quest participant receiving rewards
     * @param asActor the actor representation for inventory operations
     * @return a human-readable summary of distributed rewards
     */
    String distribute(Quest quest, QuestParticipant participant, Actor asActor);
}
