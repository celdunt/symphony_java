package loc.ex.symphony.ui;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.file.FileAdapter;
import loc.ex.symphony.file.FileResaver;
import loc.ex.symphony.indexdata.IndexSaver;
import loc.ex.symphony.indexdata.IndexStruct;
import loc.ex.symphony.indexdata.Indexator;
import loc.ex.symphony.listview.*;
import loc.ex.symphony.search.Searcher;

import org.fxmisc.richtext.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class MainController {

    public StyleClassedTextArea mainTextArea;
    public GridPane mainGridPane;
    public Menu bookmarksMenu;
    private Searcher searcher;

    public TextField searchByTextField;
    public Button searchButton;
    public TextField searchByNameField;
    public TextField searchByLinkField;
    public ListView<Integer> chapterListView;
    public ListView<Book> bibleListView;
    public ListView<Book> ellenListView;
    public ListView<Link> bibleLinkView;
    public ListView<Book> ellenLinkView;
    public Tab bibleTab;

    private Book selectedBook;
    private Chapter selectedChapter;

    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void initialize() throws IOException {

        initializeMainTextArea();

        initializeBookFiles__OnAction();

        selectedBibleList__OnAction();
        selectedEllenList__OnAction();

        selectedChapterList__OnAction();

        selectedBibleLink__OnAction();

        Platform.runLater(this::initializeSceneHandler);

        bibleLinkView.setCellFactory(param -> new LinkCell<>());

        searcher = new Searcher();
    }

    private void initializeSceneHandler() {
        Set<KeyCode> pressedKeys = new HashSet<>();

        Symphony.scene.setOnKeyPressed(e -> {
            if (mainTextArea.isFocused()) {
                if (e.getCode().getName().equals("Ctrl")) {
                    pressedKeys.add(e.getCode());
                }
                if (e.getCode().getName().equals("C")) {
                    pressedKeys.add(e.getCode());
                }
                if (pressedKeys.size() > 1) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(mainTextArea.getSelectedText());
                    clipboard.setContent(content);

                    pressedKeys.clear();
                }
            }
        });

    }

    private void initializeMainTextArea() {
        mainTextArea = new StyleClassedTextArea();

        mainTextArea.setWrapText(true);

        mainGridPane.getChildren().add(mainTextArea);
        GridPane.setColumnIndex(mainTextArea, 1);
        GridPane.setRowIndex(mainTextArea, 3);
        GridPane.setHgrow(mainTextArea, Priority.ALWAYS);
        GridPane.setVgrow(mainTextArea, Priority.ALWAYS);
        GridPane.setMargin(mainTextArea, new Insets(3, 0, 0, 0));
    }

    private void initializeBookFiles__OnAction() throws IOException {
        bibleListView.setItems(new FileAdapter().getBible());
        //ellenListView.setItems(new FileAdapter().getEllen());
    }

    private void selectedBibleLink__OnAction() {

        bibleLinkView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
            if (_new != null) {
                List<IndexStruct> selectedReferences = _new.getReferences();
                bibleListView.getSelectionModel().select(selectedReferences.get(0).getBookID());
                bibleListView.scrollTo(selectedReferences.get(0).getBookID());

                chapterListView.getSelectionModel().select(selectedReferences.get(0).getChapterID()-1);
                chapterListView.scrollTo(selectedReferences.get(0).getChapterID());

                highlightText(selectedReferences, _new.getWords());
            }
        });

    }

    private void highlightText(List<IndexStruct> selectedReferences, String[] words) {
        String mainText = mainTextArea.getText();

        int x = 0;
        int positionCarret = 0;

        selectedReferences.sort(Comparator.comparingInt(IndexStruct::getPosition));

        for (int i = 0; i < selectedReferences.size(); i++) {

            Chapter chapter = selectedBook.getChapters().get(selectedReferences.get(i).getChapterID());

            int start = 0;
            String word = selectedReferences.get(i).getWord();

            for (int j = 0; j < selectedReferences.get(i).getFragmentID(); j++) start += chapter.getFragments().get(j).length();

            start += selectedReferences.get(i).getPosition();
            int end = start + selectedReferences.get(i).getWordLength();


            //[SOLVED?] Проблема: иногда(может даже часто) не может найти в тексте искомое слово, из-за чего упирается в конец строки и выдаёт исключение
            while (!mainText.substring(start, end).equalsIgnoreCase(word)) {
                start++;
                end++;
            }

            if (positionCarret == 0) positionCarret = start;
            mainTextArea.setStyleClass(start, end, "ftext");

            x = end;
        }

        mainTextArea.moveTo(positionCarret);
        mainTextArea.requestFollowCaret();
    }

    private void selectedChapterList__OnAction() {
        chapterListView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
            if (_new != null) {
                selectedChapter = selectedBook.getChapters().get(_new);
                mainTextArea.clear();
                mainTextArea.insertText(0, selectedBook.getChapters().get(_new).getEntireText());

                mainTextArea.moveTo(0);
                mainTextArea.requestFollowCaret();
            }
        });
    }

    private void selectedBibleList__OnAction() {
        bibleListView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
            if (_new != null) {
                selectedBook = _new;
                setChapterListView(_new);
            }
        });
    }

    private void setChapterListView(Book _new) {
        ObservableList<Integer> chapterList = FXCollections.observableArrayList();
        chapterList.addAll(_new.getChapters().stream().map(x -> x.number.get()).toList());
        chapterList.remove(chapterList.size()-1);
        chapterListView.setItems(chapterList);

        chapterListView.getSelectionModel().select(0);
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

    public void BookmarksMenu_Click() throws IOException {
        BookmarksWindow.initAndShow();
    }

    public void CreateBookmark__OnAction() {
        if (!mainTextArea.getSelectedText().isEmpty()) {
            List<IndexStruct> refs = new ArrayList<>();

            IndexStruct indexStruct = new IndexStruct();
            int currentFragmentId = 0;
            int position = mainTextArea.getSelection().getStart();

            for (; currentFragmentId < selectedChapter.getFragments().size(); currentFragmentId++) {
                if (position - selectedChapter.getFragments().get(currentFragmentId).length() < 0) {
                    currentFragmentId--;
                    break;
                } else position -= selectedChapter.getFragments().get(currentFragmentId).length();
            }

            indexStruct.setBookID(bibleListView.getSelectionModel().getSelectedIndex());
            indexStruct.setChapterID(chapterListView.getSelectionModel().getSelectedIndex());
            indexStruct.setFragmentID(currentFragmentId);
            indexStruct.setPosition(position);
            indexStruct.setWord(mainTextArea.getSelectedText());
            indexStruct.setWordLength(mainTextArea.getSelectedText().length());

            refs.add(indexStruct);

            Link link = new Link(refs, (ObservableList<Book>) bibleLinkView, mainTextArea.getSelectedText());
        }
    }

    public void loadIndex__OnAction() {

    }

    public void selectTabBible__OnAction() {
        if (bibleTab.isSelected() && searcher != null) {
            searcher.setResource(bibleListView.getItems());

            logger.info("resource is set");
        }
     }

    public void doSearch__OnAction() {
        String prompt = searchByTextField.getText();
        if (!prompt.isEmpty()) {
            bibleLinkView.setItems(searcher.search(prompt));
        }
    }
}