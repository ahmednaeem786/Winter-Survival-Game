package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;

public class YewTree extends Ground {

  private int turnsSinceLastProduce = 0;

  public YewTree() {
    super('Y', "Yew Berry Tree");
  }

  @Override
  public void tick(Location location) {
    super.tick(location);
    turnsSinceLastProduce++;

    if (turnsSinceLastProduce >= 5) {
      location.addItem(new YewBerry());
      turnsSinceLastProduce = 0;
    }
  }
}
