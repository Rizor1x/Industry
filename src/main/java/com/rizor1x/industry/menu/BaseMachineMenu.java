package com.rizor1x.industry.menu;

import com.rizor1x.industry.block.entity.BaseMachineBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public abstract class BaseMachineMenu extends AbstractContainerMenu {
    public final BaseMachineBlockEntity blockEntity;
    public final ContainerData data;

    protected BaseMachineMenu(MenuType<?> menuType, int containerId, BaseMachineBlockEntity blockEntity, ContainerData data) {
        super(menuType, containerId);
        this.blockEntity = blockEntity;
        this.data = data;
        addDataSlots(data);
    }

    public int getSideConfig(int sideIndex) {
        return data.get(4 + sideIndex);
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int buttonId) {
        if (buttonId >= 0 && buttonId <= 5) {
            int currentMode = blockEntity.sideConfig[buttonId];
            blockEntity.sideConfig[buttonId] = (currentMode + 1) % 3;
            blockEntity.setChanged();
            return true;
        }
        return false;
    }
}