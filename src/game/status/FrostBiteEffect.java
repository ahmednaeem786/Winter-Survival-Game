package game.status;

import static edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation.DECREASE;
import static game.abilities.Abilities.WARMTH;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Frost-bite status effect that reduces an actor's WARMTH each tick.
 *
 * <p>Behavior:
 * <ul>
 *   <li>Lasts for {@code remainingTurns} ticks.</li>
 *   <li>Each tick reduces the target's {@code WARMTH} by {@code warmthReductionPerTurn}
 *       using the engine attribute API.</li>
 *   <li>The effect is stackable: multiple instances are additive.</li>
 * </ul>
 *
 * <p>Note: the tundra-immunity check (skip applying frostbite to actors spawned from
 * tundra) is intentionally left commented out. If your project defines a suitable
 * capability (for example {@code EnvironmentalStatus.SPAWNED_FROM_TUNDRA}), re-enable
 * the check and import the enum so tundra-spawned actors remain immune.
 *
 * @author Ahmed
 */
public class FrostBiteEffect implements StatusEffect {

  /** Number of ticks remaining for this effect. */
  private int remainingTurns;

  /** Amount of WARMTH to remove each tick for this effect instance. */
  private final int warmthReductionPerTurn;

  /**
   * Construct a new FrostBiteEffect.
   *
   * @param turns number of ticks the effect lasts
   * @param reductionPerTurn amount of WARMTH to remove each tick
   */
  public FrostBiteEffect(int turns, int reductionPerTurn) {
    this.remainingTurns = turns;
    this.warmthReductionPerTurn = reductionPerTurn;
  }

  /**
   * Apply one tick of the frostbite effect to {@code target}.
   *
   * <p>This method attempts to reduce the actor's WARMTH stat if the actor exposes that
   * statistic via the engine's attribute API. If the actor does not have the WARMTH
   * statistic, this effect is a no-op for that actor (safe behaviour).
   *
   * @param target the actor receiving the effect
   * @param map the game map (unused here but provided by the contract)
   */
  @Override
  public void applyEffect(Actor target, GameMap map) {

//    if (target.hasAbility(SPAWNED_FROM_TUNDRA)) {
//      return;
//    }

    //Only applying if the actor has the WARMTH statistic
    if (target.hasStatistic(WARMTH)) {
      target.modifyAttribute(WARMTH, DECREASE, warmthReductionPerTurn);
    }
  }

  /** Decrease the remaining duration by one tick. */
  @Override
  public void decrementDuration() {
    remainingTurns--;
  }

  /** Returns true when the effect has expired (no remaining turns). */
  @Override
  public boolean isExpired() {
    return remainingTurns <= 0;
  }

  /** Returns how many ticks remain for this effect. */
  @Override
  public int remainingTurns() {
    return remainingTurns;
  }

}
