package com.rizor1x.industry.block.entity;

import com.rizor1x.industry.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CrusherBlockEntity extends BaseMachineBlockEntity {

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        // Указываем: 10000 макс энергии, 200 тиков на переработку, 3 слота (вход, батарея, выход)
        super(ModBlockEntities.CRUSHER_BE.get(), pos, state, 10000, 200, 3);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) return;
        boolean isDirty = false;

        // 1. АВТО-I/O И БАТАРЕЯ
        autoIo(); // Засасываем предметы
        processBatterySlot(); // Забираем энергию из батарейки в 1 слоте!

        // 2. ЛОГИКА ПЕРЕРАБОТКИ (JSON)
        ItemStack inputSlot = inventory.getStackInSlot(0);
        ItemStack outputSlot = inventory.getStackInSlot(2);

        net.minecraft.world.item.crafting.SingleRecipeInput recipeInput = new net.minecraft.world.item.crafting.SingleRecipeInput(inputSlot);
        var recipe = level.getRecipeManager().getRecipeFor(com.rizor1x.industry.registry.ModRecipes.CRUSHER_TYPE.get(), recipeInput, level);

        boolean hasRecipe = recipe.isPresent();
        ItemStack outputResult = hasRecipe ? recipe.get().value().output() : ItemStack.EMPTY;

        boolean canOutput = outputSlot.isEmpty() ||
                (ItemStack.isSameItemSameComponents(outputSlot, outputResult) &&
                        outputSlot.getCount() + outputResult.getCount() <= outputSlot.getMaxStackSize());

        // Тратим 20 энергии за тик
        if (hasRecipe && canOutput && this.energy >= 20) {
            this.progress++;
            this.energy -= 20;
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

    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new com.rizor1x.industry.menu.CrusherMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack) {
        if (slot == 1) return stack.has(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get());
        if (slot == 0) return !(stack.getItem() instanceof com.rizor1x.industry.item.custom.BatteryItem);
        if (slot == 2) return false;
        return super.canInsertItem(slot, stack);
    }
}