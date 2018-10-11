package com.gmail.kazz96minecraft.listeners.block;

import com.gmail.kazz96minecraft.elements.Game;
import com.gmail.kazz96minecraft.elements.Map;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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
        //TODO
        System.out.println("gamelist : " + Game.getGames());
        System.out.println(Sponge.getScheduler().getScheduledTasks().size());
        Sponge.getScheduler().getScheduledTasks().forEach(task -> System.out.println(task.getName()));
    }
}
