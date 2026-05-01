package io.github.boncegg.skinmergermc.utils;

public enum Header {
    UPLOAD_SKIN("Select Skin (X64/X128)"),
    FOLDER_SELECTOR("Select Output Folder"),
    MISSING_BASE("Base Not Found"),
    MISSING_FILE("File Not Found"),
    INCORRECT_SAVE("Save Error");

    private final String text;

    Header(String string) {
        this.text = string;
    }

    public String get() {
        return text;
    }
}
