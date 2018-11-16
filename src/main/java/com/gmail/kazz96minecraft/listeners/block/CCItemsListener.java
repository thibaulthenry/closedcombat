package com.gmail.kazz96minecraft.listeners.block;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.elements.Game;
import com.gmail.kazz96minecraft.elements.Map;
import com.gmail.kazz96minecraft.elements.serializers.MapSerializer;
import com.gmail.kazz96minecraft.utils.CCSigns;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CCItemsListener {

    @Listener
    public void onCCStickUsage(InteractBlockEvent event, @First Player player) {
        if (!player.hasPermission("closedcombat.map")) {
            return;
        }

        boolean CCStickMainHand = player.getItemInHand(HandTypes.MAIN_HAND)
                .filter(itemStack -> {
                    Text displayName = itemStack.get(Keys.DISPLAY_NAME).orElse(Text.of("Not a CCStick"));
                    return itemStack.getType().equals(ItemTypes.STICK) && displayName.equals(Text.of("CCStick"));
                })
                .isPresent();

        if (!CCStickMainHand || !event.getTargetBlock().getLocation().isPresent()) {
            return;
        }

        event.setCancelled(true);

        Location<World> blockLocation = event.getTargetBlock().getLocation().get();

        if (event instanceof InteractBlockEvent.Primary) {
            if (Map.leftBlockMarker != null && Map.leftBlockMarker.getBlockPosition().equals(blockLocation.getBlockPosition())) {
                return;
            }

            Map.leftBlockMarker = blockLocation;
            player.sendMessage(Text.of(TextColors.GOLD, "Left Marker has been set to ", blockLocation.getExtent().getName(), " at ", blockLocation.getPosition()));
        } else {
            if (Map.rightBlockMarker != null && Map.rightBlockMarker.getBlockPosition().equals(blockLocation.getBlockPosition())) {
                return;
            }

            Map.rightBlockMarker = blockLocation;
            player.sendMessage(Text.of(TextColors.YELLOW, "Right Marker has been set to ", blockLocation.getExtent().getName(), " at ", blockLocation.getPosition()));
        }
    }

    @Listener
    public void onCCHatchetUsage(InteractBlockEvent event, @First Player player) {
        if (!player.hasPermission("closedcombat.map")) {
            return;
        }

        boolean CCHatchetMainHand = player.getItemInHand(HandTypes.MAIN_HAND)
                .filter(itemStack -> {
                    Text displayName = itemStack.get(Keys.DISPLAY_NAME).orElse(Text.of("Not a CCHatchet"));
                    return itemStack.getType().equals(ItemTypes.WOODEN_AXE) && displayName.equals(Text.of("CCHatchet"));
                })
                .isPresent();

        if (!CCHatchetMainHand || !event.getTargetBlock().getLocation().isPresent()) {
            return;
        }

        event.setCancelled(true);

        Optional<List<Text>> optionalLines = CCSigns.getLines(event.getTargetBlock().getLocation().get());

        if (!optionalLines.isPresent()) {
            return;
        }

        List<Text> lines = optionalLines.get();
        String playerWorldName = player.getLocation().getExtent().getName();

        List<Map> mapOnThisWorld = MapSerializer.getInstance().getList().stream()
                .filter(map -> map.getWorldName().equals(playerWorldName))
                .collect(Collectors.toList());

        if (mapOnThisWorld.size() == 0) {
            player.sendMessage(Text.of(TextColors.RED, "There is no map registered on the world : ", playerWorldName));
            return;
        }

        if (mapOnThisWorld.size() == 1) {
            player.sendMessage(Text.of(TextColors.GREEN, "The option ", mapOnThisWorld.get(0).affectOptions(event.getTargetBlock().getLocation().get(), lines).name(), " has been added to ", mapOnThisWorld.get(0).getName()));
            MapSerializer.getInstance().serialize(mapOnThisWorld.get(0));
            return;
        }

        player.sendMessage(Text.builder()
                .append(Text.of(TextColors.GRAY, "Several maps detected on this world (", playerWorldName, "). You have to choose : "))
                .append(mapOnThisWorld.stream()
                        .map(map -> Text.builder()
                                .append(Text.of("[", map.getName(), "] "))
                                .color(CCSigns.getRandomColor())
                                .style(TextStyles.BOLD)
                                .onClick(TextActions.executeCallback(commandSource -> {
                                    player.sendMessage(Text.of(TextColors.GREEN, "The option ", mapOnThisWorld.get(0).affectOptions(event.getTargetBlock().getLocation().get(), lines).name(), " has been added to ", mapOnThisWorld.get(0).getName()));
                                    MapSerializer.getInstance().serialize(map);
                                }))
                                .onHover(TextActions.showText(Text.of("Click to choose this map !")))
                                .build())
                        .iterator())
                .build()
        );
    }

    @Listener
    public void onCCCookieUsage(InteractBlockEvent event, @First Player player) {
        if (!player.hasPermission("closedcombat.map")) {
            return;
        }

        boolean CCCookieMainHand = player.getItemInHand(HandTypes.MAIN_HAND)
                .filter(itemStack -> {
                    Text displayName = itemStack.get(Keys.DISPLAY_NAME).orElse(Text.of("Not a CCCookie"));
                    return itemStack.getType().equals(ItemTypes.COOKIE) && displayName.equals(Text.of("CCCookie"));
                })
                .isPresent();

        if (!CCCookieMainHand || !event.getTargetBlock().getLocation().isPresent()) {
            return;
        }

        event.setCancelled(true);

        player.sendMessage(Text.of(TextColors.GREEN, "Games information on console"));

        ClosedCombat.getInstance().sendConsole("Number of games : " + Game.getGames().size());
        ClosedCombat.getInstance().sendConsole("Number of loaded tasks : " + (Sponge.getScheduler().getScheduledTasks().size() - 1));
        ClosedCombat.getInstance().sendConsole("Loaded tasks : \n\t" +
                Sponge.getScheduler().getScheduledTasks().stream().map(Task::getName).collect(Collectors.joining("\n")));
    }

    @Listener
    public void onCCCompassUsage(InteractBlockEvent event, @First Player player) {
        if (!player.hasPermission("closedcombat.map")) {
            return;
        }

        boolean CCCompassMainHand = player.getItemInHand(HandTypes.MAIN_HAND)
                .filter(itemStack -> {
                    Text displayName = itemStack.get(Keys.DISPLAY_NAME).orElse(Text.of("Not a CCCompass"));
                    return itemStack.getType().equals(ItemTypes.COMPASS) && displayName.equals(Text.of("CCCompass"));
                })
                .isPresent();

        if (!CCCompassMainHand) {
            return;
        }

        event.setCancelled(true);

        if (event.getTargetBlock().getLocation().isPresent()) {
            return;
        }

        Optional<BlockRayHit<World>> optionalBlockRayHit = BlockRay.from(player).distanceLimit(1000).stopFilter(BlockRay.onlyAirFilter()).end();

        if (!optionalBlockRayHit.isPresent()) {
            return;
        }

        player.setLocationSafely(optionalBlockRayHit.get().getLocation());
    }
}
