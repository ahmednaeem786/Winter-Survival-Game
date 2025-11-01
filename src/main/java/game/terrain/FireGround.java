package game.terrain;

import static game.capabilities.StatusAbilities.CAN_RECIEVE_STATUS;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.status.BurnEffect;
import game.status.StatusRecipient;
import game.status.StatusRecipientRegistry;

/**
 * Temporary fire ground that damages actors standing on it and then reverts
 * to an underlying ground when its duration expires.
 *
 * <p>Behavior:
 * <ul>
 *   <li>If an actor is present and advertises the {@code CAN_RECIEVE_STATUS}
 *       capability, the registry is consulted and a {@link BurnEffect}
 *       (5 turns, 5 dmg/turn) is added to the actor (stackable).</li>
 *   <li>If there is no registered StatusRecipient for the actor or the actor
 *       does not advertise the capability, an immediate {@code actor.hurt(5)}
 *       is applied as a defensive fallback.</li>
 *   <li>After each tick the remaining duration is decremented; when it reaches
 *       zero the ground reverts to the provided underlying ground.</li>
 * </ul>
 *
 * <p>Uses symbol '^' on the map to indicate active fire.
 *
 * @author Ahmed
 */
public class FireGround extends Ground{

  /** Remaining ticks the fire will persist. */
  private int remainingTurns;

  /**
   * Create a FireGround that lasts for {@code duration} ticks and reverts to
   * {@code underlying} when expired.
   *
   * @param duration number of ticks the fire remains active
   * @param underlying the ground to restore after fire expires
   */
  public FireGround(int duration, Ground underlying) {
    super('^', "Fire Ground");
    this.remainingTurns = duration;
  }

  /**
   * Called by the engine each tick. Applies burn/instant damage to any actor
   * on this location, decrements duration, and reverts to the underlying
   * ground when time runs out.
   *
   * @param location the Location of this ground tile
   */
  @Override
  public void tick(Location location) {
    super.tick(location);
    Actor actor = location.getActor();
    if (actor != null) {

      //If actor has capability, then attempt to deliver a timed BurnEffect via registry
      if (actor.hasAbility(CAN_RECIEVE_STATUS)) {
        StatusRecipient r = StatusRecipientRegistry.getRecipient(actor);
        if (r != null) {
          r.addStatusEffect(new BurnEffect(5, 5));
        } else {
          //Defensive fallback in case no StatusRecipient is registered.
          actor.hurt(5);
        }
      } else {
        //Actor can't recieve timed effect so apply immediate damage.
        actor.hurt(5);
      }
    }

    //Decreasing remaining lifetime and reverting to underlying ground as soon as expired.
    remainingTurns--;
    if (remainingTurns <= 0) {
      location.setGround(new Dirt());
    }
  }
}
