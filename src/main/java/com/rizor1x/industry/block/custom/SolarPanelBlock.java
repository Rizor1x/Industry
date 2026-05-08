package com.rizor1x.industry.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelBlock extends BaseMachineBlock {
    private final SolarTier tier;

    public SolarPanelBlock(Properties properties, SolarTier tier) {
        super(properties);
        this.tier = tier;
    }

    public SolarTier getTier() { return tier; }

    @javax.annotation.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new com.rizor1x.industry.block.entity.SolarPanelBlockEntity(pos, state);
    }
}