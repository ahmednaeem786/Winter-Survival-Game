package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;

public class AppleTree extends Ground {

  private int turnsSinceLastProduce = 0;

  public AppleTree() {
    super('T', "Wild Apple Tree");
  }

  @Override
  public void tick(Location location) {
    super.tick(location);
    turnsSinceLastProduce++;

    if (turnsSinceLastProduce >= 3) {
      location.addItem(new Apple());
      turnsSinceLastProduce = 0;
    }
  }
}
