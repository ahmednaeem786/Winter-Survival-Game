package game.status;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * A stackable burning status effect.
 *
 * <p>This effect deals a fixed amount of damage to the target each tick (turn)
 * for a limited number of turns. Multiple instances of {@code BurnEffect} can be
 * applied to a {@code GameActor} and their damage stacks.
 *
 * <p>Example: applying BurnEffect(7, 3) will cause the target to lose 3 HP at
 * the start of each of the next 7 ticks.
 *
 * @author Ahmed
 */
public class BurnEffect implements StatusEffect {

  /** Remaining ticks for which this burn will apply. */
  private int remainingTurns;

  /** Damage dealt each tick while burning. */
  private final int damagePerTurn;

  /**
   * Create a BurnEffect.
   *
   * @param turns number of turns the burn should last
   * @param damagePerTurn damage dealt each tick while burning
   */
  public BurnEffect (int turns, int damagePerTurn) {
    this.remainingTurns = turns;
    this.damagePerTurn = damagePerTurn;
  }

  /**
   * Apply the burn damage to the target. Called once per tick by the actor's
   * status-effect processing routine.
   *
   * @param target the actor receiving the effect
   * @param map the game map (provided for symmetry with other effects; not used here)
   */
  @Override
  public void applyEffect(Actor target, GameMap map) {
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
