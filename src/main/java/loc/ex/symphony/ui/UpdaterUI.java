package loc.ex.symphony.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;

import java.io.IOException;

public class UpdaterUI {

    private UpdaterWindow controller;

    public UpdaterUI show() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/download-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 120);
        Stage stage = new Stage();
        stage.setTitle("Загрузка обновления");
        stage.setScene(scene);
        stage.show();

        controller = fxmlLoader.getController();


        return this;

    }


    public UpdaterWindow getController() {
        return controller;
    }
}
