package game.status;

/**
 * A target capable of receiving {@link StatusEffect}s.
 *
 * <p>Implemented by {@link game.actors.GameActor} so external systems (grounds,
 * actions) can deliver status effects without needing to cast to game-specific
 * actor classes.
 *
 * @author Ahmed
 */
public interface StatusRecipient {

  /**
   * Add a status effect to this recipient.
   *
   * @param effect the status effect to add (may be stacked)
   */
  void addStatusEffect(StatusEffect effect);
}
