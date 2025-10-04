package game.behaviors;

import game.terrain.Fire;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A manager class that handles burning damage over time for actors.
 * Burning deals 5 damage per turn for 5 turns per burning instance.
 * Multiple burning effects stack.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class BurningManager {
    private List<BurningInstance> burningInstances;

    /**
     * Constructor for BurningManager.
     */
    public BurningManager() {
        this.burningInstances = new ArrayList<>();
    }

    /**
     * Adds a new burning instance.
     */
    public void addBurningInstance() {
        burningInstances.add(new BurningInstance(Fire.getBurnDuration(), Fire.getBurnDamage()));
    }

    /**
     * Processes all burning instances and returns total damage.
     * Also removes expired instances.
     *
     * @return total damage from all burning instances
     */
    public int processBurning() {
        if (burningInstances.isEmpty()) {
            return 0;
        }

        int totalDamage = 0;
        Iterator<BurningInstance> iterator = burningInstances.iterator();
        while (iterator.hasNext()) {
            BurningInstance instance = iterator.next();
            totalDamage += instance.getDamage();
            instance.decrementTurns();
            if (instance.isExpired()) {
                iterator.remove();
            }
        }

        return totalDamage;
    }

    /**
     * Checks if all burning instances have expired.
     *
     * @return true if no burning instances remain
     */
    public boolean isExpired() {
        return burningInstances.isEmpty();
    }

    /**
     * Inner class representing a single burning instance.
     */
    private static class BurningInstance {
        private int turnsRemaining;
        private int damage;

        public BurningInstance(int turns, int damage) {
            this.turnsRemaining = turns;
            this.damage = damage;
        }

        public void decrementTurns() {
            turnsRemaining--;
        }

        public boolean isExpired() {
            return turnsRemaining <= 0;
        }

        public int getDamage() {
            return damage;
        }
    }
}