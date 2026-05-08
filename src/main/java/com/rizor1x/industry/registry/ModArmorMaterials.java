package com.rizor1x.industry.registry;

import com.rizor1x.industry.Industry;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, Industry.MODID);

    public static final net.neoforged.neoforge.registries.DeferredHolder<ArmorMaterial, ArmorMaterial> BATPACK_MATERIAL = ARMOR_MATERIALS.register("batpack", () ->
            new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.CHESTPLATE, 3); // Защита как у железного нагрудника
                    }),
                    9, // Зачаровываемость
                    SoundEvents.ARMOR_EQUIP_IRON, // Звук надевания
                    () -> Ingredient.of(Tags.Items.INGOTS_COPPER), // Чем чинится (если сломается)
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Industry.MODID, "batpack"))), // Название текстуры!
                    0.0F, // Прочность брони (Toughness)
                    0.0F  // Защита от отбрасывания
            )
    );
    public static final net.neoforged.neoforge.registries.DeferredHolder<ArmorMaterial, ArmorMaterial> JETPACK_MATERIAL = ARMOR_MATERIALS.register("jetpack", () ->
            new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> { map.put(ArmorItem.Type.CHESTPLATE, 4); }),
                    9, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(Tags.Items.INGOTS_COPPER),
                    // ВОТ ТУТ МЫ УКАЗЫВАЕМ ИМЯ НОВОЙ ТЕКСТУРЫ ("jetpack")
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Industry.MODID, "jetpack"))),
                    0.0F, 0.0F
            )
    );
}