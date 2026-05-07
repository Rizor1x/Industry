package com.rizor1x.industry.menu;

import com.rizor1x.industry.block.entity.SolidFuelGeneratorBlockEntity;
import com.rizor1x.industry.registry.ModBlocks;
import com.rizor1x.industry.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class GeneratorMenu extends BaseMachineMenu {

    public GeneratorMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(10));
    }

    public GeneratorMenu(int containerId, Inventory playerInv, BlockEntity entity, ContainerData data) {
        super(ModMenus.GENERATOR_MENU.get(), containerId, (SolidFuelGeneratorBlockEntity) entity, data);

        // Слоты генератора (Подстроил под твою картинку)
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 80, 53)); // Слот 0: Топливо (Уголь) - Внизу
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 1, 80, 17)); // Слот 1: Батарея (Для зарядки) - Вверху

        // Инвентарь игрока
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInv, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }

    public int getEnergy() { return data.get(0); }
    public int getMaxEnergy() { return data.get(1); }
    public int getProgress() { return data.get(2); }
    public int getMaxProgress() { return data.get(3); }

    @Override
    public boolean stillValid(@NotNull Player player) {
        assert blockEntity.getLevel() != null;
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, ModBlocks.COAL_GENERATOR.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // 2 слота в генераторе
        if (index < 2) {
            if (!this.moveItemStackTo(sourceStack, 2, this.slots.size(), true)) return ItemStack.EMPTY;
        } else {
            // Если это уголь или дерево (то, что горит) -> пихаем в слот топлива (0)
            if (sourceStack.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING) > 0) {
                if (!this.moveItemStackTo(sourceStack, 0, 1, false)) return ItemStack.EMPTY;
            }
            // Иначе (если это батарейка) -> пихаем в верхний слот (1)
            else {
                if (!this.moveItemStackTo(sourceStack, 1, 2, false)) return ItemStack.EMPTY;
            }
        }

        if (sourceStack.getCount() == 0) sourceSlot.set(ItemStack.EMPTY);
        else sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }
}