package io.github.boncegg.skinmergermc;

import io.github.boncegg.skinmergermc.manager.ProcessManager;
import io.github.boncegg.skinmergermc.skins.AccessorySkin;
import io.github.boncegg.skinmergermc.skins.MinecraftSkin;
import io.github.boncegg.skinmergermc.utils.Header;
import io.github.boncegg.skinmergermc.utils.Message;
import io.github.boncegg.skinmergermc.manager.FileManager;
import io.github.boncegg.skinmergermc.manager.SkinManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static io.github.boncegg.skinmergermc.utils.SkinType.INVALID;

public class SkinMergerController {
    @FXML public TextArea logArea;
    @FXML public ListView<AccessorySkin> accesoryListView;
    @FXML private Label selectBase;
    @FXML private Label selectSkins;
    @FXML private Label selectFolder;
    @FXML private ImageView previewImageView;
    @FXML private ListView<String> skinListView;

    private ObservableList<AccessorySkin> accessories = FXCollections.observableArrayList();

    SkinManager skinManager = new SkinManager();
    FileManager fileManager = new FileManager();
    ProcessManager processManager = new ProcessManager();

    //region log
    public void addLog(String log) {
        logArea.appendText("- " + log + "\n");
    }
    //endregion log

    //region preview
    public void resetPreview() {
        previewImageView.setImage(null);
    }

    public void updatePreview(MinecraftSkin skin) {
        if (skin == null) return;

        int[][] pixels = skin.getActualPixels();
        if (pixels == null) return;

        previewImageView.setImage(processManager.intToFXImage(pixels));
    }
    //endregion preview

