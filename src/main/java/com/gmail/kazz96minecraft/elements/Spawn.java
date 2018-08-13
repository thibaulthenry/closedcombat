package com.gmail.kazz96minecraft.elements;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Spawn {

    private final Vector3i spawnPosition;
    private Integer capacity;

    Spawn(Location<World> spawnLocation) {
        spawnPosition = spawnLocation.getBlockPosition();
        capacity = 1;
    }

    public Spawn(Location<World> spawnLocation, Integer capacity) {
        this(spawnLocation);
        this.capacity = capacity;
    }

    public Vector3i getSpawnPosition() {
        return spawnPosition;
    }

    public Integer getX() {
        return spawnPosition.getX();
    }

    public Integer getY() {
        return spawnPosition.getY();
    }

    public Integer getZ() {
        return spawnPosition.getZ();
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
