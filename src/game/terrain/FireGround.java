package game.terrain;

import static game.capabilities.StatusAbilities.CAN_RECIEVE_STATUS;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.status.BurnEffect;
import game.status.StatusRecipient;
import game.status.StatusRecipientRegistry;

public class FireGround extends Ground{

  private int remainingTurns;
  private final Ground underlying;

  public FireGround(int duration, Ground underlying) {
    super('^', "Fire Ground");
    this.remainingTurns = duration;
    this.underlying = underlying;
  }

  @Override
  public void tick(Location location) {
    super.tick(location);
    Actor actor = location.getActor();
    if (actor != null) {

      if (actor.hasAbility(CAN_RECIEVE_STATUS)) {
        StatusRecipient r = StatusRecipientRegistry.getRecipient(actor);
        if (r != null) {
          r.addStatusEffect(new BurnEffect(5, 5));
        } else {
          actor.hurt(5);
        }
      } else {
        actor.hurt(5);
      }
    }
    remainingTurns--;
    if (remainingTurns <= 0) {
      location.setGround(underlying);
    }
  }
}
