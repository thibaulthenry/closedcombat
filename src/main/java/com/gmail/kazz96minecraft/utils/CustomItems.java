package com.gmail.kazz96minecraft.utils;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CustomItems {
    CCSTICK(ItemStack.builder().itemType(ItemTypes.STICK).add(Keys.DISPLAY_NAME, Text.of("CCStick")).build());

    private ItemStack item;

    CustomItems(ItemStack item) {
        this.item = item;
    }

    public ItemStack get() {
        return item;
    }

    public static Map<String, ItemStack> map() {
        return Arrays.stream(CustomItems.values()).collect(Collectors.toMap(customItem -> StringUtils.capitalize(customItem.name()),CustomItems::get));
    }

}
