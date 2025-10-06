package game.status;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Poison status effect that deals periodic damage to an actor.
 *
 * <p>Behavior:
 * <ul>
 *   <li>Lasts for {@code remainingTurns} ticks.</li>
 *   <li>Each tick deals {@code damagePerTurn} hit points of damage.</li>
 *   <li>Stackable: multiple PoisonEffect instances on the same actor sum their damage.</li>
 * </ul>
 *
 * <p>This class is intentionally simple — it delegates actual HP modification to
 * the engine {@link Actor#hurt(int)} method so it integrates cleanly with the
 * existing health/attribute system.
 *
 * @author Ahmed
 */
public class PoisonEffect implements StatusEffect {

  /** Number of ticks remaining for this poison instance. */
  private int remainingTurns;

  /** Damage inflicted each tick while this effect is active. */
  private final int damagePerTurn;

  /**
   * Create a PoisonEffect.
   *
   * @param turns number of turns the poison lasts
   * @param damagePerTurn damage applied to the target at each tick
   */
  public PoisonEffect(int turns, int damagePerTurn) {
    this.remainingTurns = turns;
    this.damagePerTurn = damagePerTurn;
  }

  /**
   * Apply one tick of poison to the target actor.
   *
   * @param target the actor receiving the effect
   * @param map the current game map (provided by contract; unused here)
   */
  @Override
  public void applyEffect (Actor target, GameMap map) {
    target.hurt(damagePerTurn);
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
