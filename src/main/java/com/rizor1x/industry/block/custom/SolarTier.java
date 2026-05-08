package com.rizor1x.industry.block.custom;

public enum SolarTier {
    // Название (День, Ночь, Дождь, Вместимость, Передача в тик)
    BASIC(10, 0, 0, 10000, 32),         // Обычная панель (1 EU/t) - не работает ночью и в дождь
    ADVANCED(80, 10, 10, 30000, 128),   // Продвинутая (8 EU/t) - дает немного ночью
    HYBRID(640, 64, 64, 100000, 512),   // Гибридная (64 EU/t)
    ULTIMATE(5120, 512, 512, 1000000, 2048); // Ультимативная (512 EU/t)

    public final int dayGen;
    public final int nightGen;
    public final int rainGen;
    public final int capacity;
    public final int maxTransfer;

    SolarTier(int dayGen, int nightGen, int rainGen, int capacity, int maxTransfer) {
        this.dayGen = dayGen;
        this.nightGen = nightGen;
        this.rainGen = rainGen;
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
    }
}