package com.gmail.kazz96minecraft.elements;

import com.flowpowered.math.vector.Vector3i;
import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.elements.serializers.WarpSerializer;
import com.gmail.kazz96minecraft.utils.CCSigns;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Map {

    public static Location<World> leftBlockMarker;
    public static Location<World> rightBlockMarker;

    public enum Options {
        LOBBY, SPAWN, BREAK, PLACE, MAX, MIN, COUNTDOWN
    }

    private String name;
    private String worldName;
    private Vector3i leftLimitPosition;
    private Vector3i rightLimitPosition;

    private List<Spawn> spawnList;
    private Vector3i lobbyPosition;

    private Integer maxPlayers;
    private Integer minPlayers;
    private Integer countdown;
    private List<String> breakableBlocks;
    private List<String> placeableBlocks;

    public Map(String name) {
        assert leftBlockMarker != null;
        assert rightBlockMarker != null;

        leftLimitPosition = leftBlockMarker.getBlockPosition();
        rightLimitPosition = rightBlockMarker.getBlockPosition();

        this.name = name;
        worldName = leftBlockMarker.getExtent().getName();

        spawnList = new ArrayList<>();
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

        if (maxPlayers < minPlayers || maxPlayers > getTotalSpawnsCapacity()) {
            maxPlayers = getTotalSpawnsCapacity();
        }
    }

    private void affectOptions(Location<World> signLocation, List<Text> lines) {
        String option = lines.get(1).toPlain();

        try {
            switch (Options.valueOf(option)) {
                case LOBBY:
                    lobbyPosition = signLocation.getBlockPosition();
                    break;
                case SPAWN:
                    Spawn spawn = new Spawn(signLocation);
                    if (StringUtils.isNumeric(lines.get(2).toPlain())) {
                        spawn.setCapacity(Integer.parseInt(lines.get(2).toPlain()));
                    }
                    spawnList.add(spawn);
                    break;
                case BREAK:
                    breakableBlocks.add(signLocation.add(0, -1, 0).getBlock().getId());
                    break;
                case PLACE:
                    placeableBlocks.add(signLocation.add(0, -1, 0).getBlock().getId());
                    break;
                case MAX:
                    if (StringUtils.isNumeric(lines.get(2).toPlain())) {
                        maxPlayers = Integer.parseInt(lines.get(2).toPlain());
                    } else {
                        ClosedCombat.getInstance().sendConsole("A corrupted MAX CCSign has been detected. Avoided..");
                    }
                    break;
                case MIN:
                    if (StringUtils.isNumeric(lines.get(2).toPlain())) {
                        minPlayers = Integer.parseInt(lines.get(2).toPlain());
                        if (Integer.parseInt(lines.get(2).toPlain()) < 1) {
                            minPlayers = 1;//TODO WARNING MESSAGE
                        }
                    } else {
                        ClosedCombat.getInstance().sendConsole("A corrupted MIN CCSign has been detected. Avoided..");
                    }
                    break;
                case COUNTDOWN:
                    if (StringUtils.isNumeric(lines.get(2).toPlain())) {
                        countdown = Integer.parseInt(lines.get(2).toPlain());
                        if (Integer.parseInt(lines.get(2).toPlain()) < 3) {
                            countdown = 3;
                        }
                    } else {
                        ClosedCombat.getInstance().sendConsole("A corrupted COUNTDOWN CCSign has been detected. Avoided..");
                    }
                    break;
            }
        } catch(IllegalArgumentException e) {
            ClosedCombat.getInstance().getLogger().warn("A CCSign has been detected with a wrong argument : " + option);
        }
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

    Vector3i getLobbyPosition() {
        return lobbyPosition;
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

    public Integer getTotalSpawnsCapacity() {
        return spawnList.stream()
                .mapToInt(Spawn::getCapacity)
                .sum();
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public List<Spawn> getSpawnList() {
        return spawnList;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public Integer getMinPlayers() {
        return minPlayers;
    }

    public Integer getCountdown() {
        return countdown;
    }

    public List<String> getBreakableBlocks() {
        return breakableBlocks;
    }

    public List<String> getPlaceableBlocks() {
        return placeableBlocks;
    }

    public Vector3i getLeftLimitPosition() {
        return leftLimitPosition;
    }

    public Vector3i getRightLimitPosition() {
        return rightLimitPosition;
    }

}
