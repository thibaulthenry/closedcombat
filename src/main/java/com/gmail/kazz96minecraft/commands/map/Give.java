package com.gmail.kazz96minecraft.commands.map;

import com.gmail.kazz96minecraft.commands.AbstractCommand;
import com.gmail.kazz96minecraft.utils.CustomItems;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Give extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {
        Player player = arguments.<Player>getOne("player").orElseThrow(errorBySponge);
        ItemStack item = arguments.<ItemStack>getOne("item").orElseThrow(errorBySponge);

        Inventory hotbarInventory = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
        Inventory mainInventory = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));

        for (Inventory slot : hotbarInventory.union(mainInventory)) {
            if (slot.size() == 0) {
                slot.set(item);
                player.sendMessage(Text.of(TextColors.AQUA, item.get(Keys.DISPLAY_NAME).orElse(Text.of("An item")), " have been added to your inventory"));
                return CommandResult.success();
            }
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
                        GenericArguments.choices(Text.of("item"), CustomItems.map())
                )
                .executor(instance)
                .build();
    }
}
