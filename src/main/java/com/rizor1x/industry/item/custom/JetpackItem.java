package com.rizor1x.industry.item.custom;

import com.rizor1x.industry.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JetpackItem extends ArmorItem implements IEnergyItem {
    public final int capacity;
    public final int energyPerTick; // Сколько жрет за тик полета
    public final double thrust;     // Сила толчка вверх (0.08 для обычного, 0.15 для продвинутого)
    public final double maxSpeedY;  // Максимальная скорость вверх (0.5 для обычного, 1.0 для продвинутого)
    public final boolean isCreative; // Дает ли креативный полет (для Кванта)

    public JetpackItem(Holder<ArmorMaterial> material, int capacity, int energyPerTick, double thrust, double maxSpeedY, boolean isCreative, Properties properties) {
        super(material, Type.CHESTPLATE, properties.stacksTo(1));
        this.capacity = capacity;
        this.energyPerTick = energyPerTick;
        this.thrust = thrust;
        this.maxSpeedY = maxSpeedY;
        this.isCreative = isCreative;
    }

    @Override public int getCapacity() { return capacity; }

    public int getEnergy(ItemStack stack) { return stack.getOrDefault(ModDataComponents.ENERGY.get(), 0); }
    public void setEnergy(ItemStack stack, int energy) { stack.set(ModDataComponents.ENERGY.get(), Math.clamp(energy, 0, capacity)); }

    @Override public boolean isBarVisible(@NotNull ItemStack stack) { return true; }
    @Override public int getBarWidth(@NotNull ItemStack stack) { return Math.round(13.0F * getEnergy(stack) / capacity); }
    @Override public int getBarColor(@NotNull ItemStack stack) { return 0xFF0000; }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(getEnergy(stack) + " / " + capacity + " FE").withStyle(ChatFormatting.RED));
        if (isCreative) {
            tooltipComponents.add(Component.literal("Creative Flight").withStyle(ChatFormatting.LIGHT_PURPLE));
        } else {
            tooltipComponents.add(Component.literal("Thrust: " + thrust).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}