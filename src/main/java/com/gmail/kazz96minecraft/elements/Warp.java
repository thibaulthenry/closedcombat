package com.gmail.kazz96minecraft.elements;

import com.flowpowered.math.vector.Vector3i;
import com.gmail.kazz96minecraft.elements.serializers.MapSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class Warp {

    private final String worldName;
    private final Vector3i position;
    private final String linkedMapName;
    private Integer countdown;

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

    private Optional<Location<World>> getLocation() {
        if (!getWorld().isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new Location<>(getWorld().get(), position));
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

    public Optional<TileEntity> getTileEntity() {
        if (!getLocation().isPresent()) {
            return Optional.empty();
        }

        Optional<TileEntity> optionalTileEntity = getLocation().get().getTileEntity();

        if (!optionalTileEntity.isPresent()) {
            return Optional.empty();
        }

        if (!(optionalTileEntity.get() instanceof org.spongepowered.api.block.tileentity.Sign)) {
            return Optional.empty();
        }

        return optionalTileEntity;
    }

    public void reset() {
        if (!getTileEntity().isPresent()) {
            return;
        }

        TileEntity tileEntity = getTileEntity().get();

        SignData signData = ((Sign) tileEntity).getSignData();

        signData.setElement(0, Text.of("[CC]"));
        signData.setElement(1, Text.of(getLinkedMap().get().getName()));
        signData.setElement(2, Text.of(TextStyles.BOLD, TextColors.GREEN, "AVAILABLE"));
        signData.setElement(3, Text.of(TextStyles.ITALIC, TextColors.WHITE, "0/", getLinkedMap().isPresent() ? getLinkedMap().get().getMaxPlayers() : "0"));

        tileEntity.offer(signData);
    }

    public void update() {
        if (!getTileEntity().isPresent() || !getLinkedMap().isPresent()) {
            return;
        }

        if (!Game.get(getLinkedMap().get()).isPresent()) {
            return;
        }

        TileEntity tileEntity = getTileEntity().get();
        SignData signData = ((Sign) tileEntity).getSignData();
        Game game = Game.get(getLinkedMap().get()).get();

        if (game.isRunning()) {
            signData.setElement(2, Text.of(TextStyles.BOLD, TextColors.GOLD, "RUNNING"));
            signData.setElement(3, Text.of(TextStyles.ITALIC, TextColors.WHITE, game.getPlayers().size(), "/", game.getLinkedMap().getMaxPlayers()));
        } else {
            int missingPlayers = game.getLinkedMap().getMaxPlayers() - game.getPlayers().size();

            if (missingPlayers == 0) {
                signData.setElement(2, Text.of(TextStyles.BOLD, TextColors.DARK_RED, "FULL"));
            } else if (missingPlayers > 0) {
                signData.setElement(2, Text.of(TextStyles.BOLD, TextColors.GREEN, "AVAILABLE"));
            }
            signData.setElement(3, Text.of(TextStyles.ITALIC, TextColors.WHITE, game.getPlayers().size(), "/", game.getLinkedMap().getMaxPlayers()));
        }

        if (countdown != null) {
            signData.setElement(0, Text.builder()
                    .append(Text.of("["))
                    .append(Text.of(TextStyles.BOLD, TextColors.DARK_RED, countdown))
                    .append(Text.of("]"))
                    .build()
            );
        } else {
            signData.setElement(0, Text.of("[CC]"));
        }

        tileEntity.offer(signData);
    }

    public void update(int countdown) {
        if (countdown == 0) {
            this.countdown = null;
        } else {
            this.countdown = countdown;
        }
        update();
    }
}
