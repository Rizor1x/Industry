package com.rizor1x.industry.menu;

import com.rizor1x.industry.block.entity.BatteryBoxBlockEntity;
import com.rizor1x.industry.item.custom.BatteryItem;
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

public class BatteryBoxMenu extends BaseMachineMenu {

    public BatteryBoxMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(10));
    }

    public BatteryBoxMenu(int containerId, Inventory playerInv, BlockEntity entity, ContainerData data) {
        super(ModMenus.BATTERY_BOX_MENU.get(), containerId, (BatteryBoxBlockEntity) entity, data);

        // Слоты Батбокса (0 - Верх, 1 - Низ)
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 80, 17)); // Верхний: Зарядить батарейку ОТ хранилища
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 1, 80, 53)); // Нижний: Разрядить батарейку В хранилище

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

    @Override
    public boolean stillValid(@NotNull Player player) {
        assert blockEntity.getLevel() != null;
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, ModBlocks.BATTERY_BOX.get());
    }

    // Идеальный Shift-клик для Батбокса!
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Если кликаем по слотам машины (0 или 1) -> выкидываем в инвентарь игрока
        if (index < 2) {
            if (!this.moveItemStackTo(sourceStack, 2, this.slots.size(), true)) return ItemStack.EMPTY;
        } else {
            // Если кликаем в инвентаре игрока: проверяем, Батарейка ли это?
            if (sourceStack.getItem() instanceof BatteryItem) {
                // Пытаемся положить сначала в нижний слот (1), потом в верхний (0)
                if (!this.moveItemStackTo(sourceStack, 1, 2, false)) {
                    if (!this.moveItemStackTo(sourceStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                return ItemStack.EMPTY; // Если это не батарейка - просто ничего не делаем
            }
        }

        if (sourceStack.getCount() == 0) sourceSlot.set(ItemStack.EMPTY);
        else sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }
}