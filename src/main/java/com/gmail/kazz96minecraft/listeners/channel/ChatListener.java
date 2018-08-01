package com.gmail.kazz96minecraft.listeners.channel;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public class ChatListener {

    @Listener
    public void onPlayerSendMessage(MessageChannelEvent.Chat event) {
        if (!(event.getSource() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getSource();

        Text message = Text.builder()
                .append(Text.of(TextColors.DARK_GRAY, "<"))
                .append(
                        Text.builder()
                                .onHover(TextActions.showText(Text.of(TextColors.GRAY, "On ", player.getWorld().getName(), " at ", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))))
                                .append(Text.of(TextColors.GRAY, player.getName()))
                                .build()
                )
                .append(Text.of(TextColors.DARK_GRAY, "> "))
                .append(Text.of(event.getRawMessage().toPlain()))
                .build();

        event.setMessage(message);
    }

}
