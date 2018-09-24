package com.gmail.kazz96minecraft.elements;

import com.flowpowered.math.vector.Vector3i;
import com.gmail.kazz96minecraft.elements.serializers.MapSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class Warp {

    private final String worldName;
    private final Vector3i position;
    private final String linkedMapName;

    public Warp(Location<World> signLocation, String linkedMapName) {
        this.worldName = signLocation.getExtent().getName();
        this.position = signLocation.getBlockPosition();
        this.linkedMapName = linkedMapName;
    }

    public String getWorldName() {
        return worldName;
    }

    public Vector3i getPosition() {
        return position;
    }

    public String getLinkedMapName() {
        return linkedMapName;
    }

    public Optional<Map> getLinkedMap() {
        return MapSerializer.getInstance().get(linkedMapName);
    }

    public Optional<World> getWorld() {
        return Sponge.getServer().getWorld(worldName);
    }
}
