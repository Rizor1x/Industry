package com.rizor1x.industry.registry;

import com.rizor1x.industry.block.entity.BaseMachineBlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

// Аннотация заставляет игру 100% запустить этот код при старте
public class ModCapabilities {

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.CRUSHER_BE.get(),
                BaseMachineBlockEntity::getItemHandler
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.CRUSHER_BE.get(),
                BaseMachineBlockEntity::getEnergyStorage
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.COAL_GENERATOR_BE.get(),
                BaseMachineBlockEntity::getItemHandler
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.COAL_GENERATOR_BE.get(),
                BaseMachineBlockEntity::getEnergyStorage
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ELECTRIC_FURNACE_BE.get(),
                BaseMachineBlockEntity::getItemHandler
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ELECTRIC_FURNACE_BE.get(),
                BaseMachineBlockEntity::getEnergyStorage
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.BATTERY_BOX_BE.get(),
                BaseMachineBlockEntity::getItemHandler
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.BATTERY_BOX_BE.get(),
                BaseMachineBlockEntity::getEnergyStorage
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.CABLE_BE.get(),
                (be, side) -> be.energyStorage
        );
    }
}