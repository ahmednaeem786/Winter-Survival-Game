package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.GameActor;
import game.items.Axe;
import game.status.BleedEffect;
import java.util.Random;

public class AxeAttackAction extends Action {

  private final Axe axe;
  private final GameActor target;
  private final String direction;
  private final Random rand = new Random();

  public AxeAttackAction(Axe axe, GameActor target, String direction) {
    this.axe = axe;
    this.target = target;
    this.direction = direction;
  }

  @Override
  public String execute(Actor actor, GameMap map) {
    if (target == null) {
      return actor + " swings the axe at nothing.";
    }

    if (rand.nextInt(100) < 75) {
      target.hurt(15);
      StringBuilder result = new StringBuilder(actor + " hits " + target + " with an axe for 15 damage.");

      if (rand.nextInt(100) < 50) {
        target.addStatusEffect(new BleedEffect(2, 10));
        result.append(" " + target + " starts bleeding (10 damage per turn for 2 turns).");
      }
      return result.toString();
    } else {
      return actor + " swings an axe at " + target + " but misses.";
    }
  }

  @Override
  public String menuDescription(Actor actor) {
    return actor + " attacks " + target + " (" + direction + ") with Axe";
  }

}
