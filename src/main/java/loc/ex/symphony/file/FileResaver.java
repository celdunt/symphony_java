package loc.ex.symphony.file;


import loc.ex.symphony.listview.PathsEnum;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileResaver {

    private final List<File> files;

    private Path biblePath;
    private Path ellenPath;

    private final PathsEnum pathRoot;

    public FileResaver(List<File> files, PathsEnum pathRoot) throws IOException {
        this.files = files;
        this.pathRoot = pathRoot;

        initializePaths();
        resave();
    }

    private void initializePaths() throws IOException {
        this.biblePath = Paths.get("bible");
        this.ellenPath = Paths.get("ellen");

        Files.createDirectories(biblePath);
        Files.createDirectories(ellenPath);
    }


    private void resave() throws IOException {
        if (files == null) return;
        Path path = null;
        if (pathRoot.equals(PathsEnum.Bible)) path = biblePath;
        else if (pathRoot.equals(PathsEnum.EllenWhite)) path = ellenPath;


        for (File file : files) {
            String fileText;
            try {
                fileText = String.join("\n", Files.readAllLines(file.toPath(), Charset.forName("windows-1251")));
            } catch (IOException exception) {
                fileText = String.join("\n", Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
            }
            Path filePath = path.resolve(file.getName());
            Files.write(filePath, fileText.getBytes());
        }
    }
}
