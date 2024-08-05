package loc.ex.symphony.file;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupManager {

    public void zip(File dir) {
        String zipName = String.format("backup-beodata-%s.zip", LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
        String[] toZip = new String[] {
                "articles.json", "bible-note-storage.json", "bible-parallels-links-storage.json",
                "bible-thelper-storage.json", "bookmarks.json", "ellen-note-storage.json",
                "ellen-parallels-links-storage.json", "ellen-thelper-storage.json",
                "other-note-storage.json", "other-parallels-links-storage.json", "other-thelper-storage.json"
        };

        try(FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath() + File.separator + zipName);
            ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (String file : toZip) {
                try(FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry ze = new ZipEntry(file);
                    zos.putNextEntry(ze);

                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) > 0) {
                        zos.write(bytes, 0, length);
                    }

                    zos.closeEntry();
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void unzip(File file) {
        if (!file.getName().contains("beodata")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка загрузки резервной копии данных!");
            alert.setHeaderText(null);
            alert.setContentText("Загружаемый архив не относится к данной программе!");
            alert.showAndWait();
            return;
        }
        try(ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                try(FileOutputStream fos = new FileOutputStream(fileName)) {
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = zis.read(bytes)) > 0) {
                        fos.write(bytes, 0, length);
                    }
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
