package com.rizor1x.industry;

import com.rizor1x.industry.client.screen.*;
import com.rizor1x.industry.registry.ModMenus;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Industry.MODID, dist = Dist.CLIENT)
public class IndustryClient {
    public IndustryClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        // Говорим игре: когда открывается CrusherMenu, показывай картинку CrusherScreen
        event.register(ModMenus.CRUSHER_MENU.get(), CrusherScreen::new);
        event.register(ModMenus.GENERATOR_MENU.get(), GeneratorScreen::new);
        event.register(ModMenus.SOLAR_PANEL_MENU.get(), SolarPanelScreen::new);
        event.register(ModMenus.ELECTRIC_FURNACE_MENU.get(), ElectricFurnaceScreen::new);
        event.register(ModMenus.BATTERY_BOX_MENU.get(), BatteryBoxScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        Industry.LOGGER.info("HELLO FROM CLIENT SETUP");
        Industry.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
