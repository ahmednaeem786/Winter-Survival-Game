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

public class AppleSprout extends Ground {

  private int age = 0;
  private int turnsSinceLastApple = 0;
  private final boolean isPlains;
  private static final Random RNG = new Random();

  public AppleSprout(boolean isPlains) {
    super(',', "Wild Apple Sprout");
    this.isPlains = isPlains;
  }

  @Override
  public void tick(Location location) {
    super.tick(location);
    age++;

    if (isPlains) {
      // Plains sprout: produces apple every turn
      turnsSinceLastApple++;
      if (turnsSinceLastApple >= 1) {
        turnsSinceLastApple = 0;
        dropAppleNearby(location, new Apple());
      }
      // after 3 turns become tree (skip sapling)
      if (age >= 3) {
        location.setGround(new WildAppleTree());
      }
    } else {
      // Forest sprout: grow into sapling after 3 turns
      if (age >= 3) {
        location.setGround(new AppleSapling(isPlains));
      }
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
