package game.terrain;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;
import game.terrain.Snow.SpawnHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a wild apple sprout on the map.
 *
 * <p>Behaviour summary:
 * <ul>
 *   <li>On the Plains: the sprout produces an {@link Apple} every turn and becomes a {@link WildAppleTree}
 *       after 3 ticks (skips the sapling stage).</li>
 *   <li>In the Forest: the sprout grows into an {@link AppleSapling} after 3 ticks (sapling stage then applies).</li>
 *   <li>Apples are dropped into a random adjacent free tile; if all adjacent tiles are blocked, the apple
 *       is placed on the sprout's own tile (fallback).</li>
 * </ul>
 * </p>
 *
 * <p>Notes:
 * <ul>
 *   <li>This class contains a static RNG for shuffling adjacent exits. For deterministic unit tests you can
 *       seed this RNG globally or refactor to inject a Random instance.</li>
 * </ul>
 * </p>
 *
 * @author Ahmed
 */
public class AppleSprout extends Ground {

  /**
   * Age of the sprout in ticks.
   */
  private int age = 0;

  /**
   * Turns since last apple production (used for plains behaviour).
   */
  private int turnsSinceLastApple = 0;

  /**
   * Flag indicating whether this sprout was created for the Plains map.
   * Plains sprouts follow different lifecycle rules from Forest sprouts.
   */
  private final boolean isPlains;

  /**
   * Creates an AppleSprout.
   *
   * @param isPlains true when the sprout is on the Plains map (plains semantics apply)
   */
  public AppleSprout(boolean isPlains) {
    super(',', "Wild Apple Sprout");
    this.isPlains = isPlains;
  }

  /**
   * Tick called each game turn. Advances age and performs map-specific lifecycle rules:
   * <ul>
   *   <li>Plains: produce an apple every turn; after 3 ticks become a {@link WildAppleTree}.</li>
   *   <li>Forest: after 3 ticks become an {@link AppleSapling} (sapling stage).</li>
   * </ul>
   *
   * @param location the Location this sprout occupies
   */
  @Override
  public void tick(Location location) {
    super.tick(location);
    age++;

    if (isPlains) {
      // Plains sprout: produces apple every turn
      turnsSinceLastApple++;
      if (turnsSinceLastApple >= PlantConstants.PLAINS_SPROUT_DROP_INTERVAL) {
        turnsSinceLastApple = 0;
        //Attempting to drop apple into a nearby free tile
        dropAppleNearby(location, new Apple());
      }
      // after 3 turns become tree (skip sapling)
      if (age >= PlantConstants.FOREST_SPROUT_TO_SAPLING_TURNS) {
        location.setGround(new WildAppleTree());
      }
    } else {
      // Forest sprout: grow into sapling after 3 turns
      if (age >= PlantConstants.FOREST_SPROUT_TO_SAPLING_TURNS) {
        location.setGround(new AppleSapling(isPlains));
      }
    }
  }

  /**
   * Attempts to place the apple into a random adjacent free tile. Uses a shuffled copy of the exits
   * so that distribution across neighbours is non-deterministic in gameplay.
   *
   * <p>The method prefers tiles that:
   * <ul>
   *   <li>are not occupied by an actor, and</li>
   *   <li>have no items currently</li>
   * </ul>
   * If no such tile exists, the apple is placed on the sprout's own tile (fallback) to avoid losing the item.
   * </p>
   *
   * @param here  the sprout's current location
   * @param apple the apple to place
   */
  private void dropAppleNearby(Location here, Item apple) {
    // Copy exits to a mutable list and shuffle to randomize pickup location
    List<Exit> exits = new ArrayList<>(here.getExits());
    Collections.shuffle(exits, SpawnHelper.getRandom());
    for (Exit exit : exits) {
      Location dest = exit.getDestination();
      if (!dest.containsAnActor() && dest.getItems().isEmpty()) {
        dest.addItem(apple);
        return;
      }
    }
    // Fallback: if all adjacent tiles are blocked, place apple on this tile
    here.addItem(apple);
  }
}
