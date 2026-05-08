package com.rizor1x.industry.item.custom;

import com.rizor1x.industry.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BatPackItem extends ArmorItem implements IEnergyItem {
    private final int capacity;
    private final int transferRate; // Сколько энергии передает в тик (например, 100)

    public BatPackItem(Holder<ArmorMaterial> material, int capacity, int transferRate, Properties properties) {
        super(material, Type.CHESTPLATE, properties.stacksTo(1));
        this.capacity = capacity;
        this.transferRate = transferRate;
    }

    @Override
    public int getCapacity() { return capacity; }

    public int getEnergy(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
    }

    // ==========================================
    // МАГИЯ АВТОЗАРЯДКИ БУРОВ!
    // ==========================================
    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;

        // Проверяем, надет ли ранец на ГРУДЬ
        if (player.getItemBySlot(EquipmentSlot.CHEST) == stack) {
            // Пытаемся зарядить предмет в правой и левой руке
            chargeItem(stack, player.getMainHandItem());
            chargeItem(stack, player.getOffhandItem());
        }
    }

    private void chargeItem(ItemStack batpack, ItemStack tool) {
        // Если в руке пусто или это не Энерго-предмет - отмена
        if (tool.isEmpty() || !(tool.getItem() instanceof IEnergyItem energyTool)) return;

        int batEnergy = getEnergy(batpack);
        if (batEnergy <= 0) return; // Ранец разряжен

        int toolEnergy = tool.getOrDefault(ModDataComponents.ENERGY.get(), 0);
        int toolCap = energyTool.getCapacity();

        // Если инструмент не заряжен до конца
        if (toolEnergy < toolCap) {
            int transfer = Math.min(batEnergy, Math.min(transferRate, toolCap - toolEnergy));
            if (transfer > 0) {
                // Забираем из ранца, кладем в бур
                batpack.set(ModDataComponents.ENERGY.get(), batEnergy - transfer);
                tool.set(ModDataComponents.ENERGY.get(), toolEnergy + transfer);
            }
        }
    }

    // ==========================================
    // ВИЗУАЛ
    // ==========================================
    @Override public boolean isBarVisible(@NotNull ItemStack stack) { return true; }
    @Override public int getBarWidth(@NotNull ItemStack stack) { return Math.round(13.0F * getEnergy(stack) / capacity); }
    @Override public int getBarColor(@NotNull ItemStack stack) { return 0xFF0000; }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(getEnergy(stack) + " / " + capacity + " FE").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.literal("Charges held tools").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}