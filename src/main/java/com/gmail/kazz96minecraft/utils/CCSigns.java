package com.gmail.kazz96minecraft.utils;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.block.DirectionalData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

    private static Optional<SignData> getSignData(Location<World> location) {
        if (!(location.getBlock().getType().equals(BlockTypes.STANDING_SIGN) || location.getBlock().getType().equals(BlockTypes.WALL_SIGN))) {
            return Optional.empty();
        }

        if(!location.getOrCreate(SignData.class).isPresent()) {
            return Optional.empty();
        }

        return Optional.of(location.getOrCreate(SignData.class).get());
    }

    public static Optional<Direction> getSignDirection(Location<World> location) {
        return location.get(DirectionalData.class).flatMap(directionalData -> directionalData.get(Keys.DIRECTION));
    }

    public static TextColor getRandomColor() {
        List<TextColor> colors = Arrays.asList(
                TextColors.WHITE,
                TextColors.AQUA,
                TextColors.BLACK,
                TextColors.BLUE,
                TextColors.DARK_AQUA,
                TextColors.DARK_BLUE,
                TextColors.DARK_GRAY,
                TextColors.DARK_GREEN,
                TextColors.DARK_PURPLE,
                TextColors.DARK_RED,
                TextColors.GOLD,
                TextColors.GRAY,
                TextColors.GREEN,
                TextColors.LIGHT_PURPLE,
                TextColors.RED,
                TextColors.YELLOW
        );

        return colors.get(new Random().nextInt(colors.size()));
    }
}
