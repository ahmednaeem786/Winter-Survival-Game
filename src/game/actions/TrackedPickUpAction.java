package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.items.PickUpAction;
import edu.monash.fit2099.engine.positions.GameMap;
import game.quest.core.QuestParticipant;
import game.quest.core.QuestParticipantRegistry;

/**
 * Pick up action that records quest COLLECT progress via QuestParticipantRegistry.
 * Extends engine PickUpAction to remain type-compatible with Item.getPickUpAction.
 */
public class TrackedPickUpAction extends PickUpAction {
    private final Item trackedItem;

    public TrackedPickUpAction(Item item) {
        super(item);
        this.trackedItem = item;
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        String result = super.execute(actor, map);
        QuestParticipant participant = QuestParticipantRegistry.get(actor);
        if (participant != null) {
            participant.getQuestTracker().recordCollect(trackedItem.getClass().getSimpleName(), 1);
        }
        return result;
    }
}
