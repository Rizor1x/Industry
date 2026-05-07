package com.rizor1x.industry.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public abstract class BaseMachineBlockEntity extends BlockEntity implements MenuProvider {

    public int energy = 0;
    public int maxEnergy;
    public int progress = 0;
    public int maxProgress;

    public int[] sideConfig = new int[6];

    public final ItemStackHandler inventory;
    public final ContainerData data;

    public BaseMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxEnergy, int maxProgress, int inventorySize) {
        super(type, pos, state);
        this.maxEnergy = maxEnergy;
        this.maxProgress = maxProgress;

        this.inventory = new ItemStackHandler(inventorySize) {
            @Override
            protected void onContentsChanged(int slot) { setChanged(); }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return canInsertItem(slot, stack);
            }
        };

        this.data = new ContainerData() {
            @Override public int get(int index) {
                return switch (index) {
                    case 0 -> BaseMachineBlockEntity.this.energy;
                    case 1 -> BaseMachineBlockEntity.this.maxEnergy;
                    case 2 -> BaseMachineBlockEntity.this.progress;
                    case 3 -> BaseMachineBlockEntity.this.maxProgress;
                    case 4, 5, 6, 7, 8, 9 -> BaseMachineBlockEntity.this.sideConfig[index - 4];
                    default -> 0;
                };
            }
            @Override public void set(int index, int value) {
                switch (index) {
                    case 0 -> BaseMachineBlockEntity.this.energy = value;
                    case 1 -> BaseMachineBlockEntity.this.maxEnergy = value;
                    case 2 -> BaseMachineBlockEntity.this.progress = value;
                    case 3 -> BaseMachineBlockEntity.this.maxProgress = value;
                    case 4, 5, 6, 7, 8, 9 -> BaseMachineBlockEntity.this.sideConfig[index - 4] = value;
                }
            }
            @Override public int getCount() { return 10; }
        };
    }

    // ==========================================
    // ГИБКАЯ НАСТРОЙКА СЛОТОВ ДЛЯ API
    // ==========================================
    // По умолчанию считаем, что Вход = 0, Выход = 2 (Как у Дробителя и Печки)
    public int getInputSlot() { return 0; }
    public int getOutputSlot() { return 2; }

    public boolean canInsertItem(int slot, ItemStack stack) { return true; }

    // ==========================================
    // АВТО-I/O (Защищенный от крашей)
    // ==========================================
    public void autoIo() {
        if (level == null || level.isClientSide) return;

        int inSlot = getInputSlot();
        int outSlot = getOutputSlot();

        for (Direction side : Direction.values()) {
            int relativeSide = getRelativeDirectionIndex(side);
            int config = sideConfig[relativeSide];

            if (config == 0) continue;

            BlockPos adjacentPos = getBlockPos().relative(side);
            IItemHandler adjacentInv = level.getCapability(Capabilities.ItemHandler.BLOCK, adjacentPos, side.getOpposite());

            if (adjacentInv != null) {
                // Если режим ВЫХОД и у машины ВООБЩЕ ЕСТЬ слот выхода (не равен -1)
                if (config == 2 && outSlot != -1) {
                    ItemStack outputStack = inventory.getStackInSlot(outSlot);
                    if (!outputStack.isEmpty()) {
                        for (int i = 0; i < adjacentInv.getSlots(); i++) {
                            ItemStack remaining = adjacentInv.insertItem(i, outputStack, false);
                            inventory.setStackInSlot(outSlot, remaining);
                            if (remaining.isEmpty()) break;
                        }
                    }
                }
                // Если режим ВХОД и у машины есть слот входа
                else if (config == 1 && inSlot != -1) {
                    if (inventory.getStackInSlot(inSlot).getCount() < inventory.getSlotLimit(inSlot)) {
                        for (int i = 0; i < adjacentInv.getSlots(); i++) {
                            ItemStack simulated = adjacentInv.extractItem(i, 1, true);
                            if (!simulated.isEmpty() && canInsertItem(inSlot, simulated) && inventory.insertItem(inSlot, simulated, true).isEmpty()) {
                                ItemStack extracted = adjacentInv.extractItem(i, 1, false);
                                inventory.insertItem(inSlot, extracted, false);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public IItemHandler getItemHandler(Direction side) {
        if (side == null) return inventory;
        int relativeSide = getRelativeDirectionIndex(side);
        int config = sideConfig[relativeSide];

        int inSlot = getInputSlot();
        int outSlot = getOutputSlot();

        return new IItemHandler() {
            @Override public int getSlots() { return inventory.getSlots(); }
            @Override public @NotNull ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(slot); }
            @Override public int getSlotLimit(int slot) { return inventory.getSlotLimit(slot); }
            @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) { return inventory.isItemValid(slot, stack); }

            @Override public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (config != 1 || slot == outSlot) return stack;
                if (!canInsertItem(slot, stack)) return stack;
                return inventory.insertItem(slot, stack, simulate);
            }
            @Override public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (config != 2 || slot != outSlot) return ItemStack.EMPTY;
                return inventory.extractItem(slot, amount, simulate);
            }
        };
    }

    private int getRelativeDirectionIndex(Direction absoluteSide) {
        if (absoluteSide == Direction.UP) return 1;
        if (absoluteSide == Direction.DOWN) return 0;

        Direction facing = getBlockState().getValue(com.rizor1x.industry.block.custom.BaseMachineBlock.FACING);
        if (absoluteSide == facing) return 2;
        if (absoluteSide == facing.getOpposite()) return 3;
        if (absoluteSide == facing.getClockWise()) return 4;
        if (absoluteSide == facing.getCounterClockWise()) return 5;
        return 0;
    }

    // ==========================================
    // СИСТЕМА ЭНЕРГИИ
    // ==========================================
    public boolean canReceiveEnergy() { return true; }
    public boolean canExtractEnergy() { return false; }

    public final net.neoforged.neoforge.energy.IEnergyStorage energyStorage = new net.neoforged.neoforge.energy.IEnergyStorage() {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!canReceiveEnergy()) return 0;
            int energyReceived = Math.min(maxEnergy - energy, maxReceive);
            if (!simulate) { energy += energyReceived; setChanged(); }
            return energyReceived;
        }
        @Override public int extractEnergy(int maxExtract, boolean simulate) {
            if (!canExtractEnergy()) return 0;
            int energyExtracted = Math.min(energy, maxExtract);
            if (!simulate) { energy -= energyExtracted; setChanged(); }
            return energyExtracted;
        }
        @Override public int getEnergyStored() { return energy; }
        @Override public int getMaxEnergyStored() { return maxEnergy; }
        @Override public boolean canExtract() { return canExtractEnergy(); }
        @Override public boolean canReceive() { return canReceiveEnergy(); }
    };

    public net.neoforged.neoforge.energy.IEnergyStorage getEnergyStorage(Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Energy", energy);
        tag.putInt("Progress", progress);
        tag.putIntArray("SideConfig", sideConfig);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        energy = tag.getInt("Energy");
        progress = tag.getInt("Progress");
        if (tag.contains("SideConfig")) sideConfig = tag.getIntArray("SideConfig");
    }

    public abstract void tick();

    // ==========================================
    // ЛОГИКА БАТАРЕЕК
    // ==========================================
    public void processBatterySlot() {
        ItemStack batteryStack = inventory.getStackInSlot(1);

        // Если предмет пустой или у него нет энергии - отменяем
        if (batteryStack.isEmpty() || !batteryStack.has(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get())) return;
        // Если это не энерго-предмет (не реализует наш интерфейс) - отменяем
        if (!(batteryStack.getItem() instanceof com.rizor1x.industry.item.custom.IEnergyItem energyItem)) return;

        // Теперь мы 100% уверены, что это правильный предмет!
        int currentBatteryEnergy = batteryStack.get(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get());
        int batteryCapacity = energyItem.getCapacity();

        // Если это Генератор -> ЗАРЯЖАЕМ батарейку/бур
        if (this.canExtractEnergy() && this.energy > 0 && currentBatteryEnergy < batteryCapacity) {
            int amountToCharge = Math.min(this.energy, Math.min(100, batteryCapacity - currentBatteryEnergy));
            if (amountToCharge > 0) {
                this.energy -= amountToCharge;
                batteryStack.set(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get(), currentBatteryEnergy + amountToCharge);
                setChanged();
            }
        }

        // Если это Дробитель/Печь -> ЗАБИРАЕМ энергию из батарейки/бура
        if (this.canReceiveEnergy() && this.energy < this.maxEnergy && currentBatteryEnergy > 0) {
            int amountToExtract = Math.min(this.maxEnergy - this.energy, Math.min(100, currentBatteryEnergy));
            if (amountToExtract > 0) {
                this.energy += amountToExtract;
                batteryStack.set(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get(), currentBatteryEnergy - amountToExtract);
                setChanged();
            }
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.industry." + getBlockState().getBlock().getDescriptionId().replace("block.industry.", ""));
    }
}