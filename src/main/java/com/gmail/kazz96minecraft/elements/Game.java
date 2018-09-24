package com.gmail.kazz96minecraft.elements;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.events.game.GameJoinEvent;
import com.gmail.kazz96minecraft.events.game.GameLeaveEvent;
import com.gmail.kazz96minecraft.events.game.GameStartEvent;
import com.gmail.kazz96minecraft.events.game.GameStopEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Game {

    private static final List<Game> games = new ArrayList<>();

    private final Map map;
    private final List<Player> players;

    private long firstEnterTime;
    private long gameStartTime;

    private int currentCountdown;
    private Task missingPlayersTask;
    private Task countdownTask;

    private Game(Map map) {
        this.map = map;
        players = new ArrayList<>();
        firstEnterTime = System.currentTimeMillis();
    }

    public static List<Game> getGames() {
        return games;
    }

    public static Optional<Game> get(Map map) {
        return games.stream()
                .filter(game -> game.map.equals(map))
                .findFirst();
    }

    public static Optional<Game> get(Player player) {
        return games.stream()
                .filter(game -> game.getPlayers().contains(player))
                .findFirst();
    }

    public static Game getOrCreate(Map map) throws CommandException {
        Optional<Game> optionalGame = games.stream()
                .filter(game -> game.map.equals(map))
                .findFirst();

        if (!optionalGame.isPresent()) {
            if (map.getLobbyPosition() == null) {
                throw new CommandException(Text.of("No lobby detected for this map. Aborting.."));
            }
            Game game = new Game(map);
            games.add(game);
            return game;
        }

        return optionalGame.get();
    }

    public static boolean isRunning(Map map) {
        return games.stream()
                .filter(game -> game.map.equals(map))
                .anyMatch(Game::isRunning);
    }

    private void updateTasks() {
        updateMissingPlayersTask();
        updateCountdownTask();
    }

    private void updateMissingPlayersTask() {
        cancelMissingPlayersTask();

        if (players.size() == 0) {
            return;
        }

        int playersRequired = map.getMinPlayers() - players.size();

        if (playersRequired == 0) {
            if (map.getMinPlayers() > 1) {
                updateCountdownTask();
            }
            return;
        }

        missingPlayersTask = Task.builder().interval(10, TimeUnit.SECONDS).async().execute(() ->
                players.forEach(player ->
                        player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.YELLOW, "Missing ", playersRequired, " players before starting countdown.."))
                )
        ).submit(ClosedCombat.getInstance());
    }

    private void updateCountdownTask() {
        cancelCountdownTask();

        if (missingPlayersTask != null) {
            return;
        }

        currentCountdown = map.getCountdown() == null ? 60 : map.getCountdown();

        countdownTask = Task.builder().interval(1, TimeUnit.SECONDS).async().execute(() -> {
            players.forEach(player -> player.sendMessages(ChatTypes.ACTION_BAR,
                    Text.builder()
                            .append(Text.of(TextColors.BLACK, "Game on ", map.getName(), " is starting in "))
                            .append(Text.of(TextColors.DARK_RED, TextStyles.BOLD, currentCountdown))
                            .build()
            ));

            if (currentCountdown == 0) {
                cancelCountdownTask();
                start();
                return;
            }

            currentCountdown--;
        }).submit(ClosedCombat.getInstance());
    }

    private void cancelTasks() {
        cancelMissingPlayersTask();
        cancelCountdownTask();
    }

    private void cancelMissingPlayersTask() {
        if (missingPlayersTask != null) {
            missingPlayersTask.cancel();
            missingPlayersTask = null;
        }
    }

    private void cancelCountdownTask() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }

    private void start() {
        GameStartEvent.fire(this);
        gameStartTime = System.currentTimeMillis();
        System.out.println("start");
    }

    private void stop() {
        GameStopEvent.fire(this);
        gameStartTime = 0;
        games.remove(this);
    }

    public void destroy() {
        players.forEach(player -> {
            player.sendMessage(Text.of(TextColors.RED, "The map ", map.getName(), " has been modified. You have been transfered to the default world"));
            Sponge.getCommandManager().process(player, "cc world tp " + Sponge.getServer().getDefaultWorldName());
        });

        cancelTasks();

        stop();
    }

    public void join(Player player) {
        if (!map.getLinkedWorld().isPresent()) {
            player.sendMessage(Text.of(TextColors.RED, "Unable to reach the linked world of ", map.getName()));
            return;
        }

        players.add(player);
        GameJoinEvent.fire(this, player);
        updateTasks();

        if (firstEnterTime == 0) {
            firstEnterTime = System.currentTimeMillis();
        }

        int xLobby = map.getLobbyPosition().getX();
        int yLobby = map.getLobbyPosition().getY();
        int zLobby = map.getLobbyPosition().getZ();

        Sponge.getCommandManager().process(player,
                "cc world tp " +
                        map.getLinkedWorld().get().getName() + " " +
                        xLobby + " " +
                        yLobby + " " +
                        zLobby
        );
    }

    public void leave(Player player) {
        players.remove(player);
        GameLeaveEvent.fire(this, player);
        updateTasks();

        if (players.size() == 0) {
            firstEnterTime = 0;
        }
    }

    public boolean isRunning() {
        return gameStartTime != 0;
    }

    public boolean isFull() {
        return players.size() >= map.getMaxPlayers();
    }

    public boolean isPlaying(Player targetPlayer) {
        return players.stream().anyMatch(player -> player.equals(targetPlayer));
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map getLinkedMap() {
        return map;
    }

    public long getLobbyWaiting() {
        return (gameStartTime == 0 ? System.currentTimeMillis() : gameStartTime) - firstEnterTime;
    }

    private long getGameDuration() {
        return isRunning() ? (System.currentTimeMillis() - gameStartTime) : 0;
    }
}
