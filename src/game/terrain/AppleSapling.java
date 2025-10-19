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

public class AppleSapling extends Ground {

  private int age = 0;
  private int turnsSinceLastApple = 0;
  private static final Random RNG = new Random();

  public AppleSapling(boolean isPlains) {
    super('t', "Wild Apple Sapling");
  }

  @Override
  public void tick(Location location) {
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

  private void dropAppleNearby(Location here, Item apple) {
    List<Exit> exits = new ArrayList<>(here.getExits());
    Collections.shuffle(exits, RNG);
    for (Exit exit : exits) {
      Location dest = exit.getDestination();
      if (!dest.containsAnActor() && dest.getItems().isEmpty()) {
        dest.addItem(apple);
        return;
      }
    }
    // fallback
    here.addItem(apple);
  }
}
