package io.github.boncegg.skinmergermc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class SkinMergerApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SkinMergerApp.class.getResource("main-view.fxml"));
        Scene merger = new Scene(fxmlLoader.load(), 980, 640);

        InputStream is = SkinMergerApp.class.getResourceAsStream("/icon.png");
        if (is == null) {
            throw new IOException("Icon not found");
        }
        stage.getIcons().add(new Image(is));

        stage.setTitle("Skin Merger - V1.0");
        stage.setScene(merger);
        stage.setResizable(false);
        stage.show();
    }
}
