package loc.ex.symphony;

import loc.ex.symphony.ui.UpdaterWindow;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Updater {

    private static final String REPO_API_URL = "https://api.github.com/repos/celdunt/symphony_java/releases/latest";
    private static String CURRENT_VERSION;

    private static final String NEW_EXE_NAME = "latest.exe";
    private static final String EXE_NAME = "Твоя Симфония.exe";
    private static final String UPDATE_SCRIPT = "update.bat";

    private static String loadCurrentVersion() throws IOException {
        Path currentVersion = Path.of("cur-v.txt");
        if (Files.exists(currentVersion)) {
            return Files.readString(currentVersion);
        } else return "v0.0.1";
    }

    public static boolean thereNewUpdates() throws IOException {
        CURRENT_VERSION = loadCurrentVersion();

        HttpURLConnection conn = (HttpURLConnection) new URL(REPO_API_URL).openConnection();
        conn.setRequestMethod("GET");
        try (InputStream is = conn.getInputStream()) {
            String response = new String(is.readAllBytes());
            JSONObject jsonResponse = new JSONObject(response);
            String latestVersion = jsonResponse.getString("tag_name");
            boolean returned = !CURRENT_VERSION.equals(latestVersion);
            CURRENT_VERSION = latestVersion;
            return returned;
        }

    }

    private static void download(String fileURL, String savePath, UpdaterWindow window) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(fileURL).openConnection();

        int fileSize = connection.getContentLength();

        System.err.println(fileSize + "  ЕБАТЬ");

        try (InputStream in = new BufferedInputStream(connection.getInputStream());
             FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;

            long totalBytesRead = 0;
            long startTime = System.currentTimeMillis();
            long lastUpdateTime = System.currentTimeMillis();

            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                double progress = (double) totalBytesRead / fileSize;
                long elapsedTime = System.currentTimeMillis() - startTime;
                double downloadSpeed = totalBytesRead / (elapsedTime / 1000d) / (1024 * 1024);

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastUpdateTime >= 1000) { // 1000 мс = 1 секунда
                    window.updateProgress(progress, downloadSpeed);
                    lastUpdateTime = currentTime; // Обновляем время последнего обновления
                }
            }
        } catch (IOException exception) {
            System.err.println("Какая-то Оёшибка");
        } finally {
            connection.disconnect();
        }
    }

    private static void updateCurrentVersionInfo() throws IOException {

        Files.write(Path.of("cur-v.txt"), CURRENT_VERSION.getBytes());

    }

    public static void updateApp(UpdaterWindow window) throws IOException {
        URL url = new URL(String.format("https://github.com/celdunt/symphony_java/releases/download/%s/latest.exe", CURRENT_VERSION));
        download(url.toString(), "latest.exe", window);
    }

    public static void endPartUpdation() throws IOException {
        updateCurrentVersionInfo();
        createUpdateScript();
        Runtime.getRuntime().exec("cmd /c start " + UPDATE_SCRIPT);
        System.exit(0);
    }

    private static void createUpdateScript() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(UPDATE_SCRIPT))) {
            writer.write("timeout /t 2\n");
            writer.write("del \"" + EXE_NAME + "\"\n");
            writer.write("rename \"" + NEW_EXE_NAME + "\" \"" + EXE_NAME + "\"\n");
            writer.write("start \"\" \"" + EXE_NAME + "\"\n");
            writer.write("del \"" + UPDATE_SCRIPT + "\"\n");
        }
    }

}
