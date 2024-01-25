package loc.ex.symphony.file;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import loc.ex.symphony.listview.Bookmark;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BookmarksSerializer {

    public static void Serialize(ObservableList<Bookmark> bookmarks) {
        List<Bookmark> bookmarkList = bookmarks.stream().toList();
        try (FileOutputStream fileOut = new FileOutputStream("bookmarks.ol")) {
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(bookmarkList);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static ObservableList<Bookmark> Deserialize() {
        ObservableList<Bookmark> bookmarks = FXCollections.observableArrayList();

        if (!Files.exists(Path.of("bookmarks.ol"))) return bookmarks;

        try (FileInputStream fileIn = new FileInputStream("bookmarks.ol")) {
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            bookmarks = FXCollections.observableArrayList((List<Bookmark>) objIn.readObject());
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return bookmarks;
    }

}
