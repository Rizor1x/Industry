package com.rizor1x.industry.block.entity;

import com.rizor1x.industry.block.custom.CableTier;
import com.rizor1x.industry.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CableBlockEntity extends BlockEntity {

    public int energy = 0;
    // 32 FE/t — это классика IC2 (Низкое напряжение / Медный провод)
    public int capacity;
    public int maxTransfer;
    public boolean isInsulated; // Изолирован ли?

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE_BE.get(), pos, state);

        if (state.getBlock() instanceof com.rizor1x.industry.block.custom.CableBlock cable) {
            var tier = cable.getTier();

            this.capacity = tier.capacity;
            this.maxTransfer = tier.maxTransfer;
            this.isInsulated = tier.insulated;
        }
    }

    public final IEnergyStorage energyStorage = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int energyReceived = Math.min(capacity - energy, Math.min(maxTransfer, maxReceive));
            if (!simulate) { energy += energyReceived; setChanged(); }
            return energyReceived;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int energyExtracted = Math.min(energy, Math.min(maxTransfer, maxExtract));
            if (!simulate) { energy -= energyExtracted; setChanged(); }
            return energyExtracted;
        }

        @Override public int getEnergyStored() { return energy; }
        @Override public int getMaxEnergyStored() { return capacity; }
        @Override public boolean canExtract() { return true; }
        @Override public boolean canReceive() { return true; }
    };

    public void tick() {
        if (level == null || level.isClientSide) return;

        // Если кабель ОГОЛЕННЫЙ и по нему течет энергия -> бьем током!
        if (!isInsulated && this.energy > 10) {
            // Ищем всех живых существ прямо в блоке кабеля
            var entities = level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, new net.minecraft.world.phys.AABB(getBlockPos()));
            for (var entity : entities) {
                // Бьем молнией на 1 сердечко (2.0f)
                entity.hurt(level.damageSources().lightningBolt(), 2.0f);
            }
        }

        // Отдаем энергию соседям
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = getBlockPos().relative(dir);
            BlockEntity neighborBe = level.getBlockEntity(neighborPos);
            var storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());

            if (storage != null && storage.canReceive()) {
                // Если сосед - это такой же провод:
                if (neighborBe instanceof CableBlockEntity otherCable) {
                    // Энергия течет как вода: оттуда, где больше, туда, где меньше!
                    if (this.energy > otherCable.energy) {
                        int diff = this.energy - otherCable.energy;
                        int transfer = Math.min(diff / 2, maxTransfer); // Передаем половину разницы
                        if (transfer > 0) {
                            int sent = otherCable.energyStorage.receiveEnergy(transfer, false);
                            this.energy -= sent;
                            setChanged();
                        }
                    }
                }
                // Если сосед - это Машина (Дробитель, Печь):
                else {
                    // Машинам отдаем всё, что можем
                    int sent = storage.receiveEnergy(Math.min(this.energy, maxTransfer), false);
                    if (sent > 0) {
                        this.energy -= sent;
                        setChanged();
                    }
                }
            }
            if (this.energy <= 0) break;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energy);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy = tag.getInt("Energy");
    }
}