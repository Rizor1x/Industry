package com.rizor1x.industry.item.custom;

import com.rizor1x.industry.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class BatteryItem extends Item {
    private final int capacity;

    public BatteryItem(Properties properties, int capacity) {
        super(properties.stacksTo(1)); // Батарейки не стакаются!
        this.capacity = capacity;
    }

    public int getCapacity() { return capacity; }

    // Получаем текущую энергию из предмета
    public int getEnergy(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
    }

    // 1. Полоска прочности (будет показывать заряд)
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true; // Полоска видна всегда
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        // Длина полоски 13 пикселей
        return Math.round(13.0F * getEnergy(stack) / capacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFF0000; // Красный цвет (как на твоем GUI!)
    }

    // 2. Всплывающий текст
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(getEnergy(stack) + " / " + capacity + " FE").withStyle(ChatFormatting.RED));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}