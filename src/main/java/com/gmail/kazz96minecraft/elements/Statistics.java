package com.gmail.kazz96minecraft.elements;

import com.gmail.kazz96minecraft.elements.serializers.StatisticsSerializer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;

import java.util.Optional;

public class Statistics {

    private final String playerName;
    private int wins;
    private int kills;
    private int deaths;
    private int games;

    public Statistics(Player player) {
        playerName = player.getName();
        wins = 0;
        kills = 0;
        deaths = 0;
        games = 0;
    }

    public static Optional<Player> checkSameGameKiller(DestructEntityEvent.Death event, Player victim) {
        Optional<EntityDamageSource> optionalDamageSource = event.getCause().first(EntityDamageSource.class);
        Optional<Game> optionalGame = Game.get(victim);

        if (!optionalGame.isPresent()) {
            return Optional.empty();
        }

        if (!optionalDamageSource.isPresent()) {
            return Optional.empty();
        }

        Entity entity = optionalDamageSource.get().getSource();

        if (!(entity instanceof Player)) {
            return Optional.empty();
        }

        Player killer = (Player) entity;

        if (killer.equals(victim)) {
            return Optional.empty();
        }

        if (!Game.get(killer).isPresent()) {
            return Optional.empty();
        }

        if (!Game.get(killer).get().equals(optionalGame.get())) {
            return Optional.empty();
        }

        return Optional.of(killer);
    }

    public static void increase(Player player, Statistics.Options options) {
        Optional<Statistics> optionalStatistics = StatisticsSerializer.getInstance().get(player);

        if (!optionalStatistics.isPresent()) {
            return;
        }

        Statistics statistics = optionalStatistics.get();

        switch (options) {
            case WINS:
                statistics.increaseWins();
                break;
            case KILLS:
                statistics.increaseKills();
                break;
            case DEATHS:
                statistics.increaseDeaths();
                break;
            case GAMES:
                statistics.increaseGames();
                break;
        }

        StatisticsSerializer.getInstance().serialize(statistics);
    }

    private void increaseWins() {
        wins++;
    }

    private void increaseKills() {
        kills++;
    }

    private void increaseDeaths() {
        deaths++;
    }

    private void increaseGames() {
        games++;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getWins() {
        return wins;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getWinsPercentage() {
        if (games == 0) {
            return wins;
        }

        return (int) (((double) wins / (double) games) * 100);
    }

    public int getCCpoints() {
        return 3 * wins + kills;
    }

    public enum Options {
        WINS, KILLS, DEATHS, GAMES
    }
}
