package loc.ex.symphony;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import loc.ex.symphony.ui.MainController;

import java.io.IOException;

public class Symphony extends Application {
    public static Stage window;
    public static Scene scene;

    @Override
    public void start(Stage window) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/main-window_design.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 450);
        Symphony.window = window;
        Symphony.scene = scene;

        MainController.currentWindowWidth.set(window.getWidth());
        MainController.currentWindowHeight.set(window.getHeight());

        window.widthProperty().addListener(lis -> {
            MainController.currentWindowWidth.set(window.getWidth());
            MainController.currentWindowHeight.set(window.getHeight());
        });

        window.setTitle("Symphony");
        window.setScene(scene);
        window.show();
    }

    public static void main(String[] args) {
        launch();
    }
}