package com.gmail.kazz96minecraft.elements;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.events.game.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
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
    private final List<Player> taskPlayers;

    private long firstEnterTime;
    private long gameStartTime;

    private int currentCountdown;
    private Task missingPlayersTask;
    private Task countdownTask;
    private Task runningGameTask;

    private Game(Map map) {
        this.map = map;
        players = new ArrayList<>();
        taskPlayers = new ArrayList<>();
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
        if (players.size() == 0) {
            firstEnterTime = 0;
            cancelTasks();
            return;
        }

        int playersRequired = map.getMinPlayers() - players.size();

        if (!isRunning() && playersRequired > 0) {
            cancelCountdownTask();
            startMissingPlayersTask();
        }

        if (!isRunning() && playersRequired <= 0) {
            cancelMissingPlayerTask();
            startCountdownTask();
        }

        if (isRunning() && playersRequired <= 0) {
            cancelMissingPlayerTask();
            cancelCountdownTask();
            startGameRunningTask();
        }
    }

    private void startMissingPlayersTask() {
        missingPlayersTask = Task.builder().name("Missing Players - " + map.getName()).interval(5, TimeUnit.SECONDS).async().execute(() -> {
            players.forEach(player ->
                    player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.YELLOW, "Missing ", map.getMinPlayers() - players.size(), " players before starting countdown.."))
            );
        }).submit(ClosedCombat.getInstance());
    }

    private void startCountdownTask() {
        currentCountdown = map.getCountdown() == null ? 60 : map.getCountdown();

        countdownTask = Task.builder().name("Countdown - " + map.getName()).interval(1, TimeUnit.SECONDS).async().execute(() -> {
            players.forEach(player -> player.sendMessages(ChatTypes.ACTION_BAR,
                    Text.builder()
                            .append(Text.of(TextColors.BLACK, "Game on ", map.getName(), " is starting in "))
                            .append(Text.of(TextColors.DARK_RED, TextStyles.BOLD, currentCountdown))
                            .build()
            ));

            GameCountdownEvent.fire(this, currentCountdown);

            if (currentCountdown == 0) {
                start();
                return;
            }

            currentCountdown--;
        }).submit(ClosedCombat.getInstance());
    }

    private void startGameRunningTask() {
        runningGameTask = Task.builder().name("Game Running - " + map.getName()).interval(1, TimeUnit.SECONDS).execute(() -> {
            taskPlayers.clear();
            taskPlayers.addAll(players);
            taskPlayers.stream().filter(map::isOutside).forEach(player -> {
                player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextStyles.BOLD, TextColors.RED, "You're outside the map limits"));
                player.damage(5, DamageSources.VOID);
            });
        }).submit(ClosedCombat.getInstance());
    }

    private void cancelTasks() {
        cancelMissingPlayerTask();
        cancelCountdownTask();
        cancelGameRunningTask();
    }

    private void cancelMissingPlayerTask() {
        if (missingPlayersTask != null) {
            missingPlayersTask.cancel();
            missingPlayersTask = null;
        }
    }

    private void cancelCountdownTask() {
        GameCountdownEvent.fire(this, 0);
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }

    private void cancelGameRunningTask() {
        if (runningGameTask != null) {
            runningGameTask.cancel();
            runningGameTask = null;
        }
    }

    private void start() {
        gameStartTime = System.currentTimeMillis();
        GameStartEvent.fire(this);
        updateTasks();
    }

    private void stop() {
        gameStartTime = 0;
        GameStopEvent.fire(this);
        cancelTasks();
        games.remove(this);
    }

    public void destroy() {
        players.forEach(player -> {
            player.sendMessage(Text.of(TextColors.RED, "The game on ", map.getName(), " has been stopped. You have been transfered to the default world"));
            Sponge.getCommandManager().process(player, "cc world tp " + Sponge.getServer().getDefaultWorldName());
        });

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

        Sponge.getCommandManager().process(player, "cc world tp " + map.getLinkedWorld().get().getName() + " " + xLobby + " " + yLobby + " " + zLobby);
    }

    public void leave(Player player) {
        players.remove(player);
        GameLeaveEvent.fire(this, player);
        updateTasks();

        if (players.size() == 0 && isRunning()) {
            stop();
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
