package game.status;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Represents a timed status effect that can be applied to an {@link Actor}.
 *
 * <p>Implementations should be lightweight value-like objects that describe
 * behaviour which is executed once per tick via {@link #applyEffect(Actor, GameMap)}.
 * The lifecycle is: apply -> decrementDuration -> removed when {@link #isExpired()} is true.
 *
 * <p>Examples: {@link BleedEffect}, {@link BurnEffect}.
 *
 * @author Ahmed
 */
public interface StatusEffect {

  /**
   * Apply this effect to the target for one tick.
   *
   * @param target the actor receiving the effect
   * @param map the current game map (provided so effects can use contextual information if needed)
   */
  void applyEffect(Actor target, GameMap map);

  /**
   * Decrement the remaining duration of this effect by one tick.
   * Implementations should update their internal remaining-turns counter.
   */
  void decrementDuration();

  /**
   * @return {@code true} if this effect has expired and should be removed
   *         from the host actor's active-effect list.
   */
  boolean isExpired();

  /**
   * @return number of remaining turns this effect will still apply
   */
  int remainingTurns();
}
