package loc.ex.symphony.ui;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.Updater;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class UpdaterWindow {
    public ProgressBar downloadProgressBar;
    public TextField downloadSpeedLabel;

    public void initialize() {
        downloadSpeedLabel.setText("Скорость загрузки: 0 мб/сек");
        downloadSpeedLabel.setDisable(true);
    }

    public void updateProgress(double progress, double speed) throws IOException {
        Task<Void> pg = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                downloadProgressBar.setProgress(progress);
                return null;
            }
        };
        Task<Void> sp = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                downloadSpeedLabel.setText(String.format("Скорость загрузки: %.2f мб/сек", speed));
                return null;
            }
        };
        new Thread(pg).start();
        new Thread(sp).start();
    }

}
