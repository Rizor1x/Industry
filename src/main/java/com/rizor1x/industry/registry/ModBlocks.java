package com.rizor1x.industry.registry;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.block.custom.BaseMachineBlock;
import com.rizor1x.industry.block.custom.CableBlock;
import com.rizor1x.industry.block.entity.CrusherBlockEntity;
import com.rizor1x.industry.block.entity.SolidFuelGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    // Реестр блоков
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Industry.MODID);

    // ==========================================
    // БЛОКИ МАШИН
    // ==========================================

    public static final DeferredBlock<Block> CRUSHER = registerBlock("crusher",
            () -> new BaseMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops()) {
                @Override
                public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
                    return new CrusherBlockEntity(pos, state);
                }
            });

    public static final DeferredBlock<Block> COAL_GENERATOR = registerBlock("coal_generator",
            () -> new BaseMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops()) {
                @Override
                public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
                    return new SolidFuelGeneratorBlockEntity(pos, state);
                }
            });

    public static final DeferredBlock<Block> ELECTRIC_FURNACE = registerBlock("electric_furnace",
            () -> new BaseMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops()) {
                @Override
                public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
                    return new com.rizor1x.industry.block.entity.ElectricFurnaceBlockEntity(pos, state);
                }
            });

    // Медный провод (Оголенный) - Бьет током!
    public static final DeferredBlock<Block> UNINSULATED_COPPER_CABLE = registerBlock("uninsulated_copper_cable",
            () -> new com.rizor1x.industry.block.custom.CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(),
                    com.rizor1x.industry.block.custom.CableTier.UNINSULATED_COPPER));

    // Медный провод (Изолированный) - Безопасный
    public static final DeferredBlock<Block> COPPER_CABLE = registerBlock("copper_cable",
            () -> new com.rizor1x.industry.block.custom.CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(),
                    com.rizor1x.industry.block.custom.CableTier.COPPER));

    // ==========================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ==========================================

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.registerSimpleBlockItem(name, block);
    }
}