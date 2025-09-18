package game.taming;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Interface for tamed animals that can follow their owner
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public interface Follower {
    Action followOwner(GameMap map);
    boolean shouldFollow();
}
