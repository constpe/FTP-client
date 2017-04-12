import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MainView extends Application{
    static Manager manager = null;
    static String selection = null;
    static ObservableList<FileInfo> filesList = FXCollections.observableArrayList();

    public void refreshFilesView(TableView<FileInfo> filesView) {
        try {
            FileInfo[] fileInfo = manager.listFiles(".");
            if (fileInfo.length > 1) {
                filesList.clear();
                for (FileInfo file : manager.listFiles(".")) {
                    filesList.add(file);
                }
            }
        }
        catch (Exception e) {}
        filesView.setItems(filesList);
    }

    public void start(Stage primaryStage) {
        VBox root = new VBox();
        HBox menuBox = new HBox();

        TableView<FileInfo> filesView = new TableView<>(filesList);
        filesView.setEditable(false);

        TableColumn<FileInfo, String> fileName = new TableColumn<>("File Name");
        fileName.setPrefWidth(300);
        fileName.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("fileName"));

        TableColumn<FileInfo, Long> fileType = new TableColumn<>("Type");
        fileType.setPrefWidth(80);
        fileType.setCellValueFactory(new PropertyValueFactory<FileInfo, Long>("fileType"));

        TableColumn<FileInfo, String> fileDate = new TableColumn<>("Date");
        fileDate.setPrefWidth(300);
        fileDate.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("fileDate"));

        TableColumn<FileInfo, Long> fileSize = new TableColumn<>("Size");
        fileSize.setPrefWidth(80);
        fileSize.setCellValueFactory(new PropertyValueFactory<FileInfo, Long>("fileSize"));

        filesView.getColumns().addAll(fileName, fileType, fileDate, fileSize);

        try {
            manager = new Manager();
        }
        catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Server connection error");
            alert.showAndWait();
        }
        catch (LoggingException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Server connection error");
            alert.showAndWait();
        }
        refreshFilesView(filesView);

        Button uploadButton = new Button("Upload file");
        uploadButton.setPrefWidth(100);

        Button addFolderButton = new Button("Create folder");
        addFolderButton.setPrefWidth(100);

        Button downloadButton = new Button("Download");
        downloadButton.setPrefWidth(100);

        Button deleteButton = new Button("Delete");
        deleteButton.setPrefWidth(100);

        Button renameButton = new Button("Rename");
        renameButton.setPrefWidth(100);

        menuBox.setSpacing(10);
        menuBox.getChildren().addAll(uploadButton, addFolderButton, downloadButton, renameButton, deleteButton);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        root.getChildren().addAll(filesView, menuBox);

        filesView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    String file = filesView.getSelectionModel().getSelectedItem().getFileName();
                    try {
                        manager.setCurrDir(file);
                    }
                    catch (IOException e) {}
                    refreshFilesView(filesView);
                }
            }
        });

        uploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialDirectory(new File("D:/"));
                    fileChooser.setTitle("Select file for upload");
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        String basename = file.getName();
                        if (manager.uploadFile(basename, file.toString())) {
                            refreshFilesView(filesView);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "File " + basename + " was succesfully upload");
                            alert.showAndWait();
                        }
                        else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't upload file " + basename);
                            alert.showAndWait();
                        }
                    }
                }
                catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Server error. Please restart client");
                    alert.showAndWait();
                }
            }
        });

        addFolderButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ActionWindow actionWindow = new ActionWindow("Add folder", "Enter folder name", "Add");
                if (!selection.contains(".")) {
                    try {
                        if (manager.createDir(selection)) {
                            refreshFilesView(filesView);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Folder " + selection + " was succesfully created");
                            alert.showAndWait();
                        }
                        else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't create folder " + selection);
                            alert.showAndWait();
                        }
                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Server error. Please restart client");
                        alert.showAndWait();
                    }
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Folder name can't contain .");
                    alert.showAndWait();
                }
            }
        });

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String name = filesView.getSelectionModel().getSelectedItem().getFileName();
                if (name != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + name);
                    Optional<ButtonType> choice = alert.showAndWait();
                    try {
                        if (choice.get() == ButtonType.OK) {
                            if (manager.remove(name)) {
                                refreshFilesView(filesView);
                                alert = new Alert(Alert.AlertType.INFORMATION, name + " was successfully deleted");
                                alert.showAndWait();
                            } else {
                                alert = new Alert(Alert.AlertType.ERROR, "Can't delete " + selection);
                                alert.showAndWait();
                            }
                        }
                    } catch (IOException e) {
                        alert = new Alert(Alert.AlertType.ERROR, "Server error. Please restart client");
                        alert.showAndWait();
                    }
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nothing selected");
                    alert.showAndWait();
                }
            }
        });

        renameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ActionWindow actionWindow = new ActionWindow("Rename", "Enter new name", "Rename");
                String name = filesView.getSelectionModel().getSelectedItem().getFileName();
                if (name != null) {
                    try {
                        if (manager.rename(name, selection)) {
                            refreshFilesView(filesView);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, name + " was successfully renamed");
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't rename " + name);
                            alert.showAndWait();
                        }
                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Server error. Please restart client");
                        alert.showAndWait();
                    }
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nothing selected");
                    alert.showAndWait();
                }
            }
        });

        downloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File("D:/"));
                fileChooser.setTitle("Select file for download");
                File file = fileChooser.showSaveDialog(primaryStage);
                String name = filesView.getSelectionModel().getSelectedItem().getFileName();
                if (file.getName().contains(".")) {
                    if (file != null) {
                        try {
                            if (manager.download(name, file.toString())) {
                                refreshFilesView(filesView);
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Was successfully downloaded to " + file.toString());
                                alert.showAndWait();
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR, "Can't download to " + name);
                                alert.showAndWait();
                            }
                        } catch (IOException e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Server error. Please restart client");
                            alert.showAndWait();
                        }
                    }
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "You can download only files");
                    alert.showAndWait();
                }
            }
        });

        Scene mainScene = new Scene(root, 760, 500);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("FTP Client");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
