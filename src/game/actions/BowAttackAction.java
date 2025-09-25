package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actors.GameActor;
import game.coating.CoatingType;
import game.items.Bow;
import game.status.FrostBiteEffect;
import game.status.PoisonEffect;
import java.util.Random;

/**
 * An {@link Action} that represents firing a {@link Bow} at a target within range.
 *
 * <p>Behavior:
 * <ul>
 *   <li>25% chance to hit and deal 5 immediate damage.</li>
 *   <li>If the attack hits and the bow is coated, the coating's effect is applied:
 *       <ul>
 *         <li>{@link CoatingType#YEWBERRY} &rarr; {@link PoisonEffect}(5 turns, 4 dmg/turn)</li>
 *         <li>{@link CoatingType#SNOW} &rarr; {@link FrostBiteEffect}(3 turns, -1 WARMTH/turn)</li>
 *       </ul>
 *   </li>
 *   <li>Coating effects are only applied on a successful hit and the effect classes
 *       themselves implement any further immunity checks (e.g., tundra immunity for frostbite).</li>
 * </ul>
 *
 * <p>The action is constructed per-target by {@link game.items.Bow#allowableActions},
 * so {@code execute} assumes the provided {@link GameActor} target is valid.
 *
 * @author Ahmed
 */
public class BowAttackAction extends Action {

  /** The bow used to perform the attack (kept for potential future use). */
  private final Bow bow;

  /** Target of this attack. */
  private final GameActor target;

  /** Distance (in tiles) from attacker to target, used for messaging. */
  private final int distance;

  /** Random Number Generator for hit rolls. */
  private final Random rand = new Random();

  /**
   * Construct a BowAttackAction for the given target.
   *
   * @param bow the Bow item used
   * @param target the target GameActor
   * @param distance distance from attacker to target (1..3)
   */
  public BowAttackAction (Bow bow, GameActor target, int distance) {
    this.bow = bow;
    this.target = target;
    this.distance = distance;
  }

  /**
   * Execute the bow attack.
   *
   * <p>Coating effects are applied only when the arrow hits. The result message
   * includes whether the attack hit and any coating effect applied.
   *
   * @param actor the actor performing the attack
   * @param map the game map (provided by engine; not used here)
   * @return a human-readable description of the result
   */
  @Override
  public String execute(Actor actor, GameMap map) {

    // if no target is supplied, describe firing into empty space.
    if (target == null) {
      return actor + " fires an arrow into empty space.";
    }

    // 25% hit chance
    if (rand.nextInt(100) < 25) {
      // Applying immediate damage.
      target.hurt(5);
      StringBuilder result = new StringBuilder(
          actor + " shoots " + target + " with a bow (distance " + distance + ") for 5 damage.");

      // If the bow exists and has a coating, apply the coating effect on a successful hit.
      if (bow != null) {
        CoatingType coat = bow.getCoating();
        if (coat == CoatingType.YEWBERRY) {
          target.addStatusEffect(new PoisonEffect(5, 4));
          result.append(" " + target + " is poisoned (4 dmg/turn for 5 turns).");
        } else if (coat == CoatingType.SNOW) {

          target.addStatusEffect(new FrostBiteEffect(3, 1));
          result.append(" " + target + " is frostbitten (reduces WARMTH by 1 for 3 turns).");
        }
      }

      return result.toString();
    } else {
      return actor + " fires an arrow at " + target + " (distance " + distance + ") but misses.";
    }
  }
  /**
   * Menu string shown to the player when selecting this action.
   *
   * @param actor the actor that would perform this action
   * @return menu description
   */
  @Override
  public String menuDescription(Actor actor) {
    return actor + " shoots " + target + " (range " + distance + " ) with Bow.";
  }

}
