package com.rizor1x.industry.registry;

import com.rizor1x.industry.Industry;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Industry.MODID);

    // ==========================================
    // БАЗОВЫЕ МАТЕРИАЛЫ
    // ==========================================

    public static final DeferredItem<Item> COPPER_DUST = registerBasicItem("copper_dust");

    // Олово (Tin)
    public static final DeferredItem<Item> TIN_INGOT = registerBasicItem("tin_ingot");
    public static final DeferredItem<Item> TIN_DUST = registerBasicItem("tin_dust");
    public static final DeferredItem<Item> TIN_PLATE = registerBasicItem("tin_plate");

    public static final DeferredItem<Item> LEAD_INGOT = registerBasicItem("lead_ingot");
    public static final DeferredItem<Item> LEAD_DUST = registerBasicItem("lead_dust");
    public static final DeferredItem<Item> LEAD_PLATE = registerBasicItem("lead_plate");

    // Бронза (Bronze)
    public static final DeferredItem<Item> BRONZE_INGOT = registerBasicItem("bronze_ingot");
    public static final DeferredItem<Item> BRONZE_DUST = registerBasicItem("bronze_dust");
    public static final DeferredItem<Item> BRONZE_PLATE = registerBasicItem("bronze_plate");

    // Сталь (Steel)
    public static final DeferredItem<Item> STEEL_INGOT = registerBasicItem("steel_ingot");
    public static final DeferredItem<Item> STEEL_DUST = registerBasicItem("steel_dust");
    public static final DeferredItem<Item> STEEL_PLATE = registerBasicItem("steel_plate");

    // ==========================================
    // ИНСТРУМЕНТЫ
    // ==========================================

    public static final DeferredItem<Item> MACHINE_CASING = registerBasicItem("machine_casing");
    public static final DeferredItem<Item> WRENCH = ITEMS.registerSimpleItem("wrench", new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> BATTERY = ITEMS.register("battery", () -> new com.rizor1x.industry.item.custom.BatteryItem(new Item.Properties(), 10000));

    // ==========================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ==========================================
    private static DeferredItem<Item> registerBasicItem(String name) {
        return ITEMS.registerSimpleItem(name, new Item.Properties());
    }
}