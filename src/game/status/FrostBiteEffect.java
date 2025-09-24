package game.status;

import static edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation.DECREASE;
import static game.abilities.Abilities.WARMTH;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public class FrostBiteEffect implements StatusEffect {
  private int remainingTurns;
  private final int warmthReductionPerTurn;

  public FrostBiteEffect(int turns, int reductionPerTurn) {
    this.remainingTurns = turns;
    this.warmthReductionPerTurn = reductionPerTurn;
  }

  @Override
  public void applyEffect(Actor target, GameMap map) {

//    if (target.hasAbility(SPAWNED_FROM_TUNDRA)) {
//      return;
//    }

    if (target.hasStatistic(WARMTH)) {
      target.modifyAttribute(WARMTH, DECREASE, warmthReductionPerTurn);
    }
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
