package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;

public class AppleSapling extends Ground {

  private int age = 0;
  private final boolean isPlains;
  private int turnsSinceLastProduce = 0;

  public AppleSapling(boolean isPlains) {
    super('t', "Wild Apple Sapling");
    this.isPlains = isPlains;
  }

  @Override
  public void tick(Location location) {
    super.tick(location);
    age++;
    turnsSinceLastProduce++;

    if (turnsSinceLastProduce >= 2) {
      location.addItem(new Apple());
      turnsSinceLastProduce = 0;
    }

    if (age >= 5) {
      location.setGround(new AppleTree());
    }
  }
}
