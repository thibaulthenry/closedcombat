package com.gmail.kazz96minecraft.listeners.player;

import com.gmail.kazz96minecraft.elements.Game;
import com.gmail.kazz96minecraft.elements.Statistics;
import com.gmail.kazz96minecraft.utils.CCScoreboards;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;

import java.util.Optional;

@SuppressWarnings("unused")
public class GamePlayerListener {

    @Listener
    public void onGamePlayerRespawning(RespawnPlayerEvent event, @First Player player) {
        if (!Game.get(player).isPresent()) {
            return;
        }

        Game game = Game.get(player).get();
        game.leave(player);

        if (!Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).isPresent()) {
            player.kick(Text.of("An error occurred while reaching the default world spawn"));
            return;
        }

        event.setToTransform(new Transform<>(
                Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get().getSpawnLocation()
        ));

        CCScoreboards.setScoreboard(player);
    }

    @Listener
    public void onGamePlayerDying(DestructEntityEvent.Death event, @First Player player) {
        if (!Game.get(player).isPresent()) {
            return;
        }

        Game game = Game.get(player).get();

        Statistics.increase(player, Statistics.Options.DEATHS);
        Statistics.checkSameGameKiller(event, player).ifPresent(targetPlayer -> Statistics.increase(targetPlayer, Statistics.Options.KILLS));
        player.respawnPlayer();
        event.setCancelled(true);
    }

    @Listener
    public void onGamePlayerDamage(DamageEntityEvent event, @First Player player) {
        Optional<Game> optionalGame = Game.get(player);

        if (optionalGame.isPresent() && optionalGame.get().isRunning()) {
            return;
        }

        event.setCancelled(true);
    }
}
