package com.rizor1x.industry.registry;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.block.entity.CrusherBlockEntity;
import com.rizor1x.industry.block.entity.SolidFuelGeneratorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

@SuppressWarnings("DataFlowIssue")
public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Industry.MODID);

    // Регистрируем мозг Дробителя и привязываем его к блоку Дробителя
    public static final Supplier<BlockEntityType<CrusherBlockEntity>> CRUSHER_BE =
            BLOCK_ENTITIES.register("crusher_be", () ->
                    BlockEntityType.Builder.of(CrusherBlockEntity::new, ModBlocks.CRUSHER.get()).build(null));

    public static final Supplier<BlockEntityType<SolidFuelGeneratorBlockEntity>> COAL_GENERATOR_BE =
            BLOCK_ENTITIES.register("coal_generator_be", () ->
                    BlockEntityType.Builder.of(SolidFuelGeneratorBlockEntity::new, ModBlocks.COAL_GENERATOR.get()).build(null));

    public static final Supplier<BlockEntityType<com.rizor1x.industry.block.entity.BatteryBoxBlockEntity>> BATTERY_BOX_BE =
            BLOCK_ENTITIES.register("battery_box_be", () ->
                    BlockEntityType.Builder.of(com.rizor1x.industry.block.entity.BatteryBoxBlockEntity::new, ModBlocks.BATTERY_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<com.rizor1x.industry.block.entity.ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE_BE =
            BLOCK_ENTITIES.register("electric_furnace_be", () ->
                    BlockEntityType.Builder.of(com.rizor1x.industry.block.entity.ElectricFurnaceBlockEntity::new, ModBlocks.ELECTRIC_FURNACE.get()).build(null));

    public static final Supplier<BlockEntityType<com.rizor1x.industry.block.entity.SolarPanelBlockEntity>> SOLAR_PANEL_BE =
            BLOCK_ENTITIES.register("solar_panel_be", () ->
                    BlockEntityType.Builder.of(com.rizor1x.industry.block.entity.SolarPanelBlockEntity::new, ModBlocks.BASIC_SOLAR_PANEL.get()).build((com.mojang.datafixers.types.Type<?>) null));

    public static final Supplier<BlockEntityType<com.rizor1x.industry.block.entity.CableBlockEntity>> CABLE_BE =
            BLOCK_ENTITIES.register("cable_be", () ->
                    BlockEntityType.Builder.of(com.rizor1x.industry.block.entity.CableBlockEntity::new,
                            ModBlocks.UNINSULATED_TIN_CABLE.get(), ModBlocks.TIN_CABLE.get(),
                            ModBlocks.UNINSULATED_COPPER_CABLE.get(), ModBlocks.COPPER_CABLE.get(),
                            ModBlocks.UNINSULATED_GOLD_CABLE.get(), ModBlocks.GOLD_CABLE.get(),
                            ModBlocks.UNINSULATED_HV_CABLE.get(), ModBlocks.HV_CABLE.get(),
                            ModBlocks.GLASS_FIBER_CABLE.get()
                    ).build(null));
}
