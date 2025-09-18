package game.taming;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Interface for tamed animals that can
 * join their owner in combat
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public interface CombatAssistant {
    Action findCombatTarget(GameMap map);
    boolean canAssistInCombat();
}
