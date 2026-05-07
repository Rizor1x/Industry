package com.rizor1x.industry.item.custom;

import com.rizor1x.industry.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnergyDrillItem extends PickaxeItem implements IEnergyItem {
    private final int capacity;
    private final int energyPerBlock;
    private final boolean hasModes; // Есть ли режим 3х3?

    public EnergyDrillItem(Tier tier, Properties properties, int capacity, int energyPerBlock, boolean hasModes) {
        super(tier, properties.stacksTo(1));
        this.capacity = capacity;
        this.energyPerBlock = energyPerBlock;
        this.hasModes = hasModes;
    }

    public int getCapacity() { return capacity; }

    public int getEnergy(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
    }

    public void setEnergy(ItemStack stack, int energy) {
        stack.set(ModDataComponents.ENERGY.get(), Math.max(0, Math.min(energy, capacity)));
    }

    // ==========================================
    // ЛОГИКА КОПАНИЯ И ЭНЕРГИИ
    // ==========================================

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
        if (getEnergy(stack) < energyPerBlock) return 1.0F; // Если разряжен - копает как рука

        // Бур = Кирка + Лопата!
        if (state.is(BlockTags.MINEABLE_WITH_SHOVEL) || state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return this.getTier().getSpeed();
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, @NotNull BlockState state) {
        if (getEnergy(stack) < energyPerBlock) return false;
        return super.isCorrectToolForDrops(stack, state) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
        if (level.isClientSide || state.getDestroySpeed(level, pos) == 0.0F) return true;

        int energy = getEnergy(stack);
        if (energy >= energyPerBlock) {
            setEnergy(stack, energy - energyPerBlock); // Тратим энергию за центральный блок

            // Если включен режим 3х3 и это игрок - копаем площадь!
            boolean isAreaMode = stack.getOrDefault(ModDataComponents.AREA_MODE.get(), false);
            if (hasModes && isAreaMode && entity instanceof ServerPlayer player) {
                mineArea(stack, level, pos, player);
            }
        }
        return true;
    }

    // ==========================================
    // СМЕНА РЕЖИМОВ (Shift + ПКМ)
    // ==========================================

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (hasModes && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                boolean currentMode = stack.getOrDefault(ModDataComponents.AREA_MODE.get(), false);
                stack.set(ModDataComponents.AREA_MODE.get(), !currentMode);

                Component modeText = (!currentMode) ?
                        Component.literal("3x3 (Area)").withStyle(ChatFormatting.GOLD) :
                        Component.literal("1x1 (Normal)").withStyle(ChatFormatting.GREEN);

                player.displayClientMessage(Component.literal("Drill Mode: ").append(modeText), true);
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    // ==========================================
    // ЛОГИКА 3х3 (Продвинутая математика)
    // ==========================================
    private void mineArea(ItemStack stack, Level level, BlockPos centerPos, ServerPlayer player) {
        HitResult hit = player.pick(5.0D, 0.0F, false);
        if (!(hit instanceof BlockHitResult blockHit)) return;

        Direction side = blockHit.getDirection(); // С какой стороны игрок смотрит на блок

        // Определяем радиус копания (1 блок во все стороны)
        int xRadius = side.getAxis() == Direction.Axis.X ? 0 : 1;
        int yRadius = side.getAxis() == Direction.Axis.Y ? 0 : 1;
        int zRadius = side.getAxis() == Direction.Axis.Z ? 0 : 1;

        for (int x = -xRadius; x <= xRadius; x++) {
            for (int y = -yRadius; y <= yRadius; y++) {
                for (int z = -zRadius; z <= zRadius; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; // Центральный блок уже сломан

                    if (getEnergy(stack) < energyPerBlock) return; // Энергия кончилась во время 3х3!

                    BlockPos targetPos = centerPos.offset(x, y, z);
                    BlockState targetState = level.getBlockState(targetPos);

                    // Если блок можно сломать буром
                    if (isCorrectToolForDrops(stack, targetState) && targetState.getDestroySpeed(level, targetPos) >= 0) {
                        // Ломаем блок как будто это сделал игрок (чтобы выпал дроп)
                        if (((ServerLevel) level).destroyBlock(targetPos, true, player)) {
                            setEnergy(stack, getEnergy(stack) - energyPerBlock); // Тратим энергию
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // ВИЗУАЛ (ПОЛОСКА ЭНЕРГИИ)
    // ==========================================

    @Override public boolean isBarVisible(@NotNull ItemStack stack) { return true; }
    @Override public int getBarWidth(@NotNull ItemStack stack) { return Math.round(13.0F * getEnergy(stack) / capacity); }
    @Override public int getBarColor(@NotNull ItemStack stack) { return 0xFF0000; }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal(getEnergy(stack) + " / " + capacity + " FE").withStyle(ChatFormatting.RED));
        if (hasModes) {
            boolean isArea = stack.getOrDefault(ModDataComponents.AREA_MODE.get(), false);
            tooltipComponents.add(Component.literal("Mode: " + (isArea ? "3x3" : "1x1")).withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}