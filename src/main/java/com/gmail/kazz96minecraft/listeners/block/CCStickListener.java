package com.gmail.kazz96minecraft.listeners.block;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.TargetBlockEvent;
import org.spongepowered.api.text.Text;

@SuppressWarnings("unused")
public class CCStickListener {

    private static long timeBuffer;

    @Listener
    public void onCCStickUsage(InteractBlockEvent event) {
        double deltaAfterLastEvent = 1000 /*System.currentTimeMillis() - timeBuffer*/;

        //check if InteractBlockEvent resolve twice bug

        if (!(event.getSource() instanceof Player) || deltaAfterLastEvent < 500) {
            return;
        }

        Player player = (Player) event.getSource();

        player.getItemInHand(HandTypes.MAIN_HAND).ifPresent(itemStack -> {
            if (itemStack.getType().getName().equals("minecraft:stick")) {
                //switch right click left click
            }
        });

        timeBuffer = System.currentTimeMillis();
    }
}
