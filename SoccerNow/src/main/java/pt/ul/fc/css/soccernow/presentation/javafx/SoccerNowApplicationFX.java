package pt.ul.fc.css.soccernow.presentation.javafx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pt.ul.fc.css.soccernow.SoccerNowApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class SoccerNowApplicationFX extends Application {
    private static String[] savedArgs;
    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        savedArgs = args;
        Application.launch(SoccerNowApplicationFX.class, args);
    }

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(SoccerNowApplication.class, savedArgs);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/views/main_view.fxml"));
        loader.setControllerFactory(springContext::getBean);

        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/javafx/css/styles.css").toExternalForm());

        primaryStage.setTitle("SoccerNow FX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
    }
}
