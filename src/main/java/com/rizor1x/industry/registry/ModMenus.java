package com.rizor1x.industry.registry;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.menu.CrusherMenu;
import com.rizor1x.industry.menu.ElectricFurnaceMenu;
import com.rizor1x.industry.menu.GeneratorMenu;
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

    public static final Supplier<MenuType<ElectricFurnaceMenu>> ELECTRIC_FURNACE_MENU = MENUS.register("electric_furnace_menu",
            () -> IMenuTypeExtension.create(ElectricFurnaceMenu::new));

}