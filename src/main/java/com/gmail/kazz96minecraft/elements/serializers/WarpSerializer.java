package com.gmail.kazz96minecraft.elements.serializers;

import com.flowpowered.math.vector.Vector3i;
import com.gmail.kazz96minecraft.ClosedCombat;
import com.gmail.kazz96minecraft.elements.Map;
import com.gmail.kazz96minecraft.elements.Warp;
import com.gmail.kazz96minecraft.utils.Storage;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class WarpSerializer extends AbstractSerializer<Warp> {

    private static final WarpSerializer instance = new WarpSerializer();

    private WarpSerializer() {
        super(Warp.class, Storage.warpsDirectory, new ArrayList<>(), WarpSerializer::getJsonFileName);
    }

    public static WarpSerializer getInstance() {
        return instance;
    }

    public static String getJsonFileName(Warp warp) {
        Vector3i signPosition = warp.getPosition();

        return String.join("-",
                warp.getLinkedMapName(),
                warp.getWorldName(),
                String.valueOf(signPosition.getX()),
                String.valueOf(signPosition.getY()),
                String.valueOf(signPosition.getZ())
        );
    }

    public void verifyRegisteredWarps() {
        AtomicInteger unreachableWarp = new AtomicInteger(0);

        new ArrayList<>(getList()).forEach(warp -> {
            if (!warp.getTileEntity().isPresent() || !warp.getLinkedMap().isPresent()) {
                if (removeRegisteredWarp(warp)) {
                    unreachableWarp.addAndGet(1);
                }
                return;
            }

            warp.reset();
        });

        if (unreachableWarp.get() > 0) {
            ClosedCombat.getInstance().sendConsole(unreachableWarp.get() + " CCSign unreachable. Deleting JSON file" + (unreachableWarp.get() > 1 ? "s" : ""));
        }
    }

    private boolean removeRegisteredWarp(Warp warp) {
        if (!warp.getLocation().isPresent()) {
            return false;
        }

        return removeRegisteredWarp(warp.getLocation().get());
    }

    public boolean removeRegisteredWarp(Location<World> location) {//TODO ADD WORLD FILTER
        Optional<Warp> optionalWarp = getList().stream()
                .filter(warp -> warp.getPosition().equals(location.getBlockPosition()))
                .findFirst();

        if (!optionalWarp.isPresent()) {
            return false;
        }

        getList().remove(optionalWarp.get());
        return Storage.deleteWarpFile(optionalWarp.get());
    }

    public boolean isRegisteredWarp(Location<World> location) {
        return getList().stream()
                .anyMatch(warp -> warp.getPosition().equals(location.getBlockPosition()));
    }

    public Optional<Map> getAssociatedMap(Location<World> location) {
        return getList().stream()
                .filter(warp -> warp.getPosition().equals(location.getBlockPosition()))
                .map(Warp::getLinkedMap)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public List<Warp> getWarps(Map map) {
        return getList().stream()
                .filter(warp -> warp.getLinkedMap().isPresent())
                .filter(warp -> warp.getLinkedMap().get().equals(map))
                .collect(Collectors.toList());
    }
}
