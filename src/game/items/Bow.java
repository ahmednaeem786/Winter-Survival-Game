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
import game.coating.Coatable;
import game.coating.CoatingType;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * A ranged weapon that exposes BowAttackAction(s) for targets within a
 * 3-tile radius. Targets are discovered using a breadth-first search (BFS)
 * from the owner's location.
 *
 * <p>Only actors that advertise the {@code CAN_RECIEVE_STATUS} capability are
 * considered valid targets (this keeps the action-creation boundary capability-driven).
 *
 * @author Ahmed
 */
public class Bow extends Item implements Coatable {

  private CoatingType coating = CoatingType.NONE;

  /**
   * Constructs a new Bow item.
   * Symbol: {@code 'c'}, portable: {@code true}.
   */
  public Bow() {
    super("Bow", 'c', true);
  }

  /**
   * Produces BowAttackAction instances for each valid target within range 3.
   * The BFS guarantees that each reachable location is visited once, and
   * that the {@code depth} passed to BowAttackAction corresponds to the
   * shortest distance (in tiles) from the owner.
   *
   * @param owner the actor carrying the bow
   * @param map the game map
   * @return an ActionList containing BowAttackAction for each discovered target
   */
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

      // Skipping start location as a target (depth==0)
      if (depth > 0 && loc.containsAnActor()) {
        Actor maybeTarget = loc.getActor();
        if (maybeTarget.hasAbility(CAN_RECIEVE_STATUS)) {
          GameActor gameTarget = (GameActor) maybeTarget;
          actions.add(new BowAttackAction(this, gameTarget, depth));
        }
      }
      // Continuing Breadth First Search until range (depth) limit reached
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

  /**
   * Simple helper that pairs a {@link Location} with its BFS depth.
   * Kept package-private and minimal; no additional behavior required.
   */
  private static class LocationDepth {
    final Location location;
    final int depth;

    LocationDepth(Location l, int d) {
      this.location = l;
      this.depth = d;
    }
  }

  @Override
  public void setCoating(CoatingType c) {
    this.coating = c == null ? CoatingType.NONE : c;
  }

  @Override
  public CoatingType getCoating () {
    return coating;
  }

  @Override
  public void clearCoating() {
    this.coating = CoatingType.NONE;
  }
}
