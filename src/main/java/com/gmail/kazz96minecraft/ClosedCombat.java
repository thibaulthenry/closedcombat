package com.gmail.kazz96minecraft;

import com.gmail.kazz96minecraft.commands.CCRegister;
import com.gmail.kazz96minecraft.utils.PluginDetails;
import com.gmail.kazz96minecraft.utils.Zip;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@Plugin(
        id = PluginDetails.ID,
        name = PluginDetails.NAME,
        version = PluginDetails.VERSION,
        description = PluginDetails.DESC
)
public class ClosedCombat {

    private static ClosedCombat instance;

    @Inject
    private Logger logger;
    private PluginContainer plugin;

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        plugin = Sponge.getPluginManager().getPlugin(PluginDetails.ID).orElseThrow(NoSuchFieldError::new);
        instance = this;

        Sponge.getCommandManager().register(this, new CCRegister().getCommandSpec(), "closedcombat", "cc");

        Zip.initBackuping();
    }

    @Listener
    public void onMessageSent(MessageChannelEvent.Chat event) {
        if (!(event.getSource() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getSource();
        event.setMessage(
                Text.builder()
                        .append(Text.of(TextColors.DARK_GRAY, "<"))
                        .append(Text.of(TextColors.GRAY, player.getName()))
                        .append(Text.of(TextColors.DARK_GRAY, "> "))
                        .append(Text.of(event.getRawMessage().toPlain()))
                        .build()
        );
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if (!(event.getSource() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getSource();
        event.setMessage(
                Text.builder()
                        .append(Text.of(TextColors.DARK_GRAY, "["))
                        .append(Text.of(TextColors.DARK_GREEN, "+"))
                        .append(Text.of(TextColors.DARK_GRAY, "] "))
                        .append(Text.of(TextColors.GRAY, player.getName()))
                        .build()
        );
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        if (!(event.getSource() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getSource();
        event.setMessage(
                Text.builder()
                        .append(Text.of(TextColors.DARK_GRAY, "["))
                        .append(Text.of(TextColors.DARK_RED, "-"))
                        .append(Text.of(TextColors.DARK_GRAY, "] "))
                        .append(Text.of(TextColors.GRAY, player.getName()))
                        .build()
        );
    }

    public static ClosedCombat getInstance() {
        return instance;
    }

    public PluginContainer getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return logger;
    }
}