package io.github.boncegg.skinmergermc.manager;

import io.github.boncegg.skinmergermc.utils.SkinArea;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ProcessManager {
    public static int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][];

        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }

        return copy;
    }

    public record Area(int xMin, int xMax, int yMin, int yMax) {}
    private static final SkinArea[] SKIN_AREAS = SkinArea.values();

    public BufferedImage fileToBImg(File file) throws IOException {
        return ImageIO.read(file);
    }

    public int[][] scaleX64toX128(int[][] skin) {
       int[][] newSkin = new int[128][128];

        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                int color = skin[x][y];
                newSkin[x*2][y*2] = color;
                newSkin[x*2][y*2+1] = color;
                newSkin[x*2+1][y*2] = color;
                newSkin[x*2+1][y*2+1] = color;
            }
        }

        return newSkin;
    }

    public int[] getOuterFromBase(int x, int y, int skinSize) {
       for (SkinArea value : SKIN_AREAS) {
            if (value.isInBase(x, y, skinSize)) {
                int[] val = value.baseToOuter(x, y, skinSize);

                if (val != null) {
                    return val;
                }
            }
        }

        return null;
    }

    public int[][] processSkin(BufferedImage image, int skinSize) {
        if (skinSize != 64 && skinSize != 128) {
            skinSize = 64;
        }

        int[][] result = new int[skinSize][skinSize];

        if (image.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
            final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

            for (int y = 0; y < skinSize; y++) {
                for (int x = 0; x < skinSize; x++) {
                    int idx = (y * skinSize + x) * 4;

                    int alpha = (pixels[idx] & 0xFF); // alpha
                    int blue = (pixels[idx + 1] & 0xFF); // blue
                    int green = (pixels[idx + 2] & 0xFF); // green
                    int red = (pixels[idx + 3] & 0xFF); // red

                    if (alpha == 0) {
                        // If the pixel isn't visible, isn't necessary
                        result[x][y] = 0;
                        continue;
                    }

                    result[x][y] = (alpha << 24) | (red << 16) | (green << 8) | blue;
                }
            }
        } else {
            // This applies the normal method for scan the skin (it's only a bit slower)
            for (int x = 0; x < skinSize; x++) {
                for (int y = 0; y < skinSize; y++) {
                    int pixelRGB = image.getRGB(x,y);

                    result[x][y] = ((pixelRGB >> 24) & 0xFF) == 0 ? 0 : pixelRGB;
                }
            }
        }

        return result;
    }

    /**
     * Merge the base with the skin
     * @param base Skin base to be applied
     * @param skin Skin target
     * @return int[][] Base applied to the skin in X64/X128
     */
    public int[][] mergeSkin(int[][] base, int[][] skin) {
        if (base.length == 128 && skin.length == 64) skin = scaleX64toX128(skin);
        else if (base.length == 64 && skin.length == 128) base = scaleX64toX128(base);

        int size = base.length;
        int[][] result = deepCopy(skin);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int pixel = base[x][y];
                if (pixel == 0) continue;

                int[] outer = getOuterFromBase(x, y, size);
                if (outer != null && base[outer[0]][outer[1]] == 0) {
                    result[outer[0]][outer[1]] = 0;
                }

                result[x][y] = pixel;
            }
        }

        return result;
    }

    public BufferedImage newImage(int[][] pixels) {
        int pxLen = pixels.length;
        if (pxLen != 64 && pxLen != 128) {
            pxLen = 64;
        }

        BufferedImage image = new BufferedImage(pxLen, pxLen, BufferedImage.TYPE_INT_ARGB);
        int[] newPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < pxLen; y++) {
            for (int x = 0; x < pxLen; x++) {
                newPixels[y * pxLen + x] = pixels[x][y];
            }
        }

        return image;
    }

    public Image intToFXImage(int[][] pixels) {
        int pxLen = pixels.length;
        WritableImage fxImage = new WritableImage(pxLen,pxLen);
        PixelWriter pw = fxImage.getPixelWriter();

        for (int y = 0; y < pxLen; y++) {
            for (int x = 0; x < pxLen; x++) {
                pw.setArgb(x,y,pixels[x][y]);
            }
        }

        return fxImage;
    }
}
