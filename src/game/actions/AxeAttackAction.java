package game.actions;

import static game.abilities.Abilities.COLD_RESISTANCE;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.GameActor;
import game.coating.CoatingType;
import game.items.Axe;
import game.status.BleedEffect;
import game.status.FrostBiteEffect;
import game.status.PoisonEffect;
import java.util.Random;

/**
 * An {@link Action} that represents attacking a target with an {@link Axe}.
 *
 * <p>Behavior:
 * <ul>
 *   <li>75% chance to hit and deal 15 immediate damage.</li>
 *   <li>On a successful hit there's a 50% chance to apply a {@link BleedEffect}
 *       (2 turns, 10 damage per turn). Bleed effects are stackable.</li>
 *   <li>If the weapon is coated, the coating's status effect is applied on a
 *       successful hit (e.g. Poison for YEWBERRY, Frostbite for SNOW).</li>
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
   * <p>Returns a descriptive string describing hit/miss and whether bleed or
   * coating effects were applied.
   *
   * @param actor the actor performing the attack
   * @param map the game map (not used here, included for engine contract)
   * @return summary String of the attack outcome
   */
  @Override
  public String execute(Actor actor, GameMap map) {

    //if target is null, there's nothing to attack.
    if (target == null) {
      return actor + " swings the axe at nothing.";
    }

    // 75% chance to hit
    if (rand.nextInt(100) < 75) {
      // Applying immediate base damage.
      target.hurt(15);
      StringBuilder result = new StringBuilder(actor + " hits " + target + " with an axe for 15 damage.");

      // 50% chance to apply bleed on successful hit.
      if (rand.nextInt(100) < 50) {
        target.addStatusEffect(new BleedEffect(2, 10));
        result.append(" " + target + " starts bleeding (10 damage per turn for 2 turns).");
      }

      /*
       * Apply coating effects only when the attack actually hits.
       * Coatings are stored on the weapon (if present) and are applied in addition
       * to the base weapon effects. The weapon may be null (defensive check).
       */
      if (axe != null) {
        CoatingType coat = axe.getCoating();
        if (coat == CoatingType.YEWBERRY) {
          target.addStatusEffect(new PoisonEffect(5, 4));
          result.append(" " + target + " is poisoned (4 damage per turn for 5 turns).");
        } else if (coat == CoatingType.SNOW) {
          // Frostbite application: the FrostBiteEffect itself should respect tundra immunity
          // (e.g., skip actors spawned from tundra). We only attach the effect here.
          target.addStatusEffect(new FrostBiteEffect(3, 1));
          if (!target.hasAbility(COLD_RESISTANCE)) {
            result.append(" " + target + " is frostbitten (reduces WARMTH by 1 for 3 turns).");
          }
        }
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
