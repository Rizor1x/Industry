package com.rizor1x.industry;

import com.rizor1x.industry.block.entity.BaseMachineBlockEntity;
import com.rizor1x.industry.registry.*;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Industry.MODID)
public class Industry {
    public static final String MODID = "industry";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Industry(IEventBus modEventBus, ModContainer modContainer) {
        // Подключаем наши модульные реестры к шине событий
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTabs.CREATIVE_MODE_TABS.register(modEventBus);

        ModMenus.MENUS.register(modEventBus);

        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        ModArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModRecipes.TYPES.register(modEventBus);

        // Регистрация базовой настройки
        modEventBus.addListener(this::commonSetup);

        modEventBus.addListener(com.rizor1x.industry.registry.ModCapabilities::register);
        modEventBus.addListener(com.rizor1x.industry.network.ModNetworking::register);
        modEventBus.addListener(com.rizor1x.industry.IndustryClient::registerScreens);

        // Конфиг мода (оставляем из генератора)
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Industry Mod: Is Loaded");
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                com.rizor1x.industry.registry.ModBlockEntities.CRUSHER_BE.get(),
                BaseMachineBlockEntity::getItemHandler
        );
    }
}