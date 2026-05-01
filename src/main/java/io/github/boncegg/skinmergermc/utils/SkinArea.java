package io.github.boncegg.skinmergermc.utils;

import io.github.boncegg.skinmergermc.manager.ProcessManager.Area;
import lombok.Getter;

public enum SkinArea {
    HEAD(
        new Area(0,31,0,15), new Area(32,63,0,15)
    ),
    TORSO(
        new Area(16,39,16,31), new Area(16,39,32,47)
    ),

    RIGHT_ARM(
        new Area(40,55,16,31), new Area(40,55,32,47)
    ),

    LEFT_ARM(
        new Area(32,47,48,63), new Area(48,63,48,63)
    ),

    RIGHT_LEG(
        new Area(0,15,16,31), new Area(0,15,32,47)
    ),

    LEFT_LEG(
        new Area(16,31,48,63), new Area(0,15,48,63)
    );

    @Getter
    private final Area base;

    @Getter
    private final Area outer;

    SkinArea(Area base, Area outer) {
        this.base = base;
        this.outer = outer;
    }

    private int scaleSkin(int skinSize) {
        if (skinSize != 64 && skinSize != 128) return 1;

        return skinSize/64;
    }

    private Area getScaledBase(int skinSize) { // If the skin is 64x64 then scale is 1. If it's 128x128, scale is 2.
        int scale = scaleSkin(skinSize);
        return new Area(
            base.xMin() * scale,
            base.xMax() * scale + (scale - 1),
            base.yMin() * scale,
            base.yMax() * scale + (scale - 1)
        );
    }

    private Area getScaledOuter(int skinSize) {
        int scale = scaleSkin(skinSize);
        return new Area(
            outer.xMin() * scale,
            outer.xMax() * scale + (scale - 1),
            outer.yMin() * scale,
            outer.yMax() * scale + (scale - 1)
        );
    }

    public boolean isInBase(int pointX, int pointY, int skinSize) {
        Area scaled = getScaledBase(skinSize);

        return ( // this is for check if the points are inside the base region.
            pointX >= scaled.xMin() && pointX <= scaled.xMax()  &&
            pointY >= scaled.yMin() && pointY <= scaled.yMax()
        );
    }

//  Commented cuz unused
//    public boolean isInOuter(int pointX, int pointY, int skinSize) {
//        Area scaled = getScaledOuter(skinSize);
//
//        return ( // (Unused) this is for check if the points are inside the outer region.
//            pointX >= scaled.xMin() && pointX <= scaled.xMax()&&
//            pointY >= scaled.yMin() && pointY <= scaled.yMax()
//        );
//    }

    public int[] baseToOuter(int pointX, int pointY, int skinSize) {
        // This method gets the point from the base
        // and converts it in a point of the outer

        Area scaledBase = getScaledBase(skinSize);
        Area scaledOuter = getScaledOuter(skinSize);

        int newX = (scaledOuter.xMin() + (pointX - scaledBase.xMin())); // in few words: this converts the point in the base to point in the outer
        int newY = (scaledOuter.yMin() + (pointY - scaledBase.yMin()));

        if (newX < scaledOuter.xMin() || newX > scaledOuter.xMax() || // if the point is outside the outer region, then this might be an error
            newY < scaledOuter.yMin() || newY > scaledOuter.yMax()) {
            return null;
        }

        return new int[] {newX,newY};
    }
}
