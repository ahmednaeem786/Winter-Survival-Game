package game.terrain;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;
import game.terrain.Snow.SpawnHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A sapling that may grow into a YewBerryTree and produces YewBerries (on plains).
 *
 * <p>Behavior summary:
 * <ul>
 *   <li>Represents a young Yew Berry plant on the map (display char 'b').</li>
 *   <li>On each tick the sapling increments an internal age counter.</li>
 *   <li>Every 3 turns it performs a 50% growth roll — if successful it transforms into a {@link YewBerryTree}.</li>
 *   <li>If placed on a Plains map (isPlains == true), it produces a YewBerry every 2 turns and drops it
 *       to a random adjacent free tile when possible.</li>
 * </ul>
 * </p>
 *
 * <p>Notes:
 * <ul>
 *   <li>Dropping attempts prefer empty adjacent tiles; if none available the berry is placed on the sapling tile
 *       as a last resort (so the game does not silently lose the item).</li>
 *   <li>A private static RNG is used to randomize adjacency order and the growth coin flip.  If you require
 *       deterministic tests, consider adding a package-private setter to inject a seeded Random for tests.</li>
 * </ul>
 * </p>
 *
 * @author Ahmed
 */
public class YewBerrySapling extends Ground {

  /**
   * Counts turns for growth checks (every 3 turns we roll a 50% chance).
   */
  private int ageCounter = 0;

  /**
   * Counts turns between berry production on plains (produces every 2 turns on plains).
   */
  private int berryCounter = 0;

  /**
   * Flag indicating plains behaviour vs forest behaviour.
   */
  private final boolean isPlains;

  /**
   * Constructs a YewBerrySapling.
   *
   * @param isPlains true if this sapling is on a Plains map (plains-specific behaviour)
   */
  public YewBerrySapling(boolean isPlains) {
    super('b', "Yew Berry Sapling");
    this.isPlains = isPlains;
  }

  /**
   * Called by the engine each turn. Handles growth roll and plains berry production.
   *
   * @param location the location of this sapling on the map
   */
  @Override
  public void tick(Location location) {
    super.tick(location);

    ageCounter++; //Age increases every tick

    // Plains-specific: produce a YewBerry every 2 turns
    if (isPlains) {
      berryCounter++;
      if (berryCounter >= PlantConstants.PLAINS_YEWBERRY_DROP_INTERVAL) {
        berryCounter = 0;
        dropBerryNearby(location, new YewBerry());
      }
    }

    // Every 3 turns: 50% chance to grow into tree
    if (ageCounter >= PlantConstants.YEWBERRY_GROW_ATTEMPT_TURNS) {
      ageCounter = 0;
      if (SpawnHelper.shouldSpawnChance(PlantConstants.YEWBERRY_GROW_CHANCE_PERCENT)) {
        location.setGround(new YewBerryTree());
      }
    }
  }

  /**
   * Attempt to place the produced berry in a random adjacent free tile.
   * If all adjacent tiles are occupied or contain items, fall back to placing the
   * berry on the sapling tile itself to avoid losing the item.
   *
   * @param here  the sapling's location
   * @param berry the YewBerry item to drop
   */
  private void dropBerryNearby(Location here, Item berry) {
    List<Exit> exits = new ArrayList<>(here.getExits());
    // Shuffle adjacency order so dropped berries are spread around
    Collections.shuffle(exits, SpawnHelper.getRandom());

    for (Exit exit : exits) {
      Location dest = exit.getDestination();
      if (!dest.containsAnActor() && dest.getItems().isEmpty()) {
        dest.addItem(berry);
        return;
      }
    }
  }

  @Override
  public String toString() {
    return "Yew Berry Sapling";
  }
}
