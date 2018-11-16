package com.gmail.kazz96minecraft.elements;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.gmail.kazz96minecraft.utils.CCSigns;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

class Spawn {

    private final Vector3i position;
    private final Vector3d rotation;
    private int capacity;

    Spawn(Location<World> spawnLocation) {
        position = spawnLocation.getBlockPosition();
        capacity = 1;
        rotation = CCSigns.getSignDirection(spawnLocation).orElse(Direction.NORTH).asOffset();
    }

    Vector3i getPosition() {
        return position;
    }

    Vector3d getRotation() {
        return rotation;
    }

    Integer getCapacity() {
        return capacity;
    }

    void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
