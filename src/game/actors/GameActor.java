package game.actors;

import static game.capabilities.StatusAbilities.CAN_RECIEVE_STATUS;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import game.status.StatusEffect;
import game.status.StatusRecipient;
import game.status.StatusRecipientRegistry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Base class for all game-specific actors that can receive timed status effects
 * such as bleed or burn.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Maintain a list of active {@link StatusEffect} instances.</li>
 *   <li>Provide {@link #addStatusEffect(StatusEffect)} so other systems can apply effects.</li>
 *   <li>Tick and expire status effects each turn via {@link #tickStatusEffects(GameMap)}.</li>
 *   <li>Register itself with {@link StatusRecipientRegistry} so non-game code can
 *       obtain a {@link StatusRecipient} for an {@link Actor} without using instanceof.</li>
 * </ul>
 *
 * <p>Note: this class enables the {@code CAN_RECIEVE_STATUS} capability in its
 * constructor so capability-based checks (hasAbility(...)) can be used elsewhere
 * to decide whether to attempt status-effect delivery.
 *
 * @author Ahmed
 */
public abstract class GameActor extends Actor implements StatusRecipient {

  /**
   * Active status effects currently applied to this actor. Effects are applied in
   * insertion order each tick and may stack depending on their implementations.
   */
  protected final List<StatusEffect> statusEffects = new ArrayList<>();

  /**
   * Constructs a new GameActor and registers it as a {@link StatusRecipient}.
   *
   * @param name the actor's display name
   * @param displayChar the character shown on the map
   * @param hitPoints the initial hit points
   */
  public GameActor(String name, char displayChar, int hitPoints) {
    super(name, displayChar, hitPoints);

    // Exposing capability so other code can check actor.hasAbility(CAN_RECIEVE_STATUS)
    // before attempting to add timed effects.
    this.enableAbility(CAN_RECIEVE_STATUS);

    // Register this actor as a StatusRecipient so ground/actions can look it up
    // without using instanceof/casts.
    StatusRecipientRegistry.register(this, this);
  }

  /**
   * Add a status effect to this actor.
   *
   * @param effect the status effect to add (may be stackable)
   */
  public void addStatusEffect (StatusEffect effect) {
    statusEffects.add(effect);
  }

  /**
   * Apply one tick for every active {@link StatusEffect}, decrementing their
   * durations and removing expired effects.
   *
   * <p>This method should be called once at the start of the actor's turn (for
   * example in {@code playTurn(...)}) so effects are processed consistently.
   *
   * @param map the current game map (passed to effects that need contextual info)
   */
  public void tickStatusEffects(GameMap map) {
    if (statusEffects.isEmpty()) {
      return;
    }

    Iterator<StatusEffect> it = statusEffects.iterator();
    while (it.hasNext()) {
      StatusEffect eff = it.next();
      eff.applyEffect(this, map);
      eff.decrementDuration();
      if (eff.isExpired()) {
        it.remove();
      }
    }
  }

}
