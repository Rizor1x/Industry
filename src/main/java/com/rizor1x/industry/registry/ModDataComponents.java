package com.rizor1x.industry.registry;

import com.mojang.serialization.Codec;
import com.rizor1x.industry.Industry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Industry.MODID);

    // Это "коробочка", в которой будет храниться энергия предмета (от 0 до Максимума)
    public static final Supplier<DataComponentType<Integer>> ENERGY = DATA_COMPONENTS.register("energy",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
}