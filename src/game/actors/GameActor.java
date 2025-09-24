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

public abstract class GameActor extends Actor implements StatusRecipient {

  protected final List<StatusEffect> statusEffects = new ArrayList<>();

  public GameActor(String name, char displayChar, int hitPoints) {
    super(name, displayChar, hitPoints);
    this.enableAbility(CAN_RECIEVE_STATUS);

    StatusRecipientRegistry.register(this, this);
  }

  public void addStatusEffect (StatusEffect effect) {
    statusEffects.add(effect);
  }

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
