package com.gmail.kazz96minecraft.elements;

import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.events.game.*;
import com.gmail.kazz96minecraft.events.statistics.StatsGameEvent;
import com.gmail.kazz96minecraft.events.statistics.StatsWinEvent;
import com.gmail.kazz96minecraft.utils.CCScoreboards;
import com.gmail.kazz96minecraft.utils.Shortcuts;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Game {

    private static final List<Game> games = new ArrayList<>();

    private final Map map;
    private final List<Player> players;
    private final List<Player> taskPlayers;

    private long firstEnterTime;
    private long gameStartTime;

    private boolean stopped;

    private int currentCountdown;
    private Task missingPlayersTask;
    private Task countdownTask;
    private Task runningGameTask;

    private final Scoreboard lobbyScoreboard;
    private final Scoreboard runningScoreboard;

    private Game(Map map) {
        this.map = map;
        players = new ArrayList<>();
        taskPlayers = new ArrayList<>();
        lobbyScoreboard = Scoreboard.builder().build();
        runningScoreboard = Scoreboard.builder().build();
        this.map.deleteVisibleSigns();
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

    public boolean isPlaying(Player targetPlayer) {
        return games.stream().anyMatch(game ->
                game.getPlayers().stream().anyMatch(player -> player.equals(targetPlayer))
        );
    }

    private void updateTasks() {
        if (players.size() == 0) {
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
            CCScoreboards.updateGameLobbyScoreboard(this);

            players.forEach(player ->
                    player.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.YELLOW, "Missing ", map.getMinPlayers() - players.size(), " players before starting countdown.."))
            );
        }).submit(ClosedCombat.getInstance());
    }

    private void startCountdownTask() {
        currentCountdown = map.getCountdown();

        countdownTask = Task.builder().name("Countdown - " + map.getName()).interval(1, TimeUnit.SECONDS).async().execute(() -> {
            CCScoreboards.updateGameLobbyScoreboard(this);

            players.forEach(player -> player.sendMessages(ChatTypes.ACTION_BAR,
                    Text.builder()
                            .append(Text.of(TextColors.BLACK, "Game on ", map.getName(), " is starting in "))
                            .append(Text.of(TextColors.DARK_RED, TextStyles.BOLD, currentCountdown))
                            .build()
            ));

            GameCountdownEvent.fire(this, currentCountdown);

            if (currentCountdown == 0) {
                Task.builder().name("Starting - " + map.getName()).execute(this::start).submit(ClosedCombat.getInstance());
                return;
            }

            currentCountdown--;
        }).submit(ClosedCombat.getInstance());
    }

    private void startGameRunningTask() {
        runningGameTask = Task.builder().name("Game Running - " + map.getName()).delayTicks(10).interval(1, TimeUnit.SECONDS).execute(() -> {
            CCScoreboards.updateGameRunningScoreboard(this);

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
        players.forEach(player -> {
            StatsGameEvent.fire(player);
            clear(player);
        });
        updateTasks();
        teleportAllUniform();
    }

    private void stop() {
        stopped = true;
        gameStartTime = 0;
        players.forEach(StatsWinEvent::fire);
        GameStopEvent.fire(this);
        cancelTasks();
        new ArrayList<>(players).forEach(this::leave);
        games.remove(this);
        map.restoreWorld();
    }

    public void destroy() {
        players.forEach(player -> {
            player.sendMessage(Text.of(TextColors.RED, "The game on ", map.getName(), " has been stopped. You have been transfered to the default world"));
            Shortcuts.defaultTeleport(player);
        });

        stop();
    }

    public void join(Player player) {
        if (!map.getLinkedWorld().isPresent()) {
            player.sendMessage(Text.of(TextColors.RED, "Unable to reach the linked world of ", map.getName()));
            return;
        }

        players.add(player);
        GameJoinEvent.fire(this);
        clear(player);
        updateTasks();

        if (firstEnterTime == 0) {
            firstEnterTime = System.currentTimeMillis();
        }

        Task.builder().name("Teleport player (" + player.getName() + ") - " + map.getName()).execute(() ->
                Shortcuts.teleport(player, map.getLinkedWorld().get().getName(), map.getLobbyPosition(), map.getLobbyRotation())).submit(ClosedCombat.getInstance()
        );
    }

    private void teleportAllUniform() {
        List<Spawn> spawns = new ArrayList<>();

        map.getSpawns().forEach(spawn -> {
            for (int i = 0; i < spawn.getCapacity(); i++) {
                spawns.add(spawn);
            }
        });

        if (!map.getLinkedWorld().isPresent()) {
            destroy();
            return;
        }

        players.forEach(player -> {
            Random random = new Random();
            int index = random.nextInt(spawns.size());
            Spawn spawn = spawns.get(index);
            Shortcuts.teleport(player, map.getLinkedWorld().get().getName(), spawn.getPosition(), spawn.getRotation());
            spawns.remove(index);
        });
    }

    public void leave(Player player) {
        clear(player);
        players.remove(player);
        GameLeaveEvent.fire(this);

        if (stopped) {
            return;
        }

        if (!isRunning()) {
            Shortcuts.defaultTeleport(player);
        }

        if (players.size() <= map.getWinPlayers() && isRunning()) {
            stop();
        } else {
            updateTasks();
        }
    }

    private void clear(Player player) {
        player.gameMode().set(GameModes.SURVIVAL);
        player.getInventory().clear();
        player.stopSounds();

        Optional<PotionEffectData> optionalPotionEffectData = player.getOrCreate(PotionEffectData.class);

        if (!optionalPotionEffectData.isPresent()) {
            return;
        }

        //TODO XP

        PotionEffectData potionEffectData = optionalPotionEffectData.get();
        potionEffectData.addElement(Shortcuts.getPotionEffect(PotionEffectTypes.INSTANT_HEALTH));
        potionEffectData.addElement(Shortcuts.getPotionEffect(PotionEffectTypes.SATURATION));
        player.offer(potionEffectData);
    }

    public boolean isRunning() {
        return gameStartTime != 0;
    }

    public boolean isStopped() {
        return stopped;
    }

    public boolean isOnCountdown() {
        return countdownTask != null;
    }

    public boolean isFull() {
        return players.size() >= map.getMaxPlayers();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map getLinkedMap() {
        return map;
    }

    public long getGameDuration() {
        return isRunning() ? (System.currentTimeMillis() - gameStartTime) : 0;
    }

    public int getMissingPlayers() {
        return Math.max(0, map.getMinPlayers() - players.size());
    }

    public int getCurrentCountdown() {
        return currentCountdown;
    }

    public Scoreboard getLobbyScoreboard() {
        return lobbyScoreboard;
    }

    public Scoreboard getRunningScoreboard() {
        return runningScoreboard;
    }

}
