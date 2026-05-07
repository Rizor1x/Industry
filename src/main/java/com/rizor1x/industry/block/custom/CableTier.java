package com.rizor1x.industry.block.custom;

public enum CableTier {
    // Оловянный провод (Сверхнизкое напряжение - ULV)
    UNINSULATED_TIN(32, 32, false),
    TIN(32, 32, true),

    // Медный провод (Низкое напряжение - LV)
    UNINSULATED_COPPER(128, 128, false),
    COPPER(128, 128, true),

    // Золотой провод (Среднее напряжение - MV)
    UNINSULATED_GOLD(512, 512, false),
    GOLD(512, 512, true),

    // Высоковольтный провод из Железа (Высокое напряжение - HV)
    UNINSULATED_HV(2048, 2048, false),
    HV(2048, 2048, true),

    // Стекловолоконный провод (Экстремальное напряжение - EV)
    // У него нет оголенной версии, он всегда изолирован!
    GLASS_FIBER(8192, 8192, true);

    public final int capacity;
    public final int maxTransfer;
    public final boolean insulated;

    CableTier(int capacity, int maxTransfer, boolean insulated) {
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
        this.insulated = insulated;
    }
}