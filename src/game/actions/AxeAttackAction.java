package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.GameActor;
import game.items.Axe;
import game.status.BleedEffect;
import java.util.Random;

/**
 * An {@link Action} that represents attacking a target with an {@link Axe}.
 *
 * <p>Behavior:
 * <ul>
 *   <li>75% chance to hit and deal 15 immediate damage.</li>
 *   <li>On a successful hit there's a 50% chance to apply a {@link BleedEffect}
 *       (2 turns, 10 damage per turn). Bleed effects are stackable.</li>
 * </ul>
 *
 * <p>This Action is constructed per-target (the target is supplied in the
 * constructor) so {@code execute} can safely assume the target is a {@link GameActor}
 * that accepts status effects.
 *
 * @author Ahmed
 */
public class AxeAttackAction extends Action {

  /** The axe item used to perform this attack (kept for potential extensions). */
  private final Axe axe;

  /** Target of this attack; must be a GameActor so we can add status effects. */
  private final GameActor target;

  /** Direction string used for menu/description (e.g., "East"). */
  private final String direction;

  /** Random number generator for hit/status rolls. */
  private final Random rand = new Random();

  /**
   * Create an AxeAttackAction for a specific target.
   *
   * @param axe the axe item used (may be null in some contexts)
   * @param target the target GameActor to attack
   * @param direction textual direction from attacker to target (for menu description)
   */
  public AxeAttackAction(Axe axe, GameActor target, String direction) {
    this.axe = axe;
    this.target = target;
    this.direction = direction;
  }

  /**
   * Executes the axe attack.
   *
   * <p>Returns a descriptive string describing hit/miss and whether bleed was applied.
   *
   * @param actor the actor performing the attack
   * @param map the game map (not used here, included for engine contract)
   * @return summary String of the attack outcome
   */
  @Override
  public String execute(Actor actor, GameMap map) {
    if (target == null) {
      return actor + " swings the axe at nothing.";
    }

    //75% chance to hit
    if (rand.nextInt(100) < 75) {
      target.hurt(15);
      StringBuilder result = new StringBuilder(actor + " hits " + target + " with an axe for 15 damage.");

      //50% chance to apply bleed
      if (rand.nextInt(100) < 50) {
        target.addStatusEffect(new BleedEffect(2, 10));
        result.append(" " + target + " starts bleeding (10 damage per turn for 2 turns).");
      }
      return result.toString();
    } else {
      return actor + " swings an axe at " + target + " but misses.";
    }
  }

  /**
   * Menu description shown to the player for this action.
   *
   * @param actor the actor that would perform this action (used in description)
   * @return a human-readable menu string
   */
  @Override
  public String menuDescription(Actor actor) {
    return actor + " attacks " + target + " (" + direction + ") with Axe";
  }

}
