package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.GameActor;
import game.items.Torch;
import game.status.BurnEffect;
import game.terrain.Dirt;
import game.terrain.FireGround;
import java.util.Random;

public class TorchAttackAction extends Action {

  private final Torch torch;
  private final GameActor target;
  private final String direction;
  private final Random rand = new Random();

  public TorchAttackAction(Torch torch, GameActor target, String direction) {
    this.torch = torch;
    this.target = target;
    this.direction = direction;
  }

  @Override
  public String execute(Actor actor, GameMap map) {
    if (target == null) {
      return actor + " swings the torch at nothing.";
    }

    if (rand.nextInt(100) < 50) {
      target.hurt(10);
      StringBuilder sb = new StringBuilder(actor + " hits " + target + " with a torch for 10 damage.");

      target.addStatusEffect(new BurnEffect(7, 3));
      sb.append(" " + target + " is burning at 3 damage per turn for 7 turns.");

      spawnFireAround(actor, map, 5);
      sb.append(" Fire spawns around " + actor + " and will burn for 5 turns.");
      return sb.toString();
    } else {
      return actor + " swings a torch at " + target + " (" + direction + ") but misses.";
    }
  }

  @Override
  public String menuDescription(Actor actor) {
    return actor + " attacks " + target + " (" + direction + ") with Torch.";
  }

  private void spawnFireAround (Actor actor, GameMap map, int duration) {
    Location center = map.locationOf(actor);

    center.setGround(new FireGround(duration, new Dirt()));
    for (Exit exit : center.getExits()) {
      Location loc = exit.getDestination();
      loc.setGround(new FireGround(duration, new Dirt()));
    }
  }

}
