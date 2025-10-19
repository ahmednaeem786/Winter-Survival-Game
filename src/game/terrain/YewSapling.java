package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;
import java.util.Random;

public class YewSapling extends Ground {

  private int age = 0;
  private int turnsSinceLastProduce = 0;
  private final boolean isPlains;
  private final Random rand = new Random();

  public YewSapling(boolean isPlains) {
    super('b', "Yew Berry Sapling");
    this.isPlains = isPlains;
  }

  @Override
  public void tick(Location location) {
    super.tick(location);
    age++;
    turnsSinceLastProduce++;

    if (isPlains && turnsSinceLastProduce >= 2) {
      location.addItem(new YewBerry());
      turnsSinceLastProduce = 0;
    }

    if (age % 3 == 0) {
      if (rand.nextBoolean()) {
        location.setGround(new YewTree());
      }
    }
  }

}
