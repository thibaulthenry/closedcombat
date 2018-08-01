package com.gmail.kazz96minecraft.listeners.block;

import com.gmail.kazz96minecraft.utils.MapMaker;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@SuppressWarnings("unused")
public class CCStickListener {

    private static long timeBuffer;

    @Listener
    public void onCCStickUsage(InteractBlockEvent event) {
        if (!(event.getSource() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getSource();

        if (!player.hasPermission("closedcombat.map")) {
            return;
        }

        double deltaAfterLastEvent = System.currentTimeMillis() - timeBuffer;

        if (deltaAfterLastEvent < 500) {
            return;
        }

        Boolean CCStickMainHand = player.getItemInHand(HandTypes.MAIN_HAND)
                .filter(itemStack -> {
                    if (!itemStack.getOrCreate(DisplayNameData.class).isPresent()) {
                        return false;
                    }

                    DisplayNameData displayNameData = itemStack.getOrCreate(DisplayNameData.class).get();

                    return itemStack.getType().getName().equals("minecraft:stick") && displayNameData.displayName().get().equals(Text.of("CCStick"));
                })
                .isPresent();

        if (!CCStickMainHand) {
            return;
        }

        if (!event.getTargetBlock().getLocation().isPresent()) {
            return;
        }

        Location<World> blockLocation = event.getTargetBlock().getLocation().get();

        if (event instanceof InteractBlockEvent.Primary) {
            MapMaker.leftBlockMarker = blockLocation;
            player.sendMessage(Text.of(TextColors.GOLD, "Left Marker has been set to ", blockLocation.getExtent().getName(), " at ", blockLocation.getPosition()));
        } else {
            MapMaker.rightBlockMarker = blockLocation;
            player.sendMessage(Text.of(TextColors.YELLOW, "Right Marker has been set to ", blockLocation.getExtent().getName(), " at ", blockLocation.getPosition()));
        }

        event.setCancelled(true);
        timeBuffer = System.currentTimeMillis();
    }
}
