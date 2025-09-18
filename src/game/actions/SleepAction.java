package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.abilities.Abilities;
import game.items.Bedroll;

import java.util.Random;

/**
 * Action for sleeping on a bedroll.
 * Sleeping skips 6-10 turns randomly, during which hydration and warmth don't decrease.
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.3
 */
public class SleepAction extends Action {
    private Bedroll bedroll;
    private boolean isSleeping = false;
    private int turnsRemaining;
    private int totalSleepDuration;
    private Random random = new Random();

    public SleepAction(Bedroll bedroll) {
        this.bedroll = bedroll;
        // Generate the sleep duration when the action is created
        this.totalSleepDuration = 6 + random.nextInt(5); // 6-10 turns
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!isSleeping) {
            // Start sleeping - use the pre-generated duration
            turnsRemaining = totalSleepDuration;
            isSleeping = true;
            actor.enableAbility(Abilities.SLEEPING);
            return actor + " sleeps on the " + bedroll + " for " + turnsRemaining + " turns.";
        } else {
            // Continue sleeping
            turnsRemaining--;
            if (turnsRemaining <= 0) {
                isSleeping = false;
                actor.disableAbility(Abilities.SLEEPING);
                return actor + " wakes up refreshed!";
            }
            return actor + " continues sleeping... (" + turnsRemaining + " turns remaining)";
        }
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " sleeps for " + totalSleepDuration + " turns";
    }

    @Override
    public Action getNextAction() {
        if (isSleeping && turnsRemaining > 0) {
            return this; // Continue sleeping
        }
        return null; // Stop sleeping
    }

    /**
     * Check if the actor is currently sleeping
     * @return true if sleeping, false otherwise
     */
    public boolean isSleeping() {
        return isSleeping;
    }
}
