package com.rizor1x.industry.block.entity;

import com.rizor1x.industry.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;

public class SolidFuelGeneratorBlockEntity extends BaseMachineBlockEntity {

    public SolidFuelGeneratorBlockEntity(BlockPos pos, BlockState state) {
        // 10000 энергии. 2 слота (0 = Уголь, 1 = Зарядка батареек в будущем)
        super(ModBlockEntities.COAL_GENERATOR_BE.get(), pos, state, 10000, 0, 2);
    }

    // Генератор только ОТДАЕТ энергию
    @Override
    public boolean canReceiveEnergy() { return false; }
    @Override
    public boolean canExtractEnergy() { return true; }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) return;
        processBatterySlot();
        boolean isDirty = false;

        // 1. СЖИГАНИЕ ТОПЛИВА
        if (this.progress > 0) {
            this.progress--; // Топливо горит
            this.energy = Math.min(this.energy + 10, this.maxEnergy); // Даем 10 FE за тик
            isDirty = true;
        } else if (this.energy < this.maxEnergy) {
            // Если генератор не полный и не горит - проверяем слот с топливом
            ItemStack fuelSlot = inventory.getStackInSlot(0);

            // Берем ванильное время горения (Уголь = 1600 тиков)
            int burnTime = fuelSlot.getBurnTime(RecipeType.SMELTING);
            if (burnTime > 0) {
                this.maxProgress = burnTime;
                this.progress = burnTime;
                fuelSlot.shrink(1); // Тратим 1 уголь
                isDirty = true;
            }
        }

        // 2. ОТПРАВКА ЭНЕРГИИ СОСЕДЯМ (Авто-Push)
        if (this.energy > 0) {
            pushEnergyToNeighbors();
        }

        autoIo(); // Для засасывания угля из воронок
        if (isDirty) setChanged();
    }

    private void pushEnergyToNeighbors() {
        if (level == null || level.isClientSide) return;

        // Перебираем все 6 сторон вокруг генератора
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = getBlockPos().relative(dir);
            // Спрашиваем у соседа: "У тебя есть место для энергии?"
            var storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, dir.getOpposite());

            if (storage != null && storage.canReceive()) {
                // Пытаемся отдать 100 FE за тик
                int sent = storage.receiveEnergy(Math.min(this.energy, 100), false);
                this.energy -= sent;
                setChanged();
                if (this.energy <= 0) break; // Если энергия кончилась, прекращаем
            }
        }
    }

    // Фильтр: Разрешаем класть в 0 слот только то, что горит в печке!
    @Override
    public boolean canInsertItem(int slot, ItemStack stack) {
        if (slot == 0) return stack.getBurnTime(RecipeType.SMELTING) > 0;
        if (slot == 1) return stack.has(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get());
        return false;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new com.rizor1x.industry.menu.GeneratorMenu(containerId, playerInventory, this, this.data);
    }

    // Добавь это в любое место внутри SolidFuelGeneratorBlockEntity
    @Override
    public int getOutputSlot() {
        return -1; // -1 означает, что слота выхода у машины нет!
    }
}