package game.items;

import static game.capabilities.StatusAbilities.CAN_RECIEVE_STATUS;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.BowAttackAction;
import game.actors.GameActor;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class Bow extends Item {

  public Bow() {
    super("Bow", 'c', true);
  }

  @Override
  public ActionList allowableActions(Actor owner, GameMap map) {
    ActionList actions = new ActionList();
    Location start = map.locationOf(owner);

    Set<Location> visited = new HashSet<>();
    Queue<LocationDepth> q = new ArrayDeque<>();
    q.add (new LocationDepth(start, 0));
    visited.add(start);

    while (!q.isEmpty()) {
      LocationDepth ld = q.poll();
      Location loc = ld.location;
      int depth = ld.depth;

      if (depth > 0 && loc.containsAnActor()) {
        Actor maybeTarget = loc.getActor();
        if (maybeTarget.hasAbility(CAN_RECIEVE_STATUS)) {
          GameActor gameTarget = (GameActor) maybeTarget;
          actions.add(new BowAttackAction(this, gameTarget, depth));
        }
      }

      if (depth < 3) {
        for (Exit exit : loc.getExits()) {
          Location next = exit.getDestination();
          if (!visited.contains(next)) {
            visited.add(next);
            q.add(new LocationDepth(next, depth + 1));
          }
        }
      }
    }

    return actions;
  }

  private static class LocationDepth {
    final Location location;
    final int depth;

    LocationDepth(Location l, int d) {
      this.location = l;
      this.depth = d;
    }
  }

}
