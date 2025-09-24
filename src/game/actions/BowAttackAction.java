package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.GameActor;
import game.items.Bow;
import java.util.Random;

public class BowAttackAction extends Action {

  private final Bow bow;
  private final GameActor target;
  private final int distance;
  private final Random rand = new Random();

  public BowAttackAction (Bow bow, GameActor target, int distance) {
    this.bow = bow;
    this.target = target;
    this.distance = distance;
  }

  @Override
  public String execute(Actor actor, GameMap map) {
    if (target == null) {
      return actor + " fires an arrow into empty space.";
    }

    if (rand.nextInt(100) < 25) {
      target.hurt(5);
      return actor + " shoots " + target + " with a bow (distance " + distance + ") for 5 damage.";
    } else {
      return actor + " fires an arrow at " + target + " (distance " + distance + ") but misses.";
    }
  }

  @Override
  public String menuDescription(Actor actor) {
    return actor + " shoots " + target + " (range " + distance + " ) with Bow.";
  }

}
