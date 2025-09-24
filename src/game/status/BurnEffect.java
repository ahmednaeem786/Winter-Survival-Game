package game.status;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public class BurnEffect implements StatusEffect {

  private int remainingTurns;
  private final int damagePerTurn;
  public BurnEffect (int turns, int damagePerTurn) {
    this.remainingTurns = turns;
    this.damagePerTurn = damagePerTurn;
  }

  @Override
  public void applyEffect(Actor target, GameMap map) {
    target.hurt(damagePerTurn);
  }

  @Override
  public void decrementDuration() {
    remainingTurns--;
  }

  @Override
  public boolean isExpired() {
    return remainingTurns <= 0;
  }

  @Override
  public int remainingTurns() {
    return remainingTurns;
  }

}
