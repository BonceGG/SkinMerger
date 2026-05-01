package io.github.boncegg.skinmergermc.manager;

import io.github.boncegg.skinmergermc.skins.MinecraftSkin;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

import static io.github.boncegg.skinmergermc.utils.SkinType.X64;
import static io.github.boncegg.skinmergermc.utils.SkinType.X128;
import static io.github.boncegg.skinmergermc.utils.SkinType.INVALID;

public class FileManager {
    private final byte[] PNG_BYTES = {
            (byte) 0x89,
            (byte) 0x50, // P
            (byte) 0x4E, // N
            (byte) 0x47, // G
            (byte) 0x0D,
            (byte) 0x0A,
            (byte) 0x1A,
            (byte) 0x0A,
    };

    public boolean isPNG(File file) throws IOException {
        try (InputStream is = Files.newInputStream(file.toPath())) {
            byte[] header = new byte[PNG_BYTES.length];
            int bytesRead = is.read(header);

            if (bytesRead < PNG_BYTES.length) return false;

            for (int i = 0; i < PNG_BYTES.length; i++) {
                if (header[i] != PNG_BYTES[i]) return false;
            }

            return true;
        }
    }

    public int getSize(File file) { // Fast, check if the skin is X64 or X128
        try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            if (iis == null) return INVALID.get();

            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) return INVALID.get();

            ImageReader reader = readers.next();

            try {
                reader.setInput(iis, true, true);

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                if (width == X64.get() && height == X64.get()) {
                    return X64.get();
                } else if (width == X128.get() && height == X128.get()) {
                    return X128.get();
                }

                return INVALID.get();
            } finally {
                reader.dispose();
            }
        } catch (Exception e) {
            return INVALID.get();
        }
    }

    public int getSkinSize(File file) throws IOException {
        if (file == null || !file.exists()) return INVALID.get();

        if (isPNG(file)) {
            int size = getSize(file);

            if (size == X64.get() || size == X128.get()) {
                return size;
            }
        }

        return INVALID.get();
    }

    public void saveFiles(Collection<MinecraftSkin> skins, Path savePath) {
        System.out.println(Runtime.getRuntime().availableProcessors());

        skins.parallelStream().forEach(skin -> {
            ImageWriter newWriter = ImageIO.getImageWritersByFormatName("png").next();

            try {
                File newFile = savePath.resolve(skin.getName()).toFile();

                try (javax.imageio.stream.ImageOutputStream ios = ImageIO.createImageOutputStream(newFile)) {
                    newWriter.setOutput(ios);
                    newWriter.write(skin.getActualSkin());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                newWriter.dispose();
            }
        });
    }
}