package com.gmail.kazz96minecraft.listeners.block;

import com.gmail.kazz96minecraft.elements.Game;
import com.gmail.kazz96minecraft.elements.Map;
import com.gmail.kazz96minecraft.elements.Warp;
import com.gmail.kazz96minecraft.elements.serializers.MapSerializer;
import com.gmail.kazz96minecraft.elements.serializers.WarpSerializer;
import com.gmail.kazz96minecraft.events.game.GameJoinEvent;
import com.gmail.kazz96minecraft.events.game.GameLeaveEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class CCSignListener {

    @Listener
    public void onCCWarpClickEvent(InteractBlockEvent.Secondary event, @First Player player) {
        Optional<Location<World>> optionalLocation = event.getTargetBlock().getLocation();
        if (!optionalLocation.isPresent()) {
            return;
        }

        Location<World> warpLocation = optionalLocation.get();

        if (!WarpSerializer.getInstance().isRegisteredWarp(optionalLocation.get())) {
            return;
        }

        event.setCancelled(true);

        Optional<Map> optionalMap = WarpSerializer.getInstance().getAssociatedMap(warpLocation);

        if (!optionalMap.isPresent()) {
            return;
        }

        Sponge.getCommandManager().process(player, "cc map join " + optionalMap.get().getName());
    }

    @Listener
    public void onCCWarpEnabled(ChangeSignEvent event, @First Player player) {
        if (!player.hasPermission("closedcombat.map")) {
            return;
        }

        List<Text> lines = event.getText().lines().get();

        if (!lines.get(1).toPlain().equals("WARP")) {
            return;
        }

        String mapName = lines.get(2).toPlain();

        if (!MapSerializer.getInstance().get(mapName).isPresent()) {
            player.sendMessage(Text.of(TextColors.RED, mapName, " isn't loaded as a map on this server"));
            return;
        }

        Map map = MapSerializer.getInstance().get(mapName).get();

        if (!map.getLinkedWorld().isPresent()) {
            player.sendMessage(Text.of(TextColors.RED, "The linked world of ", mapName, " is unreachable"));
            return;
        }

        event.getText().setElement(0, Text.of("[CC]"));
        event.getText().setElement(1, Text.of(map.getName()));
        event.getText().setElement(2, Text.of(TextColors.GREEN, "AVAILABLE"));
        event.getText().setElement(3, Text.of(TextStyles.ITALIC, 0, "/", map.getMaxPlayers()));

        Warp warp = new Warp(event.getTargetTile().getLocation(), mapName);

        if (!WarpSerializer.getInstance().serialize(warp)) {
            player.sendMessage(Text.of(TextColors.RED, "An error occurs while creating " + map.getName() + " warp's properties file"));
            return;
        }

        WarpSerializer.getInstance().getList().add(warp);
        player.sendMessage(Text.of(TextColors.GREEN, map.getName(), "'s linked warp has been created successfully"));
    }

    @Listener
    public void onCCWarpDisabled(ChangeBlockEvent.Break event, @First Player player) {
        Optional<Location<World>> optionalBlockLocation = event.getTransactions().stream()
                .map(Transaction::getFinal)
                .map(BlockSnapshot::getLocation)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if (!optionalBlockLocation.isPresent()) {
            return;
        }

        Location<World> blockLocation = optionalBlockLocation.get();

        if (!WarpSerializer.getInstance().isRegisteredWarp(blockLocation)) {
            return;
        }

        event.setCancelled(true);

        if (!player.hasPermission("closedcombat.map")) {
            return;
        }

        ClickAction removeBlockAction = TextActions.executeCallback(commandSource -> {
            if (!WarpSerializer.getInstance().isRegisteredWarp(blockLocation)) {
                return;
            }

            WarpSerializer.getInstance().removeRegisteredWarp(blockLocation);

            if (blockLocation.removeBlock()) {
                player.sendMessage(Text.of(TextColors.GREEN, "The CCWarp at ", blockLocation.getBlockPosition(), " has been deleted successfully"));
            }
        });

        player.sendMessage(Text.builder()
                .append(Text.of("CCWarp Break detected, please confirm deletion : "))
                .append(Text.builder()
                        .append(Text.of("[YES]"))
                        .color(TextColors.DARK_RED)
                        .style(TextStyles.BOLD)
                        .onClick(removeBlockAction)
                        .onHover(TextActions.showText(Text.of("Click to delete the CCWarp")))
                        .build()
                )
                .build()
        );
    }

    @Listener
    public void onGamePlayerJoin(GameJoinEvent event, @First Player player) {
        Game game = event.getGame();
        List<Warp> warps = WarpSerializer.getInstance().getWarps(game.getLinkedMap());

        warps.forEach(warp -> {
            if (!warp.getWorld().isPresent()) {
                return;
            }

            Optional<TileEntity> optionalTileEntity = new Location<>(warp.getWorld().get(), warp.getPosition()).getTileEntity();

            if (!optionalTileEntity.isPresent()) {
                return;
            }

            TileEntity tileEntity = optionalTileEntity.get();

            if (!(tileEntity instanceof org.spongepowered.api.block.tileentity.Sign)) {
                return;
            }

            int missingPlayers = game.getLinkedMap().getMaxPlayers() - game.getPlayers().size();

            SignData signData = ((Sign) tileEntity).getSignData();

            if (missingPlayers == 0) {
                signData.setElement(2, Text.of(TextColors.DARK_RED, "FULL"));
            } else if (missingPlayers > 0) {
                signData.setElement(2, Text.of(TextColors.GREEN, "AVAILABLE"));
            }
            signData.setElement(3, Text.of(TextColors.WHITE, game.getPlayers().size(), "/", game.getLinkedMap().getMaxPlayers()));

            tileEntity.offer(signData);
        });
    }

    @Listener
    public void onGamePlayerLeave(GameLeaveEvent event, @First Player player) {
        Game game = event.getGame();
        List<Warp> warps = WarpSerializer.getInstance().getWarps(game.getLinkedMap());

        warps.forEach(warp -> {
            if (!warp.getWorld().isPresent()) {
                return;
            }

            Optional<TileEntity> optionalTileEntity = new Location<>(warp.getWorld().get(), warp.getPosition()).getTileEntity();

            if (!optionalTileEntity.isPresent()) {
                return;
            }

            TileEntity tileEntity = optionalTileEntity.get();

            if (!(tileEntity instanceof org.spongepowered.api.block.tileentity.Sign)) {
                return;
            }

            int missingPlayers = game.getLinkedMap().getMaxPlayers() - game.getPlayers().size();

            SignData signData = ((Sign) tileEntity).getSignData();

            if (missingPlayers == 0) {
                signData.setElement(2, Text.of(TextStyles.BOLD, TextColors.DARK_RED, "FULL"));
            } else if (missingPlayers > 0) {
                signData.setElement(2, Text.of(TextStyles.BOLD, TextColors.GREEN, "AVAILABLE"));
            }
            signData.setElement(3, Text.of(TextColors.WHITE, game.getPlayers().size(), "/", game.getLinkedMap().getMaxPlayers()));

            tileEntity.offer(signData);
        });
    }

}
