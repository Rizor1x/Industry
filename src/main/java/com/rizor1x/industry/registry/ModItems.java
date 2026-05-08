package com.rizor1x.industry.registry;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.item.custom.JetpackItem;
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

    // Батарейка
    public static final DeferredItem<Item> BATTERY = ITEMS.register("battery",
            () -> new com.rizor1x.industry.item.custom.BatteryItem(
                    new Item.Properties().component(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get(), 0),
                    10000));

    // Обычный Бур
    public static final DeferredItem<Item> BASIC_DRILL = ITEMS.register("basic_drill",
            () -> new com.rizor1x.industry.item.custom.EnergyDrillItem(
                    net.minecraft.world.item.Tiers.IRON,
                    new Item.Properties().attributes(net.minecraft.world.item.PickaxeItem.createAttributes(net.minecraft.world.item.Tiers.IRON, 1.0F, -2.8F))
                            .component(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get(), 0),
                    30000, 50, false));

    // Алмазный Бур (Ему еще добавляем компонент Режима по умолчанию)
    public static final DeferredItem<Item> DIAMOND_DRILL = ITEMS.register("diamond_drill",
            () -> new com.rizor1x.industry.item.custom.EnergyDrillItem(
                    net.minecraft.world.item.Tiers.DIAMOND,
                    new Item.Properties().attributes(net.minecraft.world.item.PickaxeItem.createAttributes(net.minecraft.world.item.Tiers.DIAMOND, 1.0F, -2.8F))
                            .component(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get(), 0)
                            .component(com.rizor1x.industry.registry.ModDataComponents.AREA_MODE.get(), false),
                    30000, 80, true));

    public static final DeferredItem<Item> BATPACK = ITEMS.register("batpack",
            () -> new com.rizor1x.industry.item.custom.BatPackItem(
                    com.rizor1x.industry.registry.ModArmorMaterials.BATPACK_MATERIAL,
                    60000, 100, // 60 000 Вместимость, 100 передача в тик
                    new Item.Properties().component(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get(), 0)));

    // 1. Обычный Джетпак (Слабая тяга: 0.12, Макс скорость вверх: 0.4)
    public static final DeferredItem<Item> JETPACK = ITEMS.register("jetpack",
            () -> new com.rizor1x.industry.item.custom.JetpackItem(
                    com.rizor1x.industry.registry.ModArmorMaterials.JETPACK_MATERIAL,
                    30000, 10,
                    0.12D, 0.4D, false, // <--- ИЗМЕНИЛИ ЦИФРЫ ТУТ
                    new Item.Properties().component(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get(), 0)));

    // 2. Продвинутый Джетпак (Мощная тяга: 0.18, Макс скорость вверх: 0.7)
    public static final DeferredItem<Item> ADVANCED_JETPACK = ITEMS.register("advanced_jetpack",
            () -> new com.rizor1x.industry.item.custom.JetpackItem(
                    com.rizor1x.industry.registry.ModArmorMaterials.JETPACK_MATERIAL,
                    300000, 30,
                    0.18D, 0.7D, false, // <--- ИЗМЕНИЛИ ЦИФРЫ ТУТ
                    new Item.Properties().component(com.rizor1x.industry.registry.ModDataComponents.ENERGY.get(), 0)));

    // 3. Квантовый нагрудник - 10 млн энергии, КРЕАТИВНЫЙ ПОЛЕТ
    public static final DeferredItem<Item> QUANTUM_CHESTPLATE = ITEMS.register("quantum_chestplate",
            () -> new JetpackItem(ModArmorMaterials.BATPACK_MATERIAL, 10000000, 50, 0.0, 0.0, true,
                    new Item.Properties().component(ModDataComponents.ENERGY.get(), 0)));

    // ==========================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ==========================================
    private static DeferredItem<Item> registerBasicItem(String name) {
        return ITEMS.registerSimpleItem(name, new Item.Properties());
    }
}