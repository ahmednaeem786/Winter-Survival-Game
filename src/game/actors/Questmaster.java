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
 * The Mystical Questmaster: a stationary NPC offering dynamic quests.
 * This NPC does not wander or attack; it simply provides an interaction action.
 */
public class Questmaster extends GameActor {

    public Questmaster() {
        super("Questmaster", 'Q', 9999);
    }

    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        // The Questmaster is stationary and passive.
        return new DoNothingAction();
    }

    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList list = new ActionList();
        // Allow anyone to talk to the Questmaster when adjacent
        list.add(new QuestAction(this, QuestServiceFactory.create()));
        return list;
    }
}

