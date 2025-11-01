package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.QuestAction;
import game.quest.provider.QuestServiceFactory;

/**
 * The Mystical Questmaster NPC offering dynamic quests.
 * Stationary character that provides quest-related interactions to adjacent actors.
 *
 * <p>This NPC does not wander or engage in combat, serving solely as a quest hub.
 * Quest generation strategy is determined at runtime by the QuestServiceFactory.
 */
public class Questmaster extends GameActor {

    /**
     * Creates a new Questmaster with high hit points and distinctive appearance.
     */
    public Questmaster() {
        super("Questmaster", 'Q', 9999);
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        // The Questmaster is stationary and passive.
        return new DoNothingAction();
    }

    /**
     * Provides quest interaction action to adjacent actors.
     *
     * @param otherActor the actor considering interaction
     * @param direction the direction to the Questmaster
     * @param map the current game map
     * @return list containing QuestAction for quest operations
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList list = new ActionList();
        // Allow anyone to talk to the Questmaster when adjacent
        list.add(new QuestAction(this, QuestServiceFactory.create()));
        return list;
    }
}

