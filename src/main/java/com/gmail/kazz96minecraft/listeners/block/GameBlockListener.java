package com.gmail.kazz96minecraft.listeners.block;

import com.gmail.kazz96minecraft.elements.Game;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class GameBlockListener {

    @Listener
    public void onBreakableBlock(ChangeBlockEvent.Break event, @First Player player) {
        Optional<Game> optionalGame = Game.get(player);

        if (!optionalGame.isPresent()) {
            return;
        }

        List<String> breakableBlocks = optionalGame.get().getLinkedMap().getBreakableBlocks();

        boolean breakable = event.getTransactions().stream()
                .map(Transaction::getOriginal)
                .anyMatch(blockSnapshot -> breakableBlocks.contains(blockSnapshot.getExtendedState().getId()));

        if (!breakable) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onPlaceableBlock(ChangeBlockEvent.Place event, @First Player player) {
        Optional<Game> optionalGame = Game.get(player);

        if (!optionalGame.isPresent()) {
            return;
        }

        List<String> placeableBlocks = optionalGame.get().getLinkedMap().getPlaceableBlocks();

        boolean placeable = event.getTransactions().stream()
                .map(Transaction::getFinal)
                .anyMatch(blockSnapshot -> placeableBlocks.contains(blockSnapshot.getExtendedState().getId()));

        if (!placeable) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onPoweredUseOnly(InteractBlockEvent.Secondary event, @First Player player) {
        Optional<Game> optionalGame = Game.get(player);

        if (!optionalGame.isPresent()) {
            return;
        }

        Stream<BlockType> poweredOnlyBlocks = Stream.of(
                BlockTypes.DROPPER,
                BlockTypes.DISPENSER,
                BlockTypes.HOPPER,
                BlockTypes.FURNACE
        );

        boolean typeEquality = poweredOnlyBlocks.anyMatch(blockType -> blockType.equals(event.getTargetBlock().getExtendedState().getType()));

        if (typeEquality) {
            event.setCancelled(true);
        }
    }
}
