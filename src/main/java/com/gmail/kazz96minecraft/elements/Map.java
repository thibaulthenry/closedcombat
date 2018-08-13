package com.gmail.kazz96minecraft.elements;

import com.flowpowered.math.vector.Vector3i;
import com.gmail.kazz96minecraft.ClosedCombat;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Map {

    public static Location<World> leftBlockMarker;
    public static Location<World> rightBlockMarker;

    private String name;
    private String worldName;
    private List<Spawn> spawnList;

    public Map() {
    }

    public Map(String mapName) {

        assert leftBlockMarker != null;
        assert rightBlockMarker != null;


        worldName = leftBlockMarker.getExtent().getName();

        name = mapName;

        spawnScan();
    }

    private void spawnScan() {
        if (!getLinkedWorld().isPresent()) {
            return;
        }

        World world = getLinkedWorld().get();
        spawnList = new ArrayList<>();

        Vector3i leftPosition = Map.leftBlockMarker.getBlockPosition();
        Vector3i rightPosition = Map.rightBlockMarker.getBlockPosition();

        int leastX = Math.min(leftPosition.getX(), rightPosition.getX());
        int leastY = Math.min(leftPosition.getY(), rightPosition.getY());
        int leastZ = Math.min(leftPosition.getZ(), rightPosition.getZ());
        int upmostX = Math.max(leftPosition.getX(), rightPosition.getX());
        int upmostY = Math.max(leftPosition.getY(), rightPosition.getY());
        int upmostZ = Math.max(leftPosition.getZ(), rightPosition.getZ());

        for (int x = leastX; x <= upmostX; x++) {
            for (int y = leastY; y <= upmostY; y++) {
                for (int z = leastZ; z <= upmostZ; z++) {
                    if (world.getBlock(x, y, z).getType().equals(BlockTypes.STANDING_SIGN) || world.getBlock(x, y, z).getType().equals(BlockTypes.WALL_SIGN)) {
                        Location<World> spawnLocation = new Location<>(world, x, y, z);

                        Optional<SignData> optionalSignData = spawnLocation.getOrCreate(SignData.class);

                        if (optionalSignData.isPresent()) {
                            List<Text> lines = optionalSignData.get().asList();
                            if (lines.get(0).toPlain().equals("[CCSPAWN]")) {
                                Spawn spawn = new Spawn(spawnLocation);
                                if (StringUtils.isNumeric(lines.get(1).toPlain())) {
                                    spawn.setCapacity(Integer.parseInt(lines.get(1).toPlain()));
                                }

                                spawnList.add(spawn);
                            }
                        }
                    }
                }
            }
        }
    }

    public Optional<World> getLinkedWorld() {
        if (!Sponge.getServer().getWorld(worldName).isPresent()) {
            ClosedCombat.getInstance().getLogger().warn("No existing world found for the linked one of " + name + ". Default world applied");
        }

        return Sponge.getServer().getWorld(worldName);
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
}
