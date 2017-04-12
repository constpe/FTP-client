import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ActionWindow {
    public ActionWindow(String title, String description, String action) {
        VBox actionBox = new VBox();
        actionBox.setSpacing(5);
        actionBox.setPadding(new Insets(10));
        TextField fileField = new TextField();
        Button actionButton = new Button(action);

        actionBox.getChildren().addAll(new Label(description), fileField, actionButton);

        Scene actionScene = new Scene(actionBox, 260, 110);
        Stage actionStage = new Stage();
        actionStage.setScene(actionScene);
        actionStage.initStyle(StageStyle.UTILITY);
        actionStage.initModality(Modality.APPLICATION_MODAL);
        actionStage.setTitle(title);

        actionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MainView.selection = fileField.getText();
                actionStage.close();
            }
        });

        actionStage.showAndWait();
    }
}
