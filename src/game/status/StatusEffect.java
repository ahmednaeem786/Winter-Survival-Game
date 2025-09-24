package game.status;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

public interface StatusEffect {
  void applyEffect(Actor target, GameMap map);
  void decrementDuration();
  boolean isExpired();
  int remainingTurns();
}
