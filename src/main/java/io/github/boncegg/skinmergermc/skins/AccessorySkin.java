package io.github.boncegg.skinmergermc.skins;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

public class AccessorySkin {
    @Getter
    private final String name;

    @Getter @Setter
    private int[][] pixels;

    @Getter @Setter
    public int skinSize;

    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public BooleanProperty selectedProperty() { return selected; }
    public void setSelected(boolean value) { selected.set(value); }

    public AccessorySkin(String name, int[][] pixels, int skinSize) {
        this.name = name;
        this.pixels = pixels;
        this.skinSize = skinSize;
    }

    @Override
    public String toString() { return name; }
}