package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * A class representing dirt on the ground.
 * Dirt is the result of fire burning other terrain.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class Dirt extends Ground {
    /**
     * Constructor for Dirt.
     */
    public Dirt() {
        super('+', "Dirt");
    }
}
