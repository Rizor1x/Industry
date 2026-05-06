package com.rizor1x.industry.registry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rizor1x.industry.Industry;
import com.rizor1x.industry.recipe.CrusherRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Industry.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Industry.MODID);

    // Регистрируем ТИП рецепта (чтобы отличать печку от дробителя)
    public static final Supplier<RecipeType<CrusherRecipe>> CRUSHER_TYPE = TYPES.register("crushing", () -> new RecipeType<CrusherRecipe>() {
        @Override
        public String toString() {
            return "crushing";
        }
    });

    // Кодеки - это штуки, которые читают JSON файлы и превращают их в код 1.21.1
    public static final MapCodec<CrusherRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(CrusherRecipe::input),
            ItemStack.CODEC.fieldOf("output").forGetter(CrusherRecipe::output)
    ).apply(inst, CrusherRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, CrusherRecipe::input,
            ItemStack.STREAM_CODEC, CrusherRecipe::output,
            CrusherRecipe::new
    );

    // Регистрируем СЕРИАЛИЗАТОР (читатель файлов)
    public static final Supplier<RecipeSerializer<CrusherRecipe>> CRUSHER_SERIALIZER = SERIALIZERS.register("crushing", () -> new RecipeSerializer<CrusherRecipe>() {
        @Override
        public MapCodec<CrusherRecipe> codec() { return CODEC; }
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> streamCodec() { return STREAM_CODEC; }
    });
}