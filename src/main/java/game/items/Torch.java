package game.items;

import static game.capabilities.StatusAbilities.CAN_RECIEVE_STATUS;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TorchAttackAction;
import game.actors.GameActor;

/**
 * Torch item that exposes {@link TorchAttackAction} instances for adjacent
 * targets that can receive timed status effects.
 *
 * <p>The torch is a simple melee/ranged hybrid weapon in the game that, when
 * used, may apply burning effects to targets and spawn environmental fire.
 *
 * <p>{@link #allowableActions(Actor, GameMap)} returns one action per valid
 * adjacent target. Valid targets are detected via the {@code CAN_RECIEVE_STATUS}
 * capability to avoid direct instanceof checks in action logic.
 *
 * @author Ahmed
 */
public class Torch extends Item {

  /**
   * Create a new Torch.
   * Symbol: 'y', portable: true.
   */
  public Torch() {
    super("Torch", 'y', true);
  }

  /**
   * Generate torch attack actions for each adjacent actor that advertises the
   * {@code CAN_RECIEVE_STATUS} capability.
   *
   * @param owner the Actor carrying this torch
   * @param map the GameMap the owner is on
   * @return an ActionList containing TorchAttackAction instances for each valid adjacent target
   */
  @Override
  public ActionList allowableActions(Actor owner, GameMap map) {
    ActionList actions = new ActionList();
    Location ownerLocation = map.locationOf(owner);

    for (Exit exit : ownerLocation.getExits()) {
      Location adjacent = exit.getDestination();

      if (adjacent.containsAnActor()) {
        Actor maybeTarget = adjacent.getActor();
        if (maybeTarget.hasAbility(CAN_RECIEVE_STATUS)) {
          GameActor gameTarget = (GameActor) maybeTarget;
          actions.add(new TorchAttackAction(this, gameTarget, exit.getName()));
        }
      }
    }
    return actions;
  }
}
