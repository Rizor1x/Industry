package com.rizor1x.industry.registry;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.menu.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Industry.MODID);

    // Регистрируем меню Дробителя
    public static final Supplier<MenuType<CrusherMenu>> CRUSHER_MENU = MENUS.register("crusher_menu",
            () -> IMenuTypeExtension.create(CrusherMenu::new));

    public static final Supplier<MenuType<GeneratorMenu>> GENERATOR_MENU = MENUS.register("generator_menu",
            () -> IMenuTypeExtension.create(GeneratorMenu::new));

    public static final Supplier<MenuType<BatteryBoxMenu>> BATTERY_BOX_MENU = MENUS.register("battery_box_menu",
            () -> IMenuTypeExtension.create(BatteryBoxMenu::new));

    public static final Supplier<MenuType<ElectricFurnaceMenu>> ELECTRIC_FURNACE_MENU = MENUS.register("electric_furnace_menu",
            () -> IMenuTypeExtension.create(ElectricFurnaceMenu::new));

    public static final Supplier<MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU = MENUS.register("solar_panel_menu",
            () -> net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create(SolarPanelMenu::new));
}