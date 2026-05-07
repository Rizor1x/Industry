package com.rizor1x.industry.block.entity;

import com.rizor1x.industry.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;

public class BatteryBoxBlockEntity extends BaseMachineBlockEntity {

    public BatteryBoxBlockEntity(BlockPos pos, BlockState state) {
        // 40 000 энергии. 2 слота (Слот 0 - заряжать предмет, Слот 1 - разряжать предмет в блок)
        super(ModBlockEntities.BATTERY_BOX_BE.get(), pos, state, 40000, 0, 2);
    }

    @Override public boolean canExtractEnergy() { return true; }
    @Override public int getInputSlot() { return -1; } // Руду не принимает
    @Override public int getOutputSlot() { return -1; } // Ничего не выплевывает

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) return;
        boolean isDirty = false;

        // 1. ЗАРЯЖАЕМ предмет в слоте (0) ОТ Батбокса
        ItemStack chargeSlot = inventory.getStackInSlot(0);
        if (chargeSlot.has(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get()) && chargeSlot.getItem() instanceof com.rizor1x.industry.item.custom.IEnergyItem bat) {
            int batEnergy = chargeSlot.get(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get());
            int batCap = bat.getCapacity();
            if (this.energy > 0 && batEnergy < batCap) {
                int transfer = Math.min(this.energy, Math.min(100, batCap - batEnergy));
                this.energy -= transfer;
                chargeSlot.set(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get(), batEnergy + transfer);
                isDirty = true;
            }
        }

        // 2. РАЗРЯЖАЕМ предмет в слоте (1) В Батбокс
        ItemStack dischargeSlot = inventory.getStackInSlot(1);
        if (dischargeSlot.has(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get()) && dischargeSlot.getItem() instanceof com.rizor1x.industry.item.custom.IEnergyItem bat) {
            int batEnergy = dischargeSlot.get(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get());
            if (this.energy < this.maxEnergy && batEnergy > 0) {
                int transfer = Math.min(this.maxEnergy - this.energy, Math.min(100, batEnergy));
                this.energy += transfer;
                dischargeSlot.set(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get(), batEnergy - transfer);
                isDirty = true;
            }
        }

        // 3. ОТПРАВЛЯЕМ ЭНЕРГИЮ В ПРОВОДА (Только с Оранжевых сторон!)
        if (this.energy > 0) {
            for (Direction dir : Direction.values()) {
                int relativeSide = getRelativeDirectionIndex(dir); // Это метод из BaseMachineBlockEntity

                // Если сторона настроена как Выход (Оранжевая - 2)
                if (sideConfig[relativeSide] == 2) {
                    BlockPos targetPos = getBlockPos().relative(dir);
                    var storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, dir.getOpposite());
                    if (storage != null && storage.canReceive()) {
                        int sent = storage.receiveEnergy(Math.min(this.energy, 32), false); // Отдаем по 32 FE
                        if (sent > 0) {
                            this.energy -= sent;
                            isDirty = true;
                        }
                    }
                }
            }
        }

        if (isDirty) setChanged();
    }

    // Вспомогательный метод (чтобы вызвать приватный метод из Базы)
    private int getRelativeDirectionIndex(Direction absoluteSide) {
        if (absoluteSide == Direction.UP) return 1;
        if (absoluteSide == Direction.DOWN) return 0;
        Direction facing = getBlockState().getValue(com.rizor1x.industry.block.custom.BaseMachineBlock.FACING);
        if (absoluteSide == facing) return 2;
        if (absoluteSide == facing.getOpposite()) return 3;
        if (absoluteSide == facing.getClockWise()) return 4;
        if (absoluteSide == facing.getCounterClockWise()) return 5;
        return 0;
    }

    @Override
    public boolean canInsertItem(int slot, @org.jetbrains.annotations.NotNull ItemStack stack) {
        // Если у предмета есть компонент ENERGY - пускаем его в Батбокс!
        return stack.has(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new com.rizor1x.industry.menu.BatteryBoxMenu(containerId, playerInventory, this, this.data);
    }
}