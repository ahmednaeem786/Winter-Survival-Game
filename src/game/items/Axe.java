package game.items;

import static game.capabilities.StatusAbilities.CAN_RECIEVE_STATUS;


import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AxeAttackAction;
import game.actors.GameActor;
import game.coating.Coatable;
import game.coating.CoatingType;

/**
 * A melee weapon that allows the {@link Actor} to attack adjacent targets.
 *
 * <p>The {@code Axe} deals flat damage and has a chance to inflict
 * {@link game.status.BleedEffect} when attacking valid {@link GameActor}s
 * that have the {@link game.capabilities.StatusAbilities#CAN_RECIEVE_STATUS} ability.
 *
 * <p>It generates {@link AxeAttackAction} instances for each attackable adjacent
 * target at runtime via {@link #allowableActions(Actor, GameMap)}.
 *
 * @author Ahmed
 */
public class Axe extends Item implements Coatable {
  private CoatingType coating = CoatingType.NONE;

  /**
   * Creates a new Axe item.
   * Symbol: {@code 'p'}.
   * Portable: {@code true}.
   */
  public Axe() {
    super("Axe", 'p', true);
  }

  /**
   * Generates attack actions against all valid adjacent targets.
   *
   * @param owner The current owner of this Axe.
   * @param map   The map where the owner resides.
   * @return An {@link ActionList} containing possible axe attack actions.
   */
  @Override
  public ActionList allowableActions(Actor owner, GameMap map) {
    ActionList actions = new ActionList();

    Location ownerLocation = map.locationOf(owner);
    for (Exit exit : ownerLocation.getExits()) {
      Location adjacent = exit.getDestination();

      if (adjacent.containsAnActor()) {
        Actor maybeTarget = adjacent.getActor();

        //Only allowed to attack actors that can recieve status effects
        if (maybeTarget.hasAbility(CAN_RECIEVE_STATUS)) {
          GameActor gameTarget = (GameActor) maybeTarget;
          actions.add(new AxeAttackAction(this, gameTarget, exit.getName()));
        }
      }
    }
    return actions;
  }

  @Override
  public void setCoating(CoatingType c) {
    this.coating = c == null ? CoatingType.NONE : c;
  }

  @Override
  public CoatingType getCoating() {
    return coating;
  }

  @Override
  public void clearCoating() {
    this.coating = CoatingType.NONE;
  }
}
