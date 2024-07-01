package loc.ex.symphony.ui;


import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.file.BookmarksSerializer;
import loc.ex.symphony.file.FileAdapter;
import loc.ex.symphony.file.FileResaver;
import loc.ex.symphony.indexdata.*;
import loc.ex.symphony.listview.*;
import loc.ex.symphony.search.Cutser;
import loc.ex.symphony.search.Searcher;

import org.fxmisc.richtext.StyleClassedTextArea;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class MainController {

    public StyleClassedTextArea mainTextArea;
    public GridPane mainGridPane;
    public Menu bookmarksMenu;
    public Tab ellenTab;
    public Tab bibleLinkTab;
    public Tab ellenLinkTab;
    private Searcher b_searcher;
    private Searcher e_searcher;

    public TextField searchByTextField;
    public Button searchButton;
    public TextField searchByNameField;
    public TextField searchByLinkField;
    public ListView<Integer> chapterListView;
    public ListView<Book> bibleListView;
    public ListView<Book> ellenListView;
    public ListView<Link> bibleLinkView;
    public ListView<Link> ellenLinkView;
    public Tab bibleTab;

    private Book selectedBook;
    private Chapter selectedChapter;

    private HashMap<Integer, String> b_uniqueWord = new HashMap<>();
    private HashMap<Integer, String> e_uniqueWord = new HashMap<>();

    public static AtomicReference<Double> currentWindowWidth = new AtomicReference<>(0d);
    public static AtomicReference<Double> currentWindowHeight = new AtomicReference<>(0d);

    public static BookmarksWindow bookmarksWindow;

    static {
        try {
            bookmarksWindow = new BookmarksWindow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SimpleStringProperty listener = new SimpleStringProperty();
    public static SimpleStringProperty usabilityButtonListener = new SimpleStringProperty();
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public MainController() {
    }

    public void initialize() throws IOException {

        b_uniqueWord = IndexSaverSingleThreaded.loadUniqueWords(PathsEnum.Bible);
        e_uniqueWord = IndexSaverSingleThreaded.loadUniqueWords(PathsEnum.EllenWhite);

        initializeMainTextArea();

        initializeHoverSelectionPanel();

        initializeBookFiles__OnAction();

        selectedBibleList__OnAction();
        selectedEllenList__OnAction();

        selectedChapterList__OnAction();

        selectedBookLink__OnAction();

        Platform.runLater(this::initializeSceneHandler);

        bibleListView.setCellFactory(param -> new RichCell<>());
        bibleLinkView.setCellFactory(param -> new RichCell<>());
        ellenListView.setCellFactory(param -> new RichCell<>());
        ellenLinkView.setCellFactory(param -> new RichCell<>());


        logger.info("resource is set");

        listener.addListener(listener -> {
            logger.info("IS CHANGED!");
            ListView<Book> selectedResource = bibleTab.isSelected()? bibleListView: ellenTab.isSelected()? ellenListView: bibleListView;
            HashMap<Integer, String> uniqueWords = bibleTab.isSelected()? b_uniqueWord: ellenTab.isSelected()? e_uniqueWord: b_uniqueWord;
            selectedResource.getSelectionModel().select(BookmarksController.selectedBookmark.link().getReferences().getFirst().getBookID());
            selectedResource.scrollTo(BookmarksController.selectedBookmark.link().getReferences().getFirst().getBookID());

            chapterListView.getSelectionModel().select(BookmarksController.selectedBookmark.link().getReferences().getFirst().getChapterID()-1);
            chapterListView.scrollTo(BookmarksController.selectedBookmark.link().getReferences().getFirst().getChapterID());

            highlightText(BookmarksController.selectedBookmark.link().getReferences(), new String[] {

                    uniqueWords.get(BookmarksController.selectedBookmark.link().getReferences().getFirst().getWordKey())

            });
        });

        searchButton.setDisable(true);

        usabilityButtonListener.addListener(listener -> {
            searchButton.setDisable(false);
        });


        b_searcher = new Searcher(PathsEnum.Bible, b_uniqueWord);
        b_searcher.setResource(bibleListView.getItems());
        e_searcher = new Searcher(PathsEnum.EllenWhite, e_uniqueWord);
        e_searcher.setResource(ellenListView.getItems());

        mainTextArea.editableProperty().set(false);
    }

    private void initializeSearchByLink() {
        searchByLinkField.textProperty().addListener(action -> {
            if (bibleLinkTab.isSelected()) {

            }
        });
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

    private void initializeHoverSelectionPanel() {
        HBox hoverSelectionPanel = new HBox();
        hoverSelectionPanel.setVisible(false);
        hoverSelectionPanel.setMaxHeight(30);
        hoverSelectionPanel.setMaxWidth(200);
        hoverSelectionPanel.getStyleClass().add("hover_selection_panel");

        mainGridPane.getChildren().add(hoverSelectionPanel);
        initializeHoverSelectionPanelCopyButton(hoverSelectionPanel);

        GridPane.setColumnIndex(hoverSelectionPanel, 0);
        GridPane.setColumnSpan(hoverSelectionPanel, 3);
        GridPane.setRowIndex(hoverSelectionPanel, 0);
        GridPane.setRowSpan(hoverSelectionPanel, 4);
        GridPane.setValignment(hoverSelectionPanel, VPos.TOP);
        GridPane.setHalignment(hoverSelectionPanel, HPos.LEFT);
        initializeHoverSelectionPanelBehavior(hoverSelectionPanel);
    }

    private void initializeHoverSelectionPanelCopyButton(HBox hoverSelectionPanel) {

        Button copyButton = new Button();
        copyButton.setText("");
        copyButton.setPrefWidth(26);
        copyButton.setPrefHeight(26);
        HBox.setMargin(copyButton, new Insets(0, 0, 1, 3));
        copyButton.getStyleClass().add("copy_button");
        hoverSelectionPanel.alignmentProperty().set(Pos.CENTER_LEFT);
        hoverSelectionPanel.getChildren().add(copyButton);
        initializeHoverSelectionPanelCopyButtonBehavior(copyButton);

    }

    private void initializeHoverSelectionPanelCopyButtonBehavior(Button copyButton) {

        copyButton.onActionProperty().set(action -> {
            if (!mainTextArea.getSelectedText().isEmpty()) {

                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(mainTextArea.getSelectedText());
                clipboard.setContent(content);

            }
        });

    }

    private void initializeHoverSelectionPanelBehavior(HBox hoverSelectionPanel) {
        AtomicReference<Double> deltaX = new AtomicReference<>(0d);
        AtomicReference<Double> deltaY = new AtomicReference<>(0d);

        mainTextArea.setOnMousePressed(mouseEvent -> {
            deltaX.set(mouseEvent.getScreenX() - mouseEvent.getSceneX());
            deltaY.set(mouseEvent.getScreenY() - mouseEvent.getSceneY());
        });


        mainTextArea.selectionProperty().addListener((ov, i1, i2) -> {
            if (i1.getStart() != i1.getEnd() && !mainTextArea.getSelectedText().isEmpty()) {
                hoverSelectionPanel.setVisible(true);
                Bounds bounds = mainTextArea.getCharacterBoundsOnScreen(i1.getStart(), i1.getStart()+2).orElse(null);

                if (bounds != null) {
                    hoverSelectionPanel.translateXProperty().set(bounds.getMinX() - deltaX.get());
                    hoverSelectionPanel.translateYProperty().set(bounds.getMinY() - deltaY.get() - hoverSelectionPanel.getMaxHeight());

                    if (hoverSelectionPanel.translateXProperty().get() + hoverSelectionPanel.getMaxWidth()
                    > currentWindowWidth.get()-200) {
                        hoverSelectionPanel.translateXProperty().set(currentWindowWidth.get()-200-hoverSelectionPanel.getMaxWidth());
                    }

                }
            }
            if (mainTextArea.getSelectedText().isEmpty()) {
                hoverSelectionPanel.setVisible(false);
            }
        });
    }

    private void initializeBookFiles__OnAction() throws IOException {
        bibleListView.setItems(new FileAdapter().getBible());
        ellenListView.setItems(new FileAdapter().getEllen());
    }

    private void selectedBookLink__OnAction() {

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

        ellenLinkView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
            if (_new != null) {
                List<IndexStruct> selectedReferences = _new.getReferences();
                ellenListView.getSelectionModel().select(selectedReferences.get(0).getBookID());
                ellenListView.scrollTo(selectedReferences.get(0).getBookID());

                chapterListView.getSelectionModel().select(selectedReferences.get(0).getChapterID()-1);
                chapterListView.scrollTo(selectedReferences.get(0).getChapterID());

                highlightText(selectedReferences, _new.getWords());
            }
        });

    }

    private void highlightText(List<IndexStruct> selectedReferences, String[] words) {
        HashMap<Integer, String> uniqueWords = bibleLinkTab.isSelected()? b_uniqueWord: ellenLinkTab.isSelected()? e_uniqueWord: b_uniqueWord;

        String mainText = mainTextArea.getText();

        int positionCarret = 0;

        selectedReferences.sort(Comparator.comparingInt(IndexStruct::getPosition));

        for (int i = 0; i < selectedReferences.size(); i++) {

            Chapter chapter = selectedBook.getChapters().get(selectedReferences.get(i).getChapterID());

            int start = 0;
            String word = uniqueWords.get(selectedReferences.get(i).getWordKey());

            for (int j = 0; j < selectedReferences.get(i).getFragmentID(); j++) start += chapter.getFragments().get(j).length();

            start += selectedReferences.get(i).getPosition();
            int end = start + uniqueWords.get(selectedReferences.get(i).getWordKey()).length();


            //[SOLVED?] Проблема: иногда(может даже часто) не может найти в тексте искомое слово, из-за чего упирается в конец строки и выдаёт исключение
            while (!mainText.substring(start, end).equalsIgnoreCase(word)) {
                start++;
                end++;
            }

            if (positionCarret == 0) positionCarret = start;
            mainTextArea.setStyleClass(start, end, "ftext");

        }

        mainTextArea.moveTo(positionCarret);
        mainTextArea.requestFollowCaret();
    }

    private void selectedChapterList__OnAction() {
        chapterListView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
            if (_new != null) {
                selectedChapter = selectedBook.getChapters().get(_new);
                mainTextArea.clear();
                mainTextArea.setStyleClass(0, 0, "jtext");
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
                selectedBook = _new;
                setChapterListView(_new);
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

    public void doIndex__OnAction() throws IOException, SQLException, ClassNotFoundException {
        IndexatorSingleThreaded indexator = new IndexatorSingleThreaded(bibleListView.getItems());

        indexator.index();

        IndexSaverSingleThreaded.saveUniqueWords(indexator.getUniqueWords(), PathsEnum.Bible);
        IndexSaverSingleThreaded.save(indexator.getIndexData(), PathsEnum.Bible);

        /*for (var key : indexator.getDictionary().keySet()) {
            IndexSaverSingleThreaded.save(indexator.getDictionary().get(key), PathsEnum.Bible, String.valueOf(key));
        }*/

        //IndexSaverSingleThreaded.save(indexator.getIndexData(), PathsEnum.Bible);


        indexator = new IndexatorSingleThreaded(ellenListView.getItems());

        indexator.index();

        IndexSaverSingleThreaded.saveUniqueWords(indexator.getUniqueWords(), PathsEnum.EllenWhite);
        IndexSaverSingleThreaded.save(indexator.getIndexData(), PathsEnum.EllenWhite);

       /* for (var key : indexator.getDictionary().keySet()) {
            IndexSaverSingleThreaded.save(indexator.getDictionary().get(key), PathsEnum.EllenWhite, String.valueOf(key));
        }*/

        //IndexSaverSingleThreaded.save(indexator.getIndexData(), PathsEnum.EllenWhite);
    }

    public void BookmarksMenu_Click() {
        bookmarksWindow.GetStage().show();
    }

    public void CreateBookmark__OnAction() {
        if (!mainTextArea.getSelectedText().isEmpty()) {
            List<IndexStruct> refs = new ArrayList<>();
            ListView<Book> selectedResource = bibleTab.isSelected()? bibleListView: ellenTab.isSelected()? ellenListView: bibleListView;

            IndexStruct indexStruct = GetBookmarkIndexStruct(selectedResource);

            refs.add(indexStruct);

            Link link = new Link(refs, selectedResource.getItems(), mainTextArea.getSelectedText());

            Cutser cutser = new Cutser();

            String nameBookmark = STR."\{cutser.GetBibleCut(selectedResource.getSelectionModel().getSelectedIndex())} \{chapterListView.getSelectionModel().getSelectedIndex() + 1}:\{indexStruct.getFragmentID() + 1}  \{mainTextArea.getSelectedText()}";

            Bookmark bookmark = new Bookmark(nameBookmark, link, new Date(System.currentTimeMillis()));

            BookmarksController.bookmarks.add(bookmark);

            BookmarksSerializer.Serialize(BookmarksController.bookmarks);

            /*Реализовать:
                - нэйминг ссылок[done],
                - сериализация закладок[done],
                - десериализация закладок[done],
                - при открытии приложения создание окна закладок[done],
                - открытие закладок

            Последовательность создания закладок:
                << Десериализация уже существующих закладок в лист *x([десериализация закладок])
                Создание ссылки на закладку([нэйминг ссылок]) > помещается в спец. класс "закладка" > "закладка" помещается в лист *x > лист *x сериализуется([сериализация закладок])
                >> [открытие закладок]
             */

        }
    }

    @NotNull
    private IndexStruct GetBookmarkIndexStruct(ListView<Book> selectedResource) {
        IndexStruct indexStruct = new IndexStruct();
        int currentFragmentId = 0;
        int position = mainTextArea.getSelection().getStart();

        for (; currentFragmentId < selectedChapter.getFragments().size(); currentFragmentId++) {
            if (position - selectedChapter.getFragments().get(currentFragmentId).length() < 0) {
                break;
            } else position -= selectedChapter.getFragments().get(currentFragmentId).length();
        }

        indexStruct.setBookID(selectedResource.getSelectionModel().getSelectedIndex());
        indexStruct.setChapterID(chapterListView.getSelectionModel().getSelectedIndex()+1);
        indexStruct.setFragmentID(currentFragmentId);
        indexStruct.setPosition(position);
        //indexStruct.setWord(mainTextArea.getSelectedText());
        //indexStruct.setWordLength(mainTextArea.getSelectedText().length());

        logger.info(STR."\{indexStruct.getBookID()}\\\{indexStruct.getChapterID()}\\\{indexStruct.getFragmentID()}\\\{indexStruct.getPosition()}\\\{b_uniqueWord.get(indexStruct.getWordKey())}");

        return indexStruct;
    }

    public void loadIndex__OnAction() {

    }

    public void selectTabBible__OnAction() {

     }

    public void selectTabEllen__OnAction() {

    }

    public void doSearch__OnAction() {
        String prompt = searchByTextField.getText();
        if (!prompt.isEmpty()) {
            if (bibleTab.isSelected()) {
                FilteredList<Link> filteredList = new FilteredList<>(b_searcher.search(prompt, PathsEnum.Bible), p -> true);
                searchByLinkField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredList.setPredicate(data -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String[] findTexts = newValue.toLowerCase().split(" ");

                        return checkContains(data.getLinkContent().toLowerCase(), findTexts);
                    });
                });
                bibleLinkView.setItems(filteredList);
            }
            if (ellenTab.isSelected()) {
                FilteredList<Link> filteredList = new FilteredList<>(e_searcher.search(prompt, PathsEnum.EllenWhite), p -> true);
                searchByLinkField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredList.setPredicate(data -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String[] findTexts = newValue.toLowerCase().split(" ");

                        return checkContains(data.getLinkContent().toLowerCase(), findTexts);
                    });
                });
                ellenLinkView.setItems(filteredList);
            }


        }
    }

    private boolean checkContains(String str, String[] arr) {
        for (String text : arr) {
            if (!str.contains(text)) return false;
        }

        return true;
    }
}