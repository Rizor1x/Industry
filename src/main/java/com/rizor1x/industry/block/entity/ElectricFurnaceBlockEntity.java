package com.rizor1x.industry.block.entity;

import com.rizor1x.industry.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ElectricFurnaceBlockEntity extends BaseMachineBlockEntity {

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        // 10000 энергии, 100 тиков на плавку (быстрее обычной), 3 слота (вход, выход, батарея)
        super(ModBlockEntities.ELECTRIC_FURNACE_BE.get(), pos, state, 10000, 100, 3);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) return;
        this.autoIo();
        processBatterySlot();
        boolean isDirty = false;

        autoIo(); // Автоматическое засасывание/выталкивание

        // ЛОГИКА ПЛАВКИ
        ItemStack inputSlot = inventory.getStackInSlot(0);
        ItemStack outputSlot = inventory.getStackInSlot(2);

        // Ищем ванильный рецепт плавки (используем ванильную печь)
        SingleRecipeInput recipeInput = new SingleRecipeInput(inputSlot);
        Optional<RecipeHolder<net.minecraft.world.item.crafting.SmeltingRecipe>> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, recipeInput, level);

        // Проверяем, есть ли рецепт и куда выкладывать
        boolean hasRecipe = recipe.isPresent();
        ItemStack outputResult = hasRecipe ? recipe.get().value().getResultItem(level.registryAccess()) : ItemStack.EMPTY;

        boolean canOutput = outputSlot.isEmpty() ||
                (ItemStack.isSameItemSameComponents(outputSlot, outputResult) &&
                        outputSlot.getCount() + outputResult.getCount() <= outputSlot.getMaxStackSize());

        // Если есть рецепт, есть место для вывода и есть энергия
        if (hasRecipe && canOutput && this.energy >= 10) { // 10 FE за тик
            this.progress++;
            this.energy -= 10;
            isDirty = true;

            if (this.progress >= this.maxProgress) {
                inputSlot.shrink(1);
                if (outputSlot.isEmpty()) {
                    inventory.setStackInSlot(2, outputResult.copy());
                } else {
                    outputSlot.grow(outputResult.getCount());
                }
                this.progress = 0;
            }
        } else {
            if (this.progress > 0) {
                this.progress = 0;
                isDirty = true;
            }
        }

        if (isDirty) setChanged();
    }

    // Фильтр слотов: только вход (0), выход (2) ничего не принимает
    @Override
    public boolean canInsertItem(int slot, ItemStack stack) {
        if (slot == 0) return true; // Во вход можно положить всё, что плавится
        if (slot == 1) return stack.has(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get());
        if (slot == 2) return false; // В слот выхода ничего класть нельзя
        return super.canInsertItem(slot, stack);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new com.rizor1x.industry.menu.ElectricFurnaceMenu(containerId, playerInventory, this, this.data);
    }
}