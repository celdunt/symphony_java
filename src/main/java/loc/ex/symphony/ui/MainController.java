package loc.ex.symphony.ui;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.file.FileAdapter;
import loc.ex.symphony.file.FileResaver;
import loc.ex.symphony.indexdata.IndexSaver;
import loc.ex.symphony.indexdata.IndexStruct;
import loc.ex.symphony.indexdata.Indexator;
import loc.ex.symphony.listview.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class MainController {


    public TextField searchByTextField;
    public Button searchButton;
    public TextField searchByNameField;
    public TextField searchByLinkField;
    public ListView<Integer> chapterListView;
    public ListView<Book> bibleListView;
    public ListView<Book> ellenListView;
    public ListView<Book> bibleLinkView;
    public ListView<Book> ellenLinkView;
    public TextArea mainTextField;

    private Book selectedBook;

    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void initialize() throws IOException {
        initializeBookFiles__OnAction();

        selectedBibleList__OnAction();
        selectedEllenList__OnAction();

        selectedChapterList__OnAction();

    }

    private void initializeBookFiles__OnAction() throws IOException {
        bibleListView.setItems(new FileAdapter().getBible());
        ellenListView.setItems(new FileAdapter().getEllen());
    }

    private void selectedChapterList__OnAction() {
        chapterListView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
            if (_new != null) {
                mainTextField.setText(selectedBook.getChapters().get(_new).getEntireText());
            }
        });
    }

    private void selectedBibleList__OnAction() {
        bibleListView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
            if (_new != null) {
                setChapterListView(_new);

                selectedBook = _new;
            }
        });
    }

    private void setChapterListView(Book _new) {
        ObservableList<Integer> chapterList = FXCollections.observableArrayList();
        chapterList.addAll(_new.getChapters().stream().map(x -> x.number.get()).toList());
        chapterList.remove(chapterList.size()-1);
        chapterListView.setItems(chapterList);
    }

    private void selectedEllenList__OnAction() {
        ellenListView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
            if (_new != null) {
                setChapterListView(_new);

                selectedBook = _new;
            }
        });
    }

    private List<File> selectFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("text files (*.txt, *.docx)", "*.txt", "*.docx"));
        return fileChooser.showOpenMultipleDialog(Symphony.window);
    }

    public void openBibleFiles__onAction() {
        List<File> files = selectFiles();
        try {
            new FileResaver(files, PathsEnum.Bible);

            initializeBookFiles__OnAction();
        } catch (IOException exception) {
            logger.info(exception.toString());
        }
    }

    public void openEllenFiles__onAction() {
        List<File> files = selectFiles();
        try {
            new FileResaver(files, PathsEnum.EllenWhite);

            initializeBookFiles__OnAction();
        } catch (IOException exception) {
            logger.info(exception.toString());
        }
    }

    public void doIndex__OnAction() throws IOException {
        Indexator indexator = new Indexator(bibleListView.getItems());

        indexator.index();

        IndexSaver.save(indexator.getIndexData());
    }

    public void loadIndex__OnAction() {
        ConcurrentHashMap<String, List<IndexStruct>> indexData = IndexSaver.load();

        List<IndexStruct> dataOfWord = indexData.get("послание");

        if (!dataOfWord.isEmpty()) {
            IndexStruct first = dataOfWord.get(0);
            bibleListView.getSelectionModel().select(first.getBookId());
            chapterListView.getSelectionModel().select(first.getChapterId()-1);
            int start = 0;
            for (int i = 0; i < first.getFragmentId(); i++) {
                start += bibleListView.getItems().get(first.getBookId()).getChapters().get(first.getChapterId()).getFragments().get(i).length();
            }
            mainTextField.selectRange(start + first.getPosition(), start + first.getPosition() + first.getWordLength());
        }
    }
}