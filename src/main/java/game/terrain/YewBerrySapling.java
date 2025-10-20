package game.terrain;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class YewBerrySapling extends Ground {


  private int ageCounter = 0;          // counts turns for growth roll
  private int berryCounter = 0;        // counts turns between berry production on plains
  private final boolean isPlains;
  private static final Random RNG = new Random();

  public YewBerrySapling(boolean isPlains) {
    super('b', "Yew Berry Sapling");
    this.isPlains = isPlains;
  }

  @Override
  public void tick(Location location) {
    super.tick(location);

    ageCounter++;
    if (isPlains) {
      berryCounter++;
      if (berryCounter >= 2) {
        berryCounter = 0;
        dropBerryNearby(location, new YewBerry());
      }
    }

    // Every 3 turns: 50% chance to grow into tree
    if (ageCounter >= 3) {
      ageCounter = 0;
      if (RNG.nextBoolean()) {
        location.setGround(new YewBerryTree());
      }
    }
  }

  private void dropBerryNearby(Location here, Item berry) {
    List<Exit> exits = new ArrayList<>(here.getExits());
    Collections.shuffle(exits, RNG);

    for (Exit exit : exits) {
      Location dest = exit.getDestination();
      if (!dest.containsAnActor() && dest.getItems().isEmpty()) {
        dest.addItem(berry);
        return;
      }
    }

    // fallback: place on the sapling tile only if absolutely necessary
    here.addItem(berry);
  }

  @Override
  public String toString() {
    return "Yew Berry Sapling";
  }
}