    //region choosers
    public FileChooser createChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("png", "*.png")
        );

        return fileChooser;
    }

    public File selectFolder(Window window, String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        File selectedFile = directoryChooser.showDialog(window);
        if (selectedFile == null || !selectedFile.exists()) return null;
        return selectedFile;
    }

    public Path selectFolderPath(Window window, String title) {
        File pathFolder = selectFolder(window,title);
        if (pathFolder == null) return null;
        return pathFolder.toPath();
    }

    public File imageUpload(Window window, String title) {
        FileChooser fileChooser = createChooser(title);
        return fileChooser.showOpenDialog(window);
    }

    public List<File> multipleImageUpload(Window window, String title) {
        FileChooser fileChooser = createChooser(title);

        return fileChooser.showOpenMultipleDialog(window);
    }
    //endregion choosers

    //region visuals
    @FXML
    public void onButtonHover(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle(
            "-fx-background-color: #252525;" +
            "-fx-border-color: #555555;" +
            "-fx-border-width: 0.5;" +
            "-fx-text-fill: #ffffff;" +
            "-fx-font-size: 12;" +
            "-fx-padding: 8 12;" +
            "-fx-border-radius: 4;" +
            "-fx-cursor: hand;"
        );
    }

    @FXML
    public void onButtonExit(MouseEvent event) {
        Button btn = (Button) event.getSource();

        btn.setStyle(
            "-fx-background-color: #141414;" +
            "-fx-border-color: #2a2a2a;" +
            "-fx-border-width: 1;" +
            "-fx-text-fill: #cccccc;" +
            "-fx-font-size: 11;" +
            "-fx-padding: 6 8;" +
            "-fx-border-radius: 3;" +
            "-fx-cursor: hand;"
        );
    }
    //endregion visuals

    //region accessories
    private boolean loadingAccesories = false;

    private void loadAccesories(MinecraftSkin skin) {
        loadingAccesories = true;

        for (AccessorySkin accessorySkin : accessories) {
            if (accessorySkin == null) continue;

            accessorySkin.setSelected(skin.getAccessory().contains(accessorySkin.getName()));
        }

        loadingAccesories = false;
    }

    private void bindAccesoryListeners() {
        for (AccessorySkin accessorySkin : accessories) {
            if (accessorySkin == null) continue;

            final String name = accessorySkin.getName();
            accessorySkin.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (loadingAccesories) return;

                MinecraftSkin skin = skinManager.getSelectedSkin();
                if (skin == null) return;

                if (isSelected) {
                    skin.getAccessory().add(name);
                } else {
                    skin.getAccessory().remove(name);
                }
            });
        }
    }
    //endregion accessories

    //region init
    @FXML
    public void initialize() {
        accesoryListView.setItems(accessories);
        accesoryListView.setCellFactory(CheckBoxListCell.forListView(AccessorySkin::selectedProperty));

        skinListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        skinManager.setSelectedSkin(newValue);
                        loadAccesories(skinManager.getSelectedSkin());
                        updatePreview(skinManager.getSelectedSkin());
                    }
                }
        );
    }
    //endregion init

    //region onClick
    @FXML
    public void onSelectAccesory(ActionEvent event) {
        Window window = ((Node) event.getSource()).getScene().getWindow();
        File accesoryFolder = selectFolder(window, Header.FOLDER_SELECTOR.get());

        if (accesoryFolder == null) {
            addLog(Message.INVALID_FOLDER.get());
            return;
        }

        accessories.clear();
        skinManager.clearAccessories();

        File[] files = accesoryFolder.listFiles((dir, name) -> name.endsWith(".png"));
        if (files == null) return;

        Arrays.sort(files);

        for (File file : files) {
            if (file == null || !file.exists()) {
                addLog(Message.IMAGE_UPLOAD_FAIL.get());
                continue;
            }

            try {
                int skinSize = fileManager.getSkinSize(file);
                if (skinSize == INVALID.get()) {
                    addLog(file.getName() + ": " + Message.IMAGE_UPLOAD_FAIL.get());
                    continue;
                }

                int[][] skin = processManager.processSkin(ImageIO.read(file),skinSize);
                AccessorySkin acc = new AccessorySkin(file.getName(), skin, skinSize);
                accessories.add(acc);
                skinManager.addAccessory(acc);
            } catch (Exception e) {
                addLog(file.getName() + ": " + Message.UPLOAD_FAILED.get());
            }
        }

        bindAccesoryListeners();
        addLog(skinManager.getAccessories().size() + ": " + Message.UPLOAD_ACCESSORY.get());
    }

    @FXML
    protected void onSelectBase(ActionEvent event) throws IOException {
        Window window = ((Node) event.getSource()).getScene().getWindow(); // Esto es de clase Window, no Stage. Esto es un error? Como obtengo una clase Stage?
        File file = imageUpload(window, Header.UPLOAD_SKIN.get());

        int skinSize = fileManager.getSkinSize(file);
        if (skinSize == INVALID.get()) {
            String name = file == null ? "File not found" : file.getName();
            addLog(name + ": " + Message.IMAGE_UPLOAD_FAIL.get());
            return;
        }

        BufferedImage skinImage = processManager.fileToBImg(file);
        int[][] pixels = processManager.processSkin(skinImage,skinSize);
        MinecraftSkin skin = new MinecraftSkin("Base",skinImage,pixels,skinSize);
        skinManager.setSkinBase(skin);

        addLog(Message.BASE_SELECTED.get());
    }

    @FXML
    protected void onSelectSkins(ActionEvent event) throws IOException {
        MinecraftSkin base = skinManager.getSkinBase();

        if (base == null || base.getName().isEmpty()) {
            addLog(Message.MISSING_BASE.get());
            return;
        }

        Window window = ((Node) event.getSource()).getScene().getWindow();
        List<File> files = multipleImageUpload(window, Header.UPLOAD_SKIN.get());

        if (files == null) return;
        for (File file : files) {
            int skinSize = fileManager.getSkinSize(file);
            if (skinSize == INVALID.get()) {
                String name = file == null ? Header.MISSING_FILE.get() : file.getName();
                addLog(name + ": " + Message.IMAGE_UPLOAD_FAIL.get());
                continue;
            }

            String name = file.getName();

            BufferedImage skinImage = processManager.fileToBImg(file);
            int[][] pixels = processManager.processSkin(skinImage,skinSize);
            MinecraftSkin skin = new MinecraftSkin(name,skinImage,pixels,skinSize);
            skinManager.addSkin(name,skin);
            skinListView.getItems().add(name);
        }
        addLog(Message.SKINS_SELECTED.get());
    }

    @FXML
    protected void onDeleteSkin() {
        MinecraftSkin skin = skinManager.getSelectedSkin();

        if (skin == null) {
            addLog(Message.MISSING_SELECTED.get());
            return;
        }

        addLog(skin.getName() + ": " + Message.SKIN_DELETE.get());

        resetPreview();
        skinManager.setSelectedSkin(null);
        skinManager.removeSkin(skin.getName());
        skinListView.getItems().remove(skin.getName());
    }

    @FXML
    protected void onSaveSkins(ActionEvent event) {
        Path output = onSelectOutput(event);

        if (output == null) {
            addLog(Message.INVALID_FOLDER.get());
            return;
        }

        int skinsSize = skinManager.getSkins().size();
        fileManager.saveFiles(skinManager.getSkins().values(),output);

        addLog(Message.COMPLETED_SAVE.get() + skinsSize + "/" + skinsSize);
    }

    @FXML
    protected void onMergeSkin() {
        MinecraftSkin skinBase = skinManager.getSkinBase();
        int skinBaseSize = skinBase.getSkinSize();

        for (MinecraftSkin skin : skinManager.getSkins().values()) {
            if (skinBaseSize == 128 && skin.getSkinSize() == 64) {
                addLog(skin.getName() + ": " + Message.SKIN_RESIZED.get());
            }
            else if (skin.getSkinSize() == 128 && skinBaseSize == 64) {
                addLog(skin.getName() + ": " + Message.BASE_RESIZED.get());
            }

            int[][] rawSkin = processManager.mergeSkin(skinBase.getOriginalPixels(), skin.getActualPixels());

            for (String accessoryName : skin.getAccessory()) {
                AccessorySkin accessorySkin = skinManager.getAccessories().get(accessoryName);
                if (accessorySkin == null) continue;

                if (accessorySkin.getSkinSize() == 128 && skinBaseSize == 64)
                    addLog(accessorySkin.getName() + " & " + skin.getName() + ": " + Message.ACCESSORY_SKIN_RESIZED.get());

                int[][] accPixels = (accessorySkin.getSkinSize() == 64 && skinBaseSize == 128)
                        ? processManager.scaleX64toX128(accessorySkin.getPixels())
                        : accessorySkin.getPixels();

                rawSkin = processManager.mergeSkin(accPixels, rawSkin);
            }

            skin.setSkinSize(skinBaseSize);
            skin.setActualPixels(rawSkin);
            skin.setActualSkin(processManager.newImage(rawSkin));
        }

        updatePreview(skinManager.getSelectedSkin());
        addLog(Message.MERGE_COMPLETED.get());
    }

    @FXML
    protected void onResetSkin() {
        MinecraftSkin skin = skinManager.getSelectedSkin();
        if (skin == null) return;

        addLog(skin.getName() + ": " + Message.SKIN_RESET.get());
        skin.setActualPixels(ProcessManager.deepCopy(skin.getOriginalPixels()));
        skin.setActualSkin(skin.getOriginalSkin());
        updatePreview(skin);
    }

    @FXML
    protected Path onSelectOutput(ActionEvent event) {
        Window window = ((Node) event.getSource()).getScene().getWindow();
        Path outputFolder = selectFolderPath(window, Header.FOLDER_SELECTOR.get());

        if (outputFolder == null) {
            return null;
        }

        addLog(Message.FOLDER_SELECTED.get());
        return outputFolder;
    }

    @FXML
    protected void onResetAll() {
        resetPreview();
        skinManager.resetAll();
        accessories.clear();
        skinListView.getItems().clear();

        addLog(Message.RESET_ALL.get());
    }
    //endregion onClick
}