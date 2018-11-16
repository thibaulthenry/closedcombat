package com.gmail.kazz96minecraft.utils;

import com.gmail.kazz96minecraft.elements.Game;
import com.gmail.kazz96minecraft.elements.Statistics;
import com.gmail.kazz96minecraft.elements.serializers.StatisticsSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Optional;


public class CCScoreboards {

    public static void setScoreboard(Player player) {
        Optional<Game> optionalGame = Game.get(player);

        if (!optionalGame.isPresent()) {
            StatisticsSerializer.getInstance().get(player).ifPresent(statistics -> player.setScoreboard(getScoresScoreboard(statistics)));
            return;
        }

        Game game = optionalGame.get();

        if (optionalGame.get().isRunning()) {
            player.setScoreboard(game.getRunningScoreboard());
            return;
        }

        player.setScoreboard(game.getLobbyScoreboard());
    }

    private static Scoreboard getScoresScoreboard(Statistics statistics) {
        Scoreboard scoreboard = Scoreboard.builder().build();

        Objective objective = Objective.builder()
                .name("Scores")
                .displayName(Text.of(TextStyles.BOLD, TextColors.BLUE, StringUtils.center("Scores", 28)))
                .criterion(Criteria.DUMMY).build();

        scoreboard.addObjective(objective);
        scoreboard.updateDisplaySlot(objective, DisplaySlots.SIDEBAR);

        objective.getOrCreateScore(Text.of()).setScore(6);
        objective.getOrCreateScore(Text.builder()
                .append(Text.of("CCPoints : "))
                .append(Text.of(TextColors.GOLD, statistics.getCCpoints()))
                .build()
        ).setScore(5);
        objective.getOrCreateScore(Text.of(" ")).setScore(4);

        objective.getOrCreateScore(Text.builder()
                .append(Text.of("Wins : "))
                .append(Text.of(TextColors.GREEN, statistics.getWins(), " [", statistics.getWinsPercentage(), "%]"))
                .build()
        ).setScore(3);
        objective.getOrCreateScore(Text.of("  ")).setScore(2);

        objective.getOrCreateScore(Text.builder()
                .append(Text.of("Kills : "))
                .append(Text.of(TextColors.GREEN, statistics.getKills()))
                .build()
        ).setScore(1);

        objective.getOrCreateScore(Text.builder()
                .append(Text.of("Deaths : "))
                .append(Text.of(TextColors.GREEN, statistics.getDeaths()))
                .build()
        ).setScore(0);

        return scoreboard;
    }

    public static void updateGameLobbyScoreboard(Game game) {
        if (game.getLobbyScoreboard().getObjectives().size() == 1) {
            game.getLobbyScoreboard().getObjective(game.getLinkedMap().getName()).ifPresent(objective ->
                    game.getLobbyScoreboard().removeObjective(objective)
            );
        }

        Objective objective = Objective.builder()
                .name(game.getLinkedMap().getName())
                .displayName(Text.of(TextStyles.BOLD, TextColors.BLUE, StringUtils.center(game.getLinkedMap().getName() + " lobby", 28)))
                .criterion(Criteria.DUMMY).build();

        game.getLobbyScoreboard().addObjective(objective);
        game.getLobbyScoreboard().updateDisplaySlot(objective, DisplaySlots.SIDEBAR);

        objective.getOrCreateScore(Text.of()).setScore(3);
        if (game.isOnCountdown()) {
            objective.getOrCreateScore(Text.builder()
                    .append(Text.of("Countdown : "))
                    .append(Text.of(TextColors.GREEN, DurationFormatUtils.formatDuration(game.getCurrentCountdown() * 1000, "mm:ss")))
                    .build()
            ).setScore(2);
        } else {
            objective.getOrCreateScore(Text.builder()
                    .append(Text.of("Missing players : "))
                    .append(Text.of(TextColors.GREEN, game.getMissingPlayers()))
                    .build()
            ).setScore(2);
        }

        objective.getOrCreateScore(Text.of(" ")).setScore(1);
        objective.getOrCreateScore(Text.builder()
                .append(Text.of("Players : "))
                .append(Text.of(TextColors.GREEN, game.getPlayers().size(), "/", game.getLinkedMap().getMaxPlayers()))
                .build()
        ).setScore(0);
    }

    public static void updateGameRunningScoreboard(Game game) {
        if (game.getRunningScoreboard().getObjectives().size() == 1) {
            game.getRunningScoreboard().getObjective(game.getLinkedMap().getName()).ifPresent(objective ->
                    game.getRunningScoreboard().removeObjective(objective)
            );
        }

        Objective objective = Objective.builder()
                .name(game.getLinkedMap().getName())
                .displayName(Text.of(TextStyles.BOLD, TextColors.BLUE, StringUtils.center(game.getLinkedMap().getName(), 28)))
                .criterion(Criteria.DUMMY).build();

        game.getRunningScoreboard().addObjective(objective);
        game.getRunningScoreboard().updateDisplaySlot(objective, DisplaySlots.SIDEBAR);

        objective.getOrCreateScore(Text.of()).setScore(3);
        objective.getOrCreateScore(Text.builder()
                .append(Text.of("Time : "))
                .append(Text.of(TextColors.GREEN, DurationFormatUtils.formatDuration(game.getGameDuration(), "mm:ss")))
                .build()
        ).setScore(2);

        objective.getOrCreateScore(Text.of(" ")).setScore(1);
        objective.getOrCreateScore(Text.builder()
                .append(Text.of("Players : "))
                .append(Text.of(TextColors.GREEN, game.getPlayers().size(), "/", game.getLinkedMap().getMaxPlayers()))
                .build()
        ).setScore(0);
    }

}
