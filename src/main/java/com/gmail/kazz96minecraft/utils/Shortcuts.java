package com.gmail.kazz96minecraft.utils;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.living.player.Player;

public class Shortcuts {

    public static PotionEffect getPotionEffect(PotionEffectType potionEffectType) {
        return PotionEffect.builder()
                .potionType(potionEffectType)
                .duration(5)
                .amplifier(100)
                .build();
    }

    public static void teleport(Player player, String worldName, Vector3i position, Vector3d rotation) {
        String x = Integer.toString(position.getX());
        String y = Integer.toString(position.getY());
        String z = Integer.toString(position.getZ());
        String rx = Double.toString(rotation.getX());
        String ry = Double.toString(rotation.getY());
        String rz = Double.toString(rotation.getZ());

        Shortcuts.runCommand("cc", "world", "teleport", worldName, player.getName(), x, y, z, rx, ry, rz);
    }

    private static void teleport(Player player, String worldName) {
        Shortcuts.runCommand("cc", "world", "teleport", worldName, player.getName());
    }

    public static void defaultTeleport(Player player) {
        teleport(player, Sponge.getServer().getDefaultWorldName());
    }

    private static CommandResult runCommand(CommandSource source, String... command) {
        return Sponge.getCommandManager().process(source, String.join(" ", command));
    }

    public static CommandResult runCommand(String... args) {
        return runCommand(Sponge.getServer().getConsole(), args);
    }
}
