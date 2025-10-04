package game.teleportation;

import edu.monash.fit2099.engine.positions.Location;

/**
 * Class representing a teleportation destination.
 * Stores the target location and map name for teleportation.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.2
 */
public class TeleportDestination {
    private Location targetLocation;
    private String mapName;
    private String description;

    /**
     * Constructor for TeleportDestination.
     *
     * @param targetLocation the location to teleport to
     * @param mapName        the name of the map containing the target location
     * @param description    a description of this destination
     */
    public TeleportDestination(Location targetLocation, String mapName, String description) {
        this.targetLocation = targetLocation;
        this.mapName = mapName;
        this.description = description;
    }

    /**
     * Gets the target location.
     *
     * @return the target location
     */
    public Location getTargetLocation() {
        return targetLocation;
    }

    /**
     * Gets the map name.
     *
     * @return the map name
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}