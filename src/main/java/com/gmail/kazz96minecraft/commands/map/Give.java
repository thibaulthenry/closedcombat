package com.gmail.kazz96minecraft.commands.map;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.utils.CCItems;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Arrays;
import java.util.function.Function;

public class Give extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        Player player = arguments.<Player>getOne("player").orElseThrow(supplyError("Please insert a valid player name"));
        ItemStack item = arguments.<ItemStack>getOne("item").orElseThrow(supplyError("Please insert a valid item"));

        Inventory hotbarInventory = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
        Inventory mainInventory = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));

        Function<ItemStack, Boolean> giveAction = anItem -> {
            for (Inventory slot : hotbarInventory.union(mainInventory)) {
                if (slot.size() == 0) {
                    slot.set(anItem);
                    player.sendMessage(Text.of(TextColors.AQUA, anItem.get(Keys.DISPLAY_NAME).orElse(Text.of("An item")), " have been added to your inventory"));
                    return true;
                }
            }
            return false;
        };

        if (item.getType().equals(ItemTypes.BARRIER)) {
            if (Arrays.stream(CCItems.values())
                    .filter(ccItem -> !ccItem.get().getType().equals(ItemTypes.BARRIER))
                    .allMatch(ccItem -> giveAction.apply(ccItem.get()))) {
                return CommandResult.success();
            }
        } else if (giveAction.apply(item)) {
            return CommandResult.success();
        }

        throw new CommandException(Text.of("Unable to give anything, the " + player.getName() + "'s inventory is full of items"));
    }

    @Override
    public CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("closedcombat.map.give")
                .description(Text.of("Give a custom Closed Combat item"))
                .arguments(
                        GenericArguments.playerOrSource(Text.of("player")),
                        GenericArguments.choices(Text.of("item"), CCItems.map())
                )
                .executor(instance)
                .build();
    }
}
