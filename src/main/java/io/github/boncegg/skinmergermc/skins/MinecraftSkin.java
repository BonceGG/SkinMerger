package io.github.boncegg.skinmergermc.skins;

import io.github.boncegg.skinmergermc.manager.ProcessManager;
import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.util.*;

public class MinecraftSkin {
    @Getter
    private final String name;

    @Getter private final BufferedImage originalSkin;
    @Getter private final int[][] originalPixels;

    @Getter @Setter private BufferedImage actualSkin;
    @Getter @Setter private int[][] actualPixels;

    @Getter @Setter
    private Set<String> accessory = new HashSet<>();

    @Getter @Setter
    private int skinSize;

    public void accesoryClear() {
        accessory.clear();
    }

    public MinecraftSkin(String name, BufferedImage originalSkin, int[][] pixels, int skinSize) {
        this.name = name;
        this.originalSkin = originalSkin;
        this.originalPixels = ProcessManager.deepCopy(pixels);
        this.skinSize = skinSize;
        actualSkin = originalSkin;
        actualPixels = ProcessManager.deepCopy(pixels);
    }
}
