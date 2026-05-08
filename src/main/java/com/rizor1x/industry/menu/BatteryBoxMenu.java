package com.rizor1x.industry.menu;

import com.rizor1x.industry.block.entity.BatteryBoxBlockEntity;
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

        if (index < 3) {
            // Если кликаем в слотах машины -> переносим в инвентарь игрока
            if (!this.moveItemStackTo(sourceStack, 3, this.slots.size(), true)) return ItemStack.EMPTY;
        } else {
            // Если у предмета ЕСТЬ ЭНЕРГИЯ (Батарея, Бур, Ранец) -> кладем в слот батарейки (1)
            if (sourceStack.has(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get())) {
                if (!this.moveItemStackTo(sourceStack, 1, 2, false)) return ItemStack.EMPTY;
            } else {
                // Иначе кладем во Вход (0)
                if (!this.moveItemStackTo(sourceStack, 0, 1, false)) return ItemStack.EMPTY;
            }
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }
}