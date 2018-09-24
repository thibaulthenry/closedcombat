package com.gmail.kazz96minecraft.elements;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Spawn {

    private final Vector3i position;
    private Integer capacity;

    Spawn(Location<World> spawnLocation) {
        position = spawnLocation.getBlockPosition();
        capacity = 1;
    }

    public Vector3i getPosition() {
        return position;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
