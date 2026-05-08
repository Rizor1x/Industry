package com.rizor1x.industry.block.entity;

import com.rizor1x.industry.block.custom.SolarPanelBlock;
import com.rizor1x.industry.block.custom.SolarTier;
import com.rizor1x.industry.item.custom.IEnergyItem;
import com.rizor1x.industry.registry.ModBlockEntities;
import com.rizor1x.industry.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;

public class SolarPanelBlockEntity extends BaseMachineBlockEntity {
    private SolarTier tier;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_PANEL_BE.get(), pos, state, 10000, 0, 1); // Всего 1 слот для зарядки
        if (state.getBlock() instanceof SolarPanelBlock solarBlock) {
            this.tier = solarBlock.getTier();
            this.maxEnergy = tier.capacity;
        }
    }

    @Override public boolean canReceiveEnergy() { return false; }
    @Override public boolean canExtractEnergy() { return true; }
    @Override public int getInputSlot() { return -1; }  // Панель не принимает предметы
    @Override public int getOutputSlot() { return -1; }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;
        boolean isDirty = false;

        // Переменная для хранения того, сколько мы сгенерировали в этот тик
        int currentGen = 0;

        // 1. ГЕНЕРАЦИЯ ОТ СОЛНЦА
        if (this.energy < this.maxEnergy) {
            // Проверяем, видит ли панель небо (блок прямо над ней)
            if (level.canSeeSky(worldPosition.above())) {
                if (level.isDay()) {
                    currentGen = (level.isRaining() || level.isThundering()) ? tier.rainGen : tier.dayGen;
                } else {
                    currentGen = tier.nightGen; // Ночь
                }

                if (currentGen > 0) {
                    this.energy = Math.min(this.energy + currentGen, this.maxEnergy);
                    isDirty = true;
                }
            }
        }

        // Сохраняем генерацию в progress, чтобы GUI (клиент) её увидел!
        if (this.progress != currentGen) {
            this.progress = currentGen;
            isDirty = true;
        }

        // 2. ЗАРЯДКА ПРЕДМЕТА В СЛОТЕ 0 (Батарейка/Бур)
        ItemStack chargeSlot = inventory.getStackInSlot(0);
        if (chargeSlot.has(ModDataComponents.ENERGY.get()) && chargeSlot.getItem() instanceof IEnergyItem energyItem) {
            int currentBatEnergy = chargeSlot.getOrDefault(ModDataComponents.ENERGY.get(), 0);
            int batCap = energyItem.getCapacity();

            if (this.energy > 0 && currentBatEnergy < batCap) {
                int transfer = Math.min(this.energy, Math.min(tier.maxTransfer, batCap - currentBatEnergy));
                if (transfer > 0) {
                    this.energy -= transfer;
                    chargeSlot.set(ModDataComponents.ENERGY.get(), currentBatEnergy + transfer);
                    isDirty = true;
                }
            }
        }

        // 3. ОТПРАВКА ЭНЕРГИИ В ПРОВОДА (Во все стороны)
        if (this.energy > 0) {
            for (Direction dir : Direction.values()) {
                BlockPos targetPos = getBlockPos().relative(dir);
                var storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, dir.getOpposite());

                if (storage != null && storage.canReceive()) {
                    int sent = storage.receiveEnergy(Math.min(this.energy, tier.maxTransfer), false);
                    if (sent > 0) {
                        this.energy -= sent;
                        isDirty = true;
                        if (this.energy <= 0) break;
                    }
                }
            }
        }

        if (isDirty) setChanged();
    }

    // Разрешаем класть только энерго-предметы
    @Override
    public boolean canInsertItem(int slot, @org.jetbrains.annotations.NotNull ItemStack stack) {
        return stack.has(ModDataComponents.ENERGY.get());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new com.rizor1x.industry.menu.SolarPanelMenu(containerId, playerInventory, this, this.data);
    }
}