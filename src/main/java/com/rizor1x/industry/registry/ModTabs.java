package com.rizor1x.industry.registry;

import com.rizor1x.industry.Industry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTabs {
    // Создаем реестр для вкладок
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Industry.MODID);

    // Создаем саму вкладку
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.industry.main_tab")) // Название вкладки
            .icon(() -> new ItemStack(ModItems.BRONZE_INGOT.get())) // Иконка вкладки
            .displayItems((parameters, output) -> {
                // Магия: автоматически добавляем ВСЕ предметы из ModItems во вкладку
                ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
            }).build());
}