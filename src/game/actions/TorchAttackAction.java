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

/**
 * An {@link Action} representing attacking a target with a {@link Torch}.
 *
 * <p>Behavior:
 * <ul>
 *   <li>50% chance to hit and deal 10 immediate damage.</li>
 *   <li>On hit: applies a {@link BurnEffect} (7 turns, 3 damage/turn) to the target.</li>
 *   <li>On hit: spawns {@link FireGround} in the attacker's adjacent tiles for a short duration.</li>
 * </ul>
 *
 * <p>This action is created per-target by {@link game.items.Torch#allowableActions} so
 * {@code execute} can safely assume the provided target is a valid {@link GameActor}.
 *
 * @author Ahmed
 */
public class TorchAttackAction extends Action {

  /** Torch item performing this action (reserved for future use/metadata). */
  private final Torch torch;

  /** Target receiving the attack and possible burn effect. */
  private final GameActor target;

  /** Direction string used for menu descriptions (e.g., "East"). */
  private final String direction;

  /** Random number generator used for hit/burn rolls. */
  private final Random rand = new Random();

  /**
   * Create a TorchAttackAction for the given target.
   *
   * @param torch the torch item used
   * @param target the target GameActor
   * @param direction textual direction from attacker to target (for menu display)
   */
  public TorchAttackAction(Torch torch, GameActor target, String direction) {
    this.torch = torch;
    this.target = target;
    this.direction = direction;
  }

  /**
   * Execute the torch attack.
   *
   * <p>Returns a descriptive string indicating hit/miss, damage dealt, burn applied, and
   * whether fire was spawned around the attacker.
   *
   * @param actor the actor performing the attack
   * @param map the game map (required by engine contract)
   * @return human-readable summary of the attack outcome
   */
  @Override
  public String execute(Actor actor, GameMap map) {
    if (target == null) {
      return actor + " swings the torch at nothing.";
    }

    // 50% hit chance
    if (rand.nextInt(100) < 50) {
      target.hurt(10);
      StringBuilder sb = new StringBuilder(actor + " hits " + target + " with a torch for 10 damage.");

      //Applying long burn effect i.e. 7 turns and 3 damage per turn
      target.addStatusEffect(new BurnEffect(7, 3));
      sb.append(" " + target + " is burning at 3 damage per turn for 7 turns.");

      // spawning temporary fire on adjacent tiles and not on the attacker's own tile
      spawnFireAround(actor, map, 5);
      sb.append("\nFire spawns around " + actor + " and will burn for 5 turns.");
      return sb.toString();
    } else {
      return actor + " swings a torch at " + target + " (" + direction + ") but misses.";
    }
  }

  /**
   * Menu description shown to the player for this action.
   *
   * @param actor the actor that would perform this action
   * @return a brief menu string
   */
  @Override
  public String menuDescription(Actor actor) {
    return actor + " attacks " + target + " (" + direction + ") with Torch.";
  }

  /**
   * Spawn {@link FireGround} on all adjacent locations around the provided actor.
   * The attacker's own tile is intentionally not set on fire (so attacker is not
   * immediately penalised for spawning fire).
   *
   * @param actor the actor at the center of the spawn
   * @param map current game map
   * @param duration how many ticks the fire should last
   */
  private void spawnFireAround (Actor actor, GameMap map, int duration) {
    Location center = map.locationOf(actor);

    // Placing fire on adjacent tiles only
    for (Exit exit : center.getExits()) {
      Location loc = exit.getDestination();
      loc.setGround(new FireGround(duration, new Dirt()));
    }
  }

}
