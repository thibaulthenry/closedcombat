package com.gmail.kazz96minecraft.elements;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.elements.serializers.WarpSerializer;
import com.gmail.kazz96minecraft.utils.CCSigns;
import com.gmail.kazz96minecraft.utils.Shortcuts;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Map {

    public static Location<World> leftBlockMarker;
    public static Location<World> rightBlockMarker;

    private List<Spawn> spawns;
    private List<String> breakableBlocks;
    private List<String> placeableBlocks;

    private String name;
    private String worldName;

    private Vector3i leftLimitPosition;
    private Vector3i rightLimitPosition;
    private Vector3i lobbyPosition;
    private Vector3d lobbyRotation;

    private int minPlayers;
    private int maxPlayers;
    private int winPlayers;
    private int limit;
    private int countdown;

    public Map(String name) {
        assert leftBlockMarker != null;
        assert rightBlockMarker != null;

        leftLimitPosition = leftBlockMarker.getBlockPosition();
        rightLimitPosition = rightBlockMarker.getBlockPosition();

        this.name = name;
        worldName = leftBlockMarker.getExtent().getName();

        spawns = new ArrayList<>();
        breakableBlocks = new ArrayList<>();
        placeableBlocks = new ArrayList<>();

        minPlayers = 1;

        mapScan();
    }

    private void mapScan() {
        if (!getLinkedWorld().isPresent()) {
            return;
        }

        World world = getLinkedWorld().get();

        int leastX = Math.min(leftLimitPosition.getX(), rightLimitPosition.getX());
        int leastY = Math.min(leftLimitPosition.getY(), rightLimitPosition.getY());
        int leastZ = Math.min(leftLimitPosition.getZ(), rightLimitPosition.getZ());
        int upmostX = Math.max(leftLimitPosition.getX(), rightLimitPosition.getX());
        int upmostY = Math.max(leftLimitPosition.getY(), rightLimitPosition.getY());
        int upmostZ = Math.max(leftLimitPosition.getZ(), rightLimitPosition.getZ());

        for (int x = leastX; x <= upmostX; x++) {
            for (int y = leastY; y <= upmostY; y++) {
                for (int z = leastZ; z <= upmostZ; z++) {
                    Location<World> location = new Location<>(world, x, y, z);
                    CCSigns.getLines(location).ifPresent(texts -> affectOptions(location, texts));
                }
            }
        }

        if (countdown < 3) {
            countdown = 60;
        }

        if (minPlayers < 2) {
            minPlayers = 2;
        }

        if (maxPlayers < minPlayers || maxPlayers > getTotalSpawnsCapacity()) {
            maxPlayers = getTotalSpawnsCapacity();
        }

        if (winPlayers < 1 || winPlayers > minPlayers || winPlayers > maxPlayers) {
            winPlayers = 1;
        }
    }

    public Map.Options affectOptions(Location<World> signLocation, List<Text> lines) {
        String option = StringUtils.upperCase(lines.get(1).toPlain());
        String argument = lines.get(2).toPlain();

        try {
            switch (Options.valueOf(option)) {
                case LOBBY:
                    lobbyPosition = signLocation.getBlockPosition();
                    lobbyRotation = CCSigns.getSignDirection(signLocation).orElse(Direction.NORTH).asOffset();
                    break;
                case SPAWN:
                    Spawn spawn = new Spawn(signLocation);
                    if (StringUtils.isNumeric(argument)) {
                        spawn.setCapacity(Integer.parseInt(argument));
                    }
                    spawns.add(spawn);
                    break;
                case BREAK:
                    breakableBlocks.add(signLocation.add(0, -1, 0).getBlock().getId());
                    break;
                case PLACE:
                    placeableBlocks.add(signLocation.add(0, -1, 0).getBlock().getId());
                    break;
                case WIN:
                    if (StringUtils.isNumeric(argument)) {
                        winPlayers = Integer.parseInt(argument);
                    } else {
                        ClosedCombat.getInstance().sendConsole("A corrupted WIN CCSign has been detected. Avoided..");
                    }
                    break;
                case MAX:
                    if (StringUtils.isNumeric(argument)) {
                        maxPlayers = Integer.parseInt(argument);
                    } else {
                        ClosedCombat.getInstance().sendConsole("A corrupted MAX CCSign has been detected. Avoided..");
                    }
                    break;
                case MIN:
                    if (StringUtils.isNumeric(argument)) {
                        minPlayers = Integer.parseInt(argument);
                    } else {
                        ClosedCombat.getInstance().sendConsole("A corrupted MIN CCSign has been detected. Avoided..");
                    }
                    break;
                case COUNTDOWN:
                    if (StringUtils.isNumeric(argument)) {
                        countdown = Integer.parseInt(argument);
                    } else {
                        ClosedCombat.getInstance().sendConsole("A corrupted COUNTDOWN CCSign has been detected. Avoided..");
                    }
                    break;
                case LIMIT:
                    if (StringUtils.isNumeric(argument)) {
                        limit = Integer.parseInt(argument);
                    } else {
                        ClosedCombat.getInstance().sendConsole("A corrupted LIMIT CCSign has been detected. Avoided..");
                    }
                    break;
            }
        } catch(IllegalArgumentException e) {
            ClosedCombat.getInstance().getLogger().warn("A CCSign has been detected with a wrong argument : " + option);
        }

        return Options.valueOf(option);
    }

    void restoreWorld() {
        Task.builder().name("Restauring " + worldName + " from " + name).execute(() -> {
            Shortcuts.runCommand("cc", "world", "unload", worldName);
            Shortcuts.runCommand("cc", "world", "extract", worldName);
            Shortcuts.runCommand("cc", "world", "load", worldName);
        }).submit(ClosedCombat.getInstance());
    }

    void deleteVisibleSigns() {
        if (!getLinkedWorld().isPresent()) {
            return;
        }

        new Location<>(getLinkedWorld().get(), lobbyPosition).removeBlock();
        spawns.forEach(spawn -> new Location<>(getLinkedWorld().get(), spawn.getPosition()).removeBlock());
    }

    public Integer getTotalSpawnsCapacity() {
        return spawns.stream()
                .mapToInt(Spawn::getCapacity)
                .sum();
    }

    boolean isOutside(Player targetPlayer) {
        return isOutside(targetPlayer.getLocation());
    }

    private boolean isOutside(Location<World> location) {
        int x = location.getBlockX();
        int z = location.getBlockZ();

        if (!location.getExtent().getName().equals(worldName)) {
            return true;
        }

        int leastX = Math.min(leftLimitPosition.getX(), rightLimitPosition.getX());
        int leastZ = Math.min(leftLimitPosition.getZ(), rightLimitPosition.getZ());
        int upmostX = Math.max(leftLimitPosition.getX(), rightLimitPosition.getX());
        int upmostZ = Math.max(leftLimitPosition.getZ(), rightLimitPosition.getZ());

        return x < leastX || upmostX < x || z < leastZ || upmostZ < z;
    }

    public Vector3i getLeftLimitPosition() {
        return leftLimitPosition;
    }

    Vector3i getLobbyPosition() {
        return lobbyPosition;
    }

    Vector3d getLobbyRotation() {
        return lobbyRotation;
    }

    public Optional<World> getLinkedWorld() {
        if (!Sponge.getServer().getWorld(worldName).isPresent()) {
            ClosedCombat.getInstance().getLogger().warn("No existing or loaded world found for the linked one of " + name);
        }

        return Sponge.getServer().getWorld(worldName);
    }

    public Integer getWarpSignNumber() {
        return Math.toIntExact(WarpSerializer.getInstance().getList().stream().filter(sign -> sign.getLinkedMapName().equals(name)).count());
    }

    public Vector3i getRightLimitPosition() {
        return rightLimitPosition;
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public List<Spawn> getSpawns() {
        return spawns;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getWinPlayers() {
        return winPlayers;
    }

    public int getCountdown() {
        return countdown;
    }

    public enum Options {
        LOBBY, SPAWN, BREAK, PLACE, MAX, MIN, WIN, COUNTDOWN, LIMIT
    }

    public List<String> getBreakableBlocks() {
        return breakableBlocks;
    }

    public List<String> getPlaceableBlocks() {
        return placeableBlocks;
    }
}
