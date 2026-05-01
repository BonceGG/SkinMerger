package io.github.boncegg.skinmergermc.manager;

import io.github.boncegg.skinmergermc.skins.AccessorySkin;
import io.github.boncegg.skinmergermc.skins.MinecraftSkin;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class SkinManager {
    @Setter
    private String selectedSkin;

    @Getter @Setter
    private MinecraftSkin skinBase;

    @Getter @Setter
    private Map<String, MinecraftSkin> skins = new HashMap<>();

    @Getter @Setter
    private Map<String, AccessorySkin> accessories = new HashMap<>();

    public void addAccessory(AccessorySkin accessorySkin) {
        this.accessories.put(accessorySkin.getName(), accessorySkin);
    }

    public void clearAccessories() {
        for (MinecraftSkin skin : skins.values()) {
            skin.accesoryClear();
        }

        this.accessories.clear();
    }

    public void addSkin(String name, MinecraftSkin skin) {
        this.skins.put(name, skin);
    }

    public void removeSkin(String name) {
        this.skins.remove(name);
    }

    public MinecraftSkin getSelectedSkin() {
        return skins.get(selectedSkin);
    }

    public void resetAll() {
        selectedSkin = null;
        skinBase = null;
        accessories.clear();
        skins.clear();
    }
}
