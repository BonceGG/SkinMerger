package io.github.boncegg.skinmergermc.utils;

public enum Message {
    IMAGE_UPLOAD_FAIL("Invalid image file."),
    INVALID_FOLDER("Invalid output folder."),
    MISSING_BASE("Please select a base skin first."),
    MISSING_SELECTED("No skin selected."),
    COMPLETED_SAVE("Skins saved: "),
    SKIN_DELETE("Skin removed."),
    BASE_SELECTED("Base skin selected."),
    FOLDER_SELECTED("Output folder selected."),
    SKIN_RESET("Skin restored to original."),
    MERGE_COMPLETED("Merge completed."),
    SKINS_SELECTED("Skins added."),
    ACCESSORY_SKIN_RESIZED("Skin upscaled to match accessory size."),
    BASE_RESIZED("Base upscaled to match skin size."),
    SKIN_RESIZED("Skin upscaled to match base size."),
    UPLOAD_FAILED("Failed to load file."),
    UPLOAD_ACCESSORY("Accessory added."),
    RESET_ALL("Reset completed.");

    private final String text;

    Message(String string) {
        this.text = string;
    }

    public String get() {
        return text;
    }
}
