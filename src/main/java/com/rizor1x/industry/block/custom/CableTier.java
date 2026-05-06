package com.rizor1x.industry.block.custom;

public enum CableTier {
    // Название (Вместимость, Передача в тик, Изоляция)
    UNINSULATED_COPPER(32, 32, false),
    COPPER(32, 32, true),
    UNINSULATED_TIN(128, 128, false), // Задел на будущее!
    TIN(128, 128, true);

    public final int capacity;
    public final int maxTransfer;
    public final boolean insulated;

    CableTier(int capacity, int maxTransfer, boolean insulated) {
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
        this.insulated = insulated;
    }
}