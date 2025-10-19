package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;

public class AppleSprout extends Ground {

  private int age = 0;
  private final boolean isPlains;

  public AppleSprout(boolean isPlains) {
    super(',', "Wile Apple Sprout");
    this.isPlains = isPlains;
  }

  @Override
  public void tick(Location location) {
    super.tick(location);
    age++;

    if (isPlains) {
      location.addItem(new Apple());
    }

    if (age >= 3) {
      if (isPlains) {
        location.setGround(new AppleTree());
      } else {
        location.setGround(new AppleSapling(false));
      }
    }
  }

}
