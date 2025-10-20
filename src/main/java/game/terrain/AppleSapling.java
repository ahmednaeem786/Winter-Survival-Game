package game.terrain;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a young wild apple sapling on the map.
 *
 * <p>
 * Behaviour:
 * <ul>
 *   <li>Has an internal age counter. After {@code 5} sapling ticks it becomes a {@link WildAppleTree}.</li>
 *   <li>Produces an {@link Apple} item every {@code 2} sapling ticks, attempting to place it in a random
 *       adjacent free tile. If all adjacent tiles are blocked or occupied, it falls back and places the
 *       apple at the sapling's own location.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Note: the constructor accepts {@code isPlains} to allow map-specific behaviour (per-spec). In this
 * implementation the {@code isPlains} flag is unused because the sapling lifecycle rules for plains are
 * handled by producing a different initial ground object via {@code PlantFactory.createAppleSproutForMap(true)}.
 * Keep this parameter for future map-specific tweaks or to make behaviour conditional here later.
 * </p>
 * @author Ahmed
 */

public class AppleSapling extends Ground {


  /**
   * Age of the sapling in ticks. When {@code age >= 5} the sapling becomes a WildAppleTree.
   */
  private int age = 0;
  /**
   * Tracks turns since an apple was last produced. When >= 2, the sapling attempts to drop an apple.
   */
  private int turnsSinceLastApple = 0;
  /**
   * RNG (Random Number Generator) used to shuffle adjacent exits so apple-dropping is non-deterministic across runs.
   * <p>
   * Using a single static RNG keeps behaviour simple. For deterministic unit tests you can replace or
   * seed this RNG (or better: refactor to inject RNG).
   * </p>
   */
  private static final Random RNG = new Random();

  /**
   * Construct an AppleSapling.
   *
   * @param isPlains flag indicating if this sapling is created for the Plains map semantics.
   *                 Currently unused here — factories decide which ground instance to create.
   */
  public AppleSapling(boolean isPlains) {
    super('t', "Wild Apple Sapling");
  }

  /**
   * Called each game tick to advance the sapling lifecycle and produce fruit periodically.
   *
   * @param location the Location this sapling occupies
   */
  @Override
  public void tick(Location location) {

    // Always call super to preserve any engine-level behaviour
    super.tick(location);
    age++;
    turnsSinceLastApple++;

    // produce apple every 2 turns
    if (turnsSinceLastApple >= 2) {
      turnsSinceLastApple = 0;
      dropAppleNearby(location, new Apple());
    }

    // grow into tree after 5 turns
    if (age >= 5) {
      location.setGround(new WildAppleTree());
    }
  }

  /**
   * Attempts to drop the provided apple into a random adjacent location that is free (no actor and no items).
   *
   * <p>
   * Algorithm:
   * <ol>
   *   <li>Copy the exits (adjacent tiles) into a list</li>
   *   <li>Shuffle the exits with RNG to randomize which free tile is picked</li>
   *   <li>For each exit in shuffled order: if destination does not contain an actor and has no items, add the apple there</li>
   *   <li>If no suitable adjacent tile is found, the apple is placed on the sapling's own tile as a fallback</li>
   * </ol>
   * </p>
   *
   * @param here  the sapling's current location
   * @param apple the apple item to place
   */
  private void dropAppleNearby(Location here, Item apple) {

    // Defensive copy so we can shuffle without affecting the map's internal exit collection.
    List<Exit> exits = new ArrayList<>(here.getExits());
    // Randomize search order so apples don't always go to the same neighbor (more natural distribution).
    Collections.shuffle(exits, RNG);

    // Try to find a free adjacent tile (no actor, no items)
    for (Exit exit : exits) {
      Location dest = exit.getDestination();

      // Accept only tiles that aren't occupied by an actor and currently have no items.
      // This keeps dropped fruit accessible and avoids overwriting or stacking on existing items.
      if (!dest.containsAnActor() && dest.getItems().isEmpty()) {
        dest.addItem(apple);
        return;
      }
    }
    // Fallback: if all adjacent tiles were blocked, place apple on the sapling's tile itself.
    here.addItem(apple);
  }
}
