package com.rizor1x.industry.registry;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.block.custom.BaseMachineBlock;
import com.rizor1x.industry.block.custom.CableBlock;
import com.rizor1x.industry.block.custom.CableTier;
import com.rizor1x.industry.block.entity.BatteryBoxBlockEntity;
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
import org.jetbrains.annotations.NotNull;

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
                public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
                    return new CrusherBlockEntity(pos, state);
                }
            });

    public static final DeferredBlock<Block> COAL_GENERATOR = registerBlock("coal_generator",
            () -> new BaseMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops()) {
                @Override
                public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
                    return new SolidFuelGeneratorBlockEntity(pos, state);
                }
            });

    public static final DeferredBlock<Block> BATTERY_BOX = registerBlock("battery_box",
            () -> new BaseMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops()) {
                @Override
                public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
                    return new BatteryBoxBlockEntity(pos, state);
                }
            });

    public static final DeferredBlock<Block> ELECTRIC_FURNACE = registerBlock("electric_furnace",
            () -> new BaseMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops()) {
                @Override
                public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
                    return new com.rizor1x.industry.block.entity.ElectricFurnaceBlockEntity(pos, state);
                }
            });

    // ОЛОВО
    public static final DeferredBlock<Block> UNINSULATED_TIN_CABLE = registerBlock("uninsulated_tin_cable", () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(), CableTier.UNINSULATED_TIN));
    public static final DeferredBlock<Block> TIN_CABLE = registerBlock("tin_cable", () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(), CableTier.TIN));

    // МЕДЬ
    public static final DeferredBlock<Block> UNINSULATED_COPPER_CABLE = registerBlock("uninsulated_copper_cable", () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(), CableTier.UNINSULATED_COPPER));
    public static final DeferredBlock<Block> COPPER_CABLE = registerBlock("copper_cable", () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(), CableTier.COPPER));

    // ЗОЛОТО
    public static final DeferredBlock<Block> UNINSULATED_GOLD_CABLE = registerBlock("uninsulated_gold_cable", () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(), CableTier.UNINSULATED_GOLD));
    public static final DeferredBlock<Block> GOLD_CABLE = registerBlock("gold_cable", () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(), CableTier.GOLD));

    // ЖЕЛЕЗО (HV)
    public static final DeferredBlock<Block> UNINSULATED_HV_CABLE = registerBlock("uninsulated_hv_cable", () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(), CableTier.UNINSULATED_HV));
    public static final DeferredBlock<Block> HV_CABLE = registerBlock("hv_cable", () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(), CableTier.HV));

    // СТЕКЛОВОЛОКНО
    public static final DeferredBlock<Block> GLASS_FIBER_CABLE = registerBlock("glass_fiber_cable", () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion(), CableTier.GLASS_FIBER));

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