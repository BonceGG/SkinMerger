module io.github.boncegg.skinmergermc {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires static lombok;

    //exports io.github.boncegg.skinmergermc.utils.warning;

    opens io.github.boncegg.skinmergermc to javafx.fxml;
    exports io.github.boncegg.skinmergermc;
    exports io.github.boncegg.skinmergermc.manager;
    opens io.github.boncegg.skinmergermc.manager to javafx.fxml;
    exports io.github.boncegg.skinmergermc.utils;
    exports io.github.boncegg.skinmergermc.skins;
    opens io.github.boncegg.skinmergermc.skins to javafx.fxml;
}