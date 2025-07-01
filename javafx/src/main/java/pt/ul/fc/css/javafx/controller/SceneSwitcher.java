package pt.ul.fc.css.javafx.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class SceneSwitcher {

    public static <T> T switchScene(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent viewRoot = loader.load();
        Scene newScene = new Scene(viewRoot);

        newScene.getStylesheets().add(SceneSwitcher.class.getResource("/styles.css").toExternalForm());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        boolean wasFullScreen = stage.isFullScreen();
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();

        stage.setScene(newScene);
        stage.setFullScreen(wasFullScreen);

        if (!wasFullScreen) {
            stage.setWidth(Math.max(currentWidth, viewRoot.minWidth(-1)));
            stage.setHeight(Math.max(currentHeight, viewRoot.minHeight(-1)));
            stage.centerOnScreen();
        }

        stage.show();

        return loader.getController();
    }
}
