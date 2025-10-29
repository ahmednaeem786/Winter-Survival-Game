package game.taming;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.List;

/**
 * Interface for actors that can collect items and give them to an owner.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public interface ItemCollector {
    void collectNearbyItems(GameMap map, Display display);
    void giveItemsToOwner(Display display);
    List<Item> getCollectedItems();
}
