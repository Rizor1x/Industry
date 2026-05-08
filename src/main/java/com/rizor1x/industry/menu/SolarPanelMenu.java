package com.rizor1x.industry.menu;

import com.rizor1x.industry.block.entity.SolarPanelBlockEntity;
import com.rizor1x.industry.registry.ModBlocks;
import com.rizor1x.industry.registry.ModDataComponents;
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

public class SolarPanelMenu extends BaseMachineMenu {

    public SolarPanelMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(10));
    }

    public SolarPanelMenu(int containerId, Inventory playerInv, BlockEntity entity, ContainerData data) {
        super(ModMenus.SOLAR_PANEL_MENU.get(), containerId, (SolarPanelBlockEntity) entity, data);

        // Слот 0: Слот для зарядки батареек (Снизу под молнией)
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 80, 53));

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
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, ModBlocks.BASIC_SOLAR_PANEL.get());
    }

    // Идеальный Shift-клик (разрешает класть только предметы с Энергией)
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < 1) { // Если кликаем по слоту панели -> в инвентарь игрока
            if (!this.moveItemStackTo(sourceStack, 1, this.slots.size(), true)) return ItemStack.EMPTY;
        } else { // Если кликаем в инвентаре игрока -> в панель
            if (sourceStack.has(ModDataComponents.ENERGY.get())) {
                if (!this.moveItemStackTo(sourceStack, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY; // Не-энерго предметы не пускаем!
            }
        }

        if (sourceStack.getCount() == 0) sourceSlot.set(ItemStack.EMPTY);
        else sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    public int getProgress() {
        return data.get(2);
    }
}