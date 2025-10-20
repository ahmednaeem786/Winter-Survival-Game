package game.status;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * A stackable bleeding status effect.
 *
 * <p>When applied, this effect deals a fixed amount of damage to the target
 * each tick (turn) for a limited number of turns. Multiple instances of
 * {@code BleedEffect} can be added to a {@code GameActor} and their damage
 * stacks.
 *
 * <p>Example: two BleedEffect(2, 10) instances cause 20 damage per tick for
 * the next tick, then 10 damage on the following tick (each instance expires
 * after 2 decrements).
 *
 * @author Ahmed
 */
public class BleedEffect implements StatusEffect {

  /** How many more ticks this effect will apply for. */
  private int remainingTurns;

  /** Damage applied to the target each tick. */
  private final int damagePerTurn;

  /**
   * Create a bleed effect.
   *
   * @param turns number of turns the bleed should last
   * @param damagePerTurn damage dealt each tick while bleeding
   */
  public BleedEffect (int turns, int damagePerTurn) {
    this.remainingTurns = turns;
    this.damagePerTurn = damagePerTurn;
  }

  /**
   * Apply the bleed damage to the target. This method is expected to be called
   * once per tick by the actor's status-effect processing routine.
   *
   * @param target the actor receiving the effect
   * @param map the game map (provided for symmetry with other effects; not used here)
   */
  @Override
  public void applyEffect (Actor target, GameMap map) {
    target.hurt(damagePerTurn);
  }

  /**
   * Decrease the remaining duration by one tick.
   */
  @Override
  public void decrementDuration() {
    remainingTurns--;
  }

  /**
   * @return {@code true} when the effect has expired and should be removed
   */
  @Override
  public boolean isExpired() {
    return remainingTurns <= 0;
  }

  /**
   * @return number of remaining turns this effect will still apply
   */
  @Override
  public int remainingTurns() {
    return remainingTurns;
  }

}
