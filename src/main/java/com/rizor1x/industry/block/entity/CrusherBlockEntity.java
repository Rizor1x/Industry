package com.rizor1x.industry.block.entity;

import com.rizor1x.industry.registry.ModBlockEntities;
import com.rizor1x.industry.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public class CrusherBlockEntity extends BaseMachineBlockEntity {

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        // Указываем: 10000 макс энергии, 200 тиков на переработку, 3 слота (вход, батарея, выход)
        super(ModBlockEntities.CRUSHER_BE.get(), pos, state, 10000, 200, 3);
    }

    @Override
    public void tick() {
        processBatterySlot();
        this.autoIo();
        boolean isDirty = false; // Флаг, нужно ли сохранить блок

        // ==========================================
        // 1. ЛОГИКА ЗАРЯДКИ (Нижний слот - 1)
        // ==========================================
        ItemStack batterySlot = inventory.getStackInSlot(1);
        // Если лежит редстоун и есть место для 1000 энергии:
        if (batterySlot.getItem() == Items.REDSTONE && this.energy + 1000 <= this.maxEnergy) {
            this.energy += 1000;
            batterySlot.shrink(1); // Тратим 1 шт. редстоуна
            isDirty = true;
        }

        // ==========================================
        // 2. ЛОГИКА ПЕРЕРАБОТКИ (Ищем рецепт в JSON)
        // ==========================================
        ItemStack inputSlot = inventory.getStackInSlot(0);
        ItemStack outputSlot = inventory.getStackInSlot(2);

        // Формируем "запрос" к игре
        net.minecraft.world.item.crafting.SingleRecipeInput recipeInput = new net.minecraft.world.item.crafting.SingleRecipeInput(inputSlot);

        // Ищем рецепт типа industry:crushing
        var recipe = level.getRecipeManager().getRecipeFor(com.rizor1x.industry.registry.ModRecipes.CRUSHER_TYPE.get(), recipeInput, level);

        boolean hasRecipe = recipe.isPresent();
        // Если рецепт найден, узнаем, что должно получиться на выходе
        ItemStack outputResult = hasRecipe ? recipe.get().value().output() : ItemStack.EMPTY;

        // Проверяем: Выходной слот пустой? ИЛИ там лежит ТА ЖЕ пыль, и места хватит?
        boolean canOutput = outputSlot.isEmpty() ||
                (ItemStack.isSameItemSameComponents(outputSlot, outputResult) &&
                        outputSlot.getCount() + outputResult.getCount() <= outputSlot.getMaxStackSize());

        if (hasRecipe && canOutput && this.energy >= 20) {
            this.progress++;
            this.energy -= 20;
            isDirty = true;

            if (this.progress >= this.maxProgress) {
                inputSlot.shrink(1); // Забираем 1 предмет из входа

                if (outputSlot.isEmpty()) {
                    inventory.setStackInSlot(2, outputResult.copy()); // Кладем новую пыль
                } else {
                    outputSlot.grow(outputResult.getCount()); // Добавляем пыль к существующей
                }
                this.progress = 0;
            }
        } else {
            if (this.progress > 0) {
                this.progress = 0;
                isDirty = true;
            }
        }

        // Если что-то изменилось (потратилась энергия/предмет), говорим игре сохраниться
        if (isDirty) {
            setChanged();
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new com.rizor1x.industry.menu.CrusherMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack) {
        if (slot == 1) return stack.getItem() == net.minecraft.world.item.Items.REDSTONE; // В батарейку - только редстоун!
        if (slot == 0) return stack.getItem() != net.minecraft.world.item.Items.REDSTONE; // Во вход - что угодно, КРОМЕ редстоуна!
        if (slot == 2) return false; // В выход вообще ничего нельзя класть!
        return super.canInsertItem(slot, stack);
    }
}