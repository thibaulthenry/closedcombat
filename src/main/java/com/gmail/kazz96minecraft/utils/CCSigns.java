package com.gmail.kazz96minecraft.utils;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

public class CCSigns {

    public static Optional<List<Text>> getLines(Location<World> location) {
        if (!getSignData(location).isPresent()) {
            return Optional.empty();
        }

        List<Text> lines = getSignData(location).get().asList();

        if (!lines.get(0).toPlain().equals("[CC]")) {
            return Optional.empty();
        }

        return Optional.of(lines);
    }

    public static Optional<SignData> getSignData(Location<World> location) {
        if (!(location.getBlock().getType().equals(BlockTypes.STANDING_SIGN) || location.getBlock().getType().equals(BlockTypes.WALL_SIGN))) {
            return Optional.empty();
        }

        if(!location.getOrCreate(SignData.class).isPresent()) {
            return Optional.empty();
        }

        return Optional.of(location.getOrCreate(SignData.class).get());
    }
}
