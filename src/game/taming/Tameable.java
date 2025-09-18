package game.taming;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;

/**
 * Interface for actors that can be tamed
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public interface Tameable {
    boolean isTamed();
    void tame(Actor tamer);
    Actor getTamer();
    boolean canBeTamedWith(Item item);
}
