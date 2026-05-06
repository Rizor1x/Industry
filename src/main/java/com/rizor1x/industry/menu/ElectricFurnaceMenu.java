package com.rizor1x.industry.menu;

import com.rizor1x.industry.block.entity.ElectricFurnaceBlockEntity;
import com.rizor1x.industry.registry.ModBlocks;
import com.rizor1x.industry.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ElectricFurnaceMenu extends BaseMachineMenu {

    public ElectricFurnaceMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(10));
    }

    public ElectricFurnaceMenu(int containerId, Inventory playerInv, BlockEntity entity, ContainerData data) {
        super(ModMenus.ELECTRIC_FURNACE_MENU.get(), containerId, (ElectricFurnaceBlockEntity) entity, data);

        // Слоты печки (Стандартные координаты ванильной печи)
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 56, 17)); // Вход
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 1, 56, 53)); // Топливо (пока не используется, но слот есть)
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 2, 116, 35)); // Выход

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
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, ModBlocks.ELECTRIC_FURNACE.get());
    }

    // Shift-клик для электропечи
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // 3 слота в печке
        if (index < 3) {
            if (!this.moveItemStackTo(sourceStack, 3, this.slots.size(), true)) return ItemStack.EMPTY;
        } else {
            // Если предмет плавится -> в слот 0
            if (this.blockEntity.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(sourceStack), this.blockEntity.getLevel()).isPresent()) {
                if (!this.moveItemStackTo(sourceStack, 0, 1, false)) return ItemStack.EMPTY;
            }
            // В противном случае пытаемся положить в слот 1 (батарея), пока не используется
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