package io.github.boncegg.skinmergermc.utils;

public enum SkinType { // Skin Types
    X64(64),
    X128(128),
    INVALID(-1);

    private final int size;

    SkinType(int size) {
        this.size = size;
    }

    public int get() {
        return this.size;
    }
}
