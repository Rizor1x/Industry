package com.rizor1x.industry.registry;

import com.rizor1x.industry.Industry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Industry.MODID);

    // Это "коробочка", в которой будет храниться энергия предмета (от 0 до Максимума)
    public static final Supplier<DataComponentType<Integer>> ENERGY = DATA_COMPONENTS.register("energy",
            () -> DataComponentType.<Integer>builder().persistent(com.mojang.serialization.Codec.INT).networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_INT).build());

    // ДОБАВЛЯЕМ НОВЫЙ КОМПОНЕНТ ДЛЯ РЕЖИМА БУРА (true = 3x3, false = 1x1)
    public static final Supplier<DataComponentType<Boolean>> AREA_MODE = DATA_COMPONENTS.register("area_mode",
            () -> DataComponentType.<Boolean>builder().persistent(com.mojang.serialization.Codec.BOOL).networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.BOOL).build());
}