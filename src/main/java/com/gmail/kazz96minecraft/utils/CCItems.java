package com.gmail.kazz96minecraft.utils;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum CCItems {
    ALL(ItemStack.builder().itemType(ItemTypes.BARRIER).build()),
    CCSTICK(ItemStack.builder().itemType(ItemTypes.STICK).add(Keys.DISPLAY_NAME, Text.of("CCStick")).build()),
    CCHATCHET(ItemStack.builder().itemType(ItemTypes.WOODEN_AXE).add(Keys.DISPLAY_NAME, Text.of("CCHatchet")).build()),
    CCCOOKIE(ItemStack.builder().itemType(ItemTypes.COOKIE).add(Keys.DISPLAY_NAME, Text.of("CCCookie")).build()),
    CCCOMPASS(ItemStack.builder().itemType(ItemTypes.COMPASS).add(Keys.DISPLAY_NAME, Text.of("CCCompass")).build());

    private final ItemStack item;

    CCItems(ItemStack item) {
        this.item = item;
    }

    public static Map<String, ItemStack> map() {
        return Arrays.stream(CCItems.values())
                .collect(Collectors.toMap(customItem -> StringUtils.capitalize(customItem.name()), CCItems::get));
    }

    public ItemStack get() {
        return item;
    }

}
