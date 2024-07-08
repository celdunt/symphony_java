package loc.ex.symphony.ui;


import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.file.FileAdapter;
import loc.ex.symphony.file.FileResaver;
import loc.ex.symphony.indexdata.*;
import loc.ex.symphony.listview.*;
import loc.ex.symphony.search.*;

import org.fxmisc.richtext.StyleClassedTextArea;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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
    public TabPane bookTabPane;
    public Button eraseSearchByText;
    public Button eraseSearchByName;
    public Button eraseSearchByLink;
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

    public FilteredList<Link> filteredBibleList;
    public FilteredList<Link> filteredEllenList;
    public ObservableList<Link> obsBibleLink = FXCollections.observableArrayList();
    public ObservableList<Link> obsEllenLink = FXCollections.observableArrayList();

    public Tab bibleTab;
    private Book selectedBook;

    private HashMap<Integer, String> b_uniqueWord = new HashMap<>();
    private HashMap<Integer, String> e_uniqueWord = new HashMap<>();
    private HashMap<String, Integer> b_uniqueWordH = new HashMap<>();
    private HashMap<String, Integer> e_uniqueWordH = new HashMap<>();

    public static AtomicReference<Double> currentWindowWidth = new AtomicReference<>(0d);
    public static AtomicReference<Double> currentWindowHeight = new AtomicReference<>(0d);

    public BookmarksWindow bookmarksWindow;
    public ArticlesWindow articlesWindow;

    public static SimpleStringProperty listener = new SimpleStringProperty();
    public static SimpleStringProperty usabilityButtonListener = new SimpleStringProperty();
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static ObjectProperty<BookmarkStruct> openingBookmark = new SimpleObjectProperty<>();

    public MainController() {
    }

    public void initialize() throws IOException, URISyntaxException {

        initBookmarkWindow();
        initArticlesWindow();
        initUniqueWordsFields();
        MainTextAreaComponent.getInstance(this).initMainTextArea();
        LeafButtonComponent.getInstance(this).initLeafButtons();
        HoverPanelComponent.getInstance(this).initHoverPanel();
        initializeBookFiles__OnAction();
        selectedBibleList__OnAction();
        selectedEllenList__OnAction();
        selectedChapterList__OnAction();
        LinkComponent.getInstance(this).initLinkListSelection();
        EraseButtonComponent.getInstance(this).initEraseButtons();
        initOpenBookmarkAction();
        initSearchByLink();
        initSearchByLinkCut();
        Platform.runLater(this::initializeSceneHandler);
        bibleListView.setCellFactory(param -> new RichCell<>());
        bibleLinkView.setCellFactory(param -> new RichCell<>());
        ellenListView.setCellFactory(param -> new RichCell<>());
        ellenLinkView.setCellFactory(param -> new RichCell<>());
        logger.info("resource is set");
        searchButton.setDisable(true);
        usabilityButtonListener.addListener(listener -> {
            searchButton.setDisable(false);
        });
        b_searcher = new Searcher(PathsEnum.Bible, b_uniqueWord);
        b_searcher.setResource(bibleListView.getItems());
        e_searcher = new Searcher(PathsEnum.EllenWhite, e_uniqueWord);
        e_searcher.setResource(ellenListView.getItems());
        mainTextArea.editableProperty().set(false);
        selectTabBible__OnAction();
    }

    public void initBookmarkWindow() throws IOException {

        bookmarksWindow = new BookmarksWindow();

    }

    public void initArticlesWindow() throws IOException {
        articlesWindow = new ArticlesWindow();
    }

    public void initUniqueWordsFields() throws IOException {

        b_uniqueWord = IndexSaverSingleThreaded.loadUniqueWords(PathsEnum.Bible);
        e_uniqueWord = IndexSaverSingleThreaded.loadUniqueWords(PathsEnum.EllenWhite);
        b_uniqueWordH = IndexSaverSingleThreaded.loadUniqueWordsHelp(PathsEnum.Bible);
        e_uniqueWordH = IndexSaverSingleThreaded.loadUniqueWordsHelp(PathsEnum.EllenWhite);

    }

    public void initOpenBookmarkAction() {

        openingBookmark.addListener(change -> {

            if (openingBookmark.get() != null) {
                ListView<Book> openingBook;
                if (openingBookmark.get().getRoot() == PathsEnum.Bible) {
                    openingBook = bibleListView;
                    bookTabPane.getSelectionModel().select(bibleTab);
                } else {
                    openingBook = ellenListView;
                    bookTabPane.getSelectionModel().select(ellenTab);
                }

                openingBook.getSelectionModel().select(-1);
                openingBook.getSelectionModel().select(openingBookmark.get().getBookId());
                openingBook.scrollTo(openingBookmark.get().getBookId());

                chapterListView.getSelectionModel().select(openingBookmark.get().getChapterId() - 1);
                chapterListView.scrollTo(openingBookmark.get().getChapterId());

                highlightBookmark(openingBookmark.get());

                openingBookmark.set(null);
            }

        });

    }

    public void highlightBookmark(BookmarkStruct bookmark) {

        String mainText = mainTextArea.getText();

        int start = bookmark.getPosition();
        int end = start + bookmark.getText().length();

        while (!mainText.substring(start, end).equalsIgnoreCase(bookmark.getText())) {
            start++;
            end++;
        }

        mainTextArea.setStyleClass(start, end, "ftext");

        mainTextArea.moveTo(start);
        mainTextArea.requestFollowCaret();

    }

    public void doCreateBookmark() {
        BookmarksController.additionBookmark.set(createBookmark());
    }

    public BookmarkStruct createBookmark() {

        String selectedText = mainTextArea.getSelectedText();
        ListView<Book> selectedList = bibleTab.isSelected() ? bibleListView : ellenListView;
        PathsEnum root = bibleTab.isSelected() ? PathsEnum.Bible : PathsEnum.EllenWhite;
        String link = "";
        if (!selectedText.isEmpty()) {
            link += String.format("%s %d", new Cutser().getCutByRoot(selectedList.getSelectionModel().getSelectedIndex(), root), chapterListView.getSelectionModel().getSelectedItem());

            return new BookmarkStruct.Builder()
                    .with_content(selectedText)
                    .with_link(link)
                    .withRoot(root)
                    .withBookId(selectedList.getSelectionModel().getSelectedIndex())
                    .withChapterId(chapterListView.getSelectionModel().getSelectedItem())
                    .withPosition(mainTextArea.getSelection().getStart())
                    .withText(selectedText)
                    .build();
        }
        return new BookmarkStruct.Builder().build();

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

    private void initializeBookFiles__OnAction() throws IOException {
        bibleListView.setItems(new FileAdapter().getBible());
        ellenListView.setItems(new FileAdapter().getEllen());
    }

    private void highlightText(List<IndexStruct> selectedReferences, String[] words) {
        HashMap<Integer, String> uniqueWords = bibleLinkTab.isSelected() ? b_uniqueWord : ellenLinkTab.isSelected() ? e_uniqueWord : b_uniqueWord;

        String mainText = mainTextArea.getText();

        int positionCarret = 0;

        selectedReferences.sort(Comparator.comparingInt(IndexStruct::getPosition));

        for (int i = 0; i < selectedReferences.size(); i++) {

            Chapter chapter = selectedBook.getChapters().get(selectedReferences.get(i).getChapterID());

            int start = 0;
            String word = uniqueWords.get(selectedReferences.get(i).getWordKey());

            for (int j = 0; j < selectedReferences.get(i).getFragmentID(); j++)
                start += chapter.getFragments().get(j).length();

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
            selectBibleList();
        });
    }

    private void selectBibleList() {

        Book _selectedBook = bibleListView.getSelectionModel().getSelectedItem();
        if (_selectedBook != null) {
            selectedBook = _selectedBook;
            setChapterListView(_selectedBook);
            if (bibleListView.getSelectionModel().getSelectedIndex() > -1 && !bibleLinkView.getItems().isEmpty())
                sortLinkView(bibleListView.getSelectionModel().getSelectedIndex());
        }

    }

    private void sortLinkView(int id) {

        ListView<Link> listView;
        PathsEnum enu;
        FilteredList<Link> filteredList;

        if (bibleTab.isSelected()) {
            enu = PathsEnum.Bible;
            listView = bibleLinkView;
            filteredList = filteredBibleList;
        } else {
            enu = PathsEnum.EllenWhite;
            listView = ellenLinkView;
            filteredList = filteredEllenList;
        }

        SortedList<Link> sortedList = new SortedList<>(filteredList);

        String searchText = new Cutser().getCutByRoot(id, enu);

        sortedList.setComparator((item1, item2) -> {
            boolean item1Contains = item1.getLinkContent().contains(searchText);
            boolean item2Contains = item2.getLinkContent().contains(searchText);

            if (item1Contains && !item2Contains) {
                return -1;
            } else if (!item1Contains && item2Contains) {
                return 1;
            } else {
                return item1.getLinkContent().compareTo(item2.getLinkContent());
            }
        });

        listView.setItems(sortedList);
        listView.scrollTo(listView.getSelectionModel().getSelectedIndex());

    }

    private void setChapterListView(Book _new) {
        ObservableList<Integer> chapterList = FXCollections.observableArrayList();
        chapterList.addAll(_new.getChapters().stream().map(x -> x.number.get()).toList());
        chapterList.removeLast();
        chapterListView.setItems(chapterList);

        chapterListView.getSelectionModel().select(0);
    }

    private void selectedEllenList__OnAction() {
        ellenListView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
            selectEllenList();
        });
    }

    private void selectEllenList() {

        Book _selectedBook = ellenListView.getSelectionModel().getSelectedItem();
        if (_selectedBook != null) {
            selectedBook = _selectedBook;
            setChapterListView(_selectedBook);
            if (ellenListView.getSelectionModel().getSelectedIndex() > -1 && !ellenLinkView.getItems().isEmpty())
                sortLinkView(ellenListView.getSelectionModel().getSelectedIndex());
        }

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
        IndexSaverSingleThreaded.saveUniqueWordsHelp(indexator.getUniqueWordsHelp(), PathsEnum.Bible);
        IndexSaverSingleThreaded.save(indexator.getIndexData(), PathsEnum.Bible);

        /*for (var key : indexator.getDictionary().keySet()) {
            IndexSaverSingleThreaded.save(indexator.getDictionary().get(key), PathsEnum.Bible, String.valueOf(key));
        }*/

        //IndexSaverSingleThreaded.save(indexator.getIndexData(), PathsEnum.Bible);


        indexator = new IndexatorSingleThreaded(ellenListView.getItems());

        indexator.index();

        IndexSaverSingleThreaded.saveUniqueWords(indexator.getUniqueWords(), PathsEnum.EllenWhite);
        IndexSaverSingleThreaded.saveUniqueWordsHelp(indexator.getUniqueWordsHelp(), PathsEnum.EllenWhite);
        IndexSaverSingleThreaded.save(indexator.getIndexData(), PathsEnum.EllenWhite);

       /* for (var key : indexator.getDictionary().keySet()) {
            IndexSaverSingleThreaded.save(indexator.getDictionary().get(key), PathsEnum.EllenWhite, String.valueOf(key));
        }*/

        //IndexSaverSingleThreaded.save(indexator.getIndexData(), PathsEnum.EllenWhite);
    }

    public void openBookmarkWindowOnAction() throws IOException {
        bookmarksWindow.stage().show();
    }

    public void openArticleWindowOnAction() {
        articlesWindow.stage().show();
    }

    public void loadIndex__OnAction() throws URISyntaxException, IOException {


    }

    public void selectTabBible__OnAction() {
        bibleListView.getSelectionModel().select(0);
        selectBibleList();
    }

    public void selectTabEllen__OnAction() {
        ellenListView.getSelectionModel().select(0);
        selectEllenList();
    }

    public void initSearchByLink() {

        filteredBibleList = new FilteredList<>(obsBibleLink, p -> true);
        searchByLinkField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBibleList.setPredicate(data -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String[] findTexts = newValue.toLowerCase().split(" ");

                return checkContains(data.getLinkContent().toLowerCase(), findTexts);
            });
        });
        bibleLinkView.setItems(filteredBibleList);

        filteredEllenList = new FilteredList<>(obsEllenLink, p -> true);
        searchByLinkField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredEllenList.setPredicate(data -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String[] findTexts = newValue.toLowerCase().split(" ");

                return checkContains(data.getLinkContent().toLowerCase(), findTexts);
            });
        });
        ellenLinkView.setItems(filteredEllenList);

    }

    public void initSearchByLinkCut() {

        searchByLinkField.onKeyPressedProperty().set(action -> {
            if (action.getCode() == KeyCode.ENTER && !searchByLinkField.getText().isEmpty()) {
                searchByCut(searchByLinkField.getText());
            }
        });

    }

    public void searchByCut(String prompt) {

        Cutprompt cutprompt = Cutprompt.validationBuild(prompt);
        if (cutprompt instanceof ErrorCutprompt)
            System.err.println("link isn't valid: error prompt");
        else if (cutprompt instanceof BibleCutprompt) {
            searchByCutBiblePart((BibleCutprompt) cutprompt);
        } else if (cutprompt instanceof EllenCutprompt) {
            searchByCutEllenPart((EllenCutprompt) cutprompt);
        }

    }

    public void searchByCutEllenPart(EllenCutprompt cutprompt) {

        int page = cutprompt.getPage();
        int chapterId = 0;
        int fragmentId = 0;
        int pos = -1;

        Book book = ellenListView.getItems().get(cutprompt.getBookId());
        boolean key = false;
        for (Chapter chapter : book.getChapters()) {
            fragmentId = 0;
            for (String fragment : chapter.getFragments()) {
                if (fragment.contains(String.format("[%d]", page))) {
                    pos = fragment.indexOf(String.format("[%d]", page));
                    key = true;
                    break;
                }
                fragmentId++;
            }
            if (key) break;
            chapterId++;
        }


        if (pos > -1) {
            if (ellenListView.getItems().get(cutprompt.getBookId()).getChapters().size() > chapterId && chapterId >= 0
                    && ellenListView.getItems().get(cutprompt.getBookId()).getChapters().get(chapterId).getFragments().size() > fragmentId
                    && fragmentId >= 0) {
                Link link = new Link(List.of(new IndexStruct(
                        cutprompt.getBookId(),
                        chapterId,
                        fragmentId, 0,
                        e_uniqueWordH.get(String.valueOf(page)),
                        null
                )), ellenListView.getItems(), cutprompt.getMode(), String.valueOf(page));
                obsEllenLink.add(link);
            } else System.err.println("link isn't valid: invalid values");
        } else System.err.println("link isn't valid: invalid values");

    }

    public void searchByCutBiblePart(BibleCutprompt cutprompt) {

        int chapterId = cutprompt.getChapter();
        int fragmentId = cutprompt.getFragment();

        if (bibleListView.getItems().get(cutprompt.getBookId()).getChapters().size() > chapterId && chapterId >= 0
                && bibleListView.getItems().get(cutprompt.getBookId()).getChapters().get(chapterId).getFragments().size() > fragmentId
                && fragmentId >= 0) {
            Link link = new Link(List.of(new IndexStruct(
                    cutprompt.getBookId(),
                    chapterId,
                    fragmentId, 0,
                    b_uniqueWordH.get(String.valueOf(fragmentId + 1)),
                    null
            )), bibleListView.getItems(), cutprompt.getMode(), String.valueOf(fragmentId + 1));
            obsBibleLink.add(link);
        } else System.err.println("link isn't valid: invalid values");

    }

    public void doSearch__OnAction() {
        String prompt = searchByTextField.getText();
        if (!prompt.isEmpty()) {
            if (bibleTab.isSelected()) {
                filteredBibleList = new FilteredList<>(b_searcher.search(prompt, PathsEnum.Bible), p -> true);
                /*searchByLinkField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredBibleList.setPredicate(data -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String[] findTexts = newValue.toLowerCase().split(" ");

                        return checkContains(data.getLinkContent().toLowerCase(), findTexts);
                    });
                });*/
                bibleLinkView.setItems(filteredBibleList);
            }
            if (ellenTab.isSelected()) {
                filteredEllenList = new FilteredList<>(e_searcher.search(prompt, PathsEnum.EllenWhite), p -> true);
                /*searchByLinkField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredEllenList.setPredicate(data -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String[] findTexts = newValue.toLowerCase().split(" ");

                        return checkContains(data.getLinkContent().toLowerCase(), findTexts);
                    });
                });*/
                ellenLinkView.setItems(filteredEllenList);
            }


        }
    }

    private boolean checkContains(String str, String[] arr) {
        for (String text : arr) {
            if (!str.contains(text)) return false;
        }

        return true;
    }

    /* * * Component description * * */

    static class LinkComponent {

        private LinkComponent() {
        }

        private static MainController controller;

        private static class Holder {
            private static final LinkComponent INSTANCE = new LinkComponent();
        }

        public static LinkComponent getInstance(MainController _controller) {
            if (controller == null) controller = _controller;
            return Holder.INSTANCE;
        }

        public void initLinkListSelection() {

            defineBibleLinkSelection();
            defineEllenLinkSelection();

        }

        private void defineBibleLinkSelection() {

            controller.bibleLinkView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
                if (_new != null) {
                    List<IndexStruct> selectedReferences = _new.getReferences();

                    controller.bibleLinkView.scrollTo(controller.bibleLinkView.getSelectionModel().getSelectedIndex());

                    controller.bibleListView.getSelectionModel().select(selectedReferences.getFirst().getBookID());
                    controller.bibleListView.scrollTo(selectedReferences.getFirst().getBookID());

                    controller.chapterListView.getSelectionModel().select(selectedReferences.getFirst().getChapterID() - 1);
                    controller.chapterListView.scrollTo(selectedReferences.getFirst().getChapterID());

                    controller.highlightText(selectedReferences, _new.getWords());
                }
            });

        }

        private void defineEllenLinkSelection() {

            controller.ellenLinkView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
                if (_new != null) {
                    List<IndexStruct> selectedReferences = _new.getReferences();
                    controller.ellenLinkView.scrollTo(controller.bibleLinkView.getSelectionModel().getSelectedIndex());

                    controller.ellenListView.getSelectionModel().select(selectedReferences.getFirst().getBookID());
                    controller.ellenListView.scrollTo(selectedReferences.getFirst().getBookID());

                    controller.chapterListView.getSelectionModel().select(selectedReferences.getFirst().getChapterID() - 1);
                    controller.chapterListView.scrollTo(selectedReferences.getFirst().getChapterID());

                    controller.highlightText(selectedReferences, _new.getWords());
                }
            });

        }

    }

    static class EraseButtonComponent {

        private static MainController controller;

        private static class Holder {
            private static final EraseButtonComponent INSTANCE = new EraseButtonComponent();
        }

        public static EraseButtonComponent getInstance(MainController _controller) {
            if (controller == null) controller = _controller;
            return EraseButtonComponent.Holder.INSTANCE;
        }

        public void initEraseButtons() throws URISyntaxException {

            defineEraseButtonGraphics();
            defineEraseButtonAction();

        }

        private StackPane defineEraseGraphic(Image image) {

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(15);
            imageView.setFitHeight(15);

            StackPane stackPane = new StackPane(imageView);
            stackPane.setPrefSize(16, 29);
            stackPane.setMaxSize(16, 29);
            stackPane.setMinSize(16, 29);
            StackPane.setAlignment(imageView, Pos.CENTER);
            return stackPane;

        }

        private void defineEraseButtonVisibilityRule(TextField textField, Button button) {

            textField.textProperty().addListener(listener -> {
                button.setVisible(!textField.getText().isEmpty());
            });

        }

        private void defineEraseButtonGraphics() throws URISyntaxException {

            String url = Objects.requireNonNull(Symphony.class.getResource("buttons/erase2.png")).toURI().getPath();
            if (url.startsWith("/")) url = url.replaceFirst("/", "");

            System.err.println(url);

            Image image = new Image(url);
            StackPane byText = defineEraseGraphic(image);
            StackPane byLink = defineEraseGraphic(image);

            controller.eraseSearchByText.setVisible(false);
            controller.eraseSearchByLink.setVisible(false);
            controller.eraseSearchByText.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            controller.eraseSearchByText.setGraphic(byText);
            controller.eraseSearchByText.setAlignment(Pos.CENTER);

            controller.eraseSearchByLink.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            controller.eraseSearchByLink.setGraphic(byLink);
            controller.eraseSearchByLink.setAlignment(Pos.CENTER);

            defineEraseButtonVisibilityRule(controller.searchByTextField, controller.eraseSearchByText);
            defineEraseButtonVisibilityRule(controller.searchByLinkField, controller.eraseSearchByLink);

        }

        private void defineEraseButtonAction() {

            controller.eraseSearchByText.setOnAction(action -> {
                controller.searchByTextField.setText("");
                controller.searchByTextField.requestFocus();
            });
            controller.eraseSearchByLink.setOnAction(action -> {
                controller.searchByLinkField.setText("");
                controller.searchByLinkField.requestFocus();
            });

        }

    }

    static class MainTextAreaComponent {

        private static MainController controller;

        private static class Holder {
            private static final MainTextAreaComponent INSTANCE = new MainTextAreaComponent();
        }

        public static MainTextAreaComponent getInstance(MainController _controller) {
            if (controller == null) controller = _controller;
            return MainTextAreaComponent.Holder.INSTANCE;
        }

        public void initMainTextArea() {

            defineMainTextArea();
            defineMainTextAreaHandler();

        }

        public void defineMainTextArea() {

            controller.mainTextArea = new StyleClassedTextArea();

            controller.mainTextArea.setWrapText(true);

            controller.mainGridPane.getChildren().add(controller.mainTextArea);
            GridPane.setColumnIndex(controller.mainTextArea, 1);
            GridPane.setRowIndex(controller.mainTextArea, 3);
            GridPane.setHgrow(controller.mainTextArea, Priority.ALWAYS);
            GridPane.setVgrow(controller.mainTextArea, Priority.ALWAYS);
            GridPane.setMargin(controller.mainTextArea, new Insets(3, 0, 0, 0));

        }

        public void defineMainTextAreaHandler() {

            controller.mainTextArea.setOnMouseClicked(mouse -> {
                if (controller.mainTextArea.getSelectedText().isEmpty()) {
                    String text = controller.mainTextArea.getText();
                    int caretPosition = controller.mainTextArea.getCaretPosition();

                    int start = caretPosition;
                    int end = caretPosition;

                    while (start > 0 && Character.isLetterOrDigit(text.charAt(start - 1))) {
                        start--;
                    }

                    while (end < text.length() && Character.isLetterOrDigit(text.charAt(end))) {
                        end++;
                    }

                    controller.mainTextArea.selectRange(start, end+1);
                    controller.mainTextArea.selectRange(start, end);
                }
            });

        }

    }

    static class LeafButtonComponent {

        private static MainController controller;

        private static class Holder {
            private static final LeafButtonComponent INSTANCE = new LeafButtonComponent();
        }

        public static LeafButtonComponent getInstance(MainController _controller) {
            if (controller == null) controller = _controller;
            return LeafButtonComponent.Holder.INSTANCE;
        }

        public void initLeafButtons() throws URISyntaxException {

            defineLeafButton("left");
            defineLeafButton("right");

        }

        private void defineLeafButton(String side) throws URISyntaxException {

            String path;
            HPos pos;
            if (side.equals("right")) {
                path = "buttons/forward.png";
                pos = HPos.RIGHT;
            } else if (side.equals("left")) {
                path = "buttons/backward.png";
                pos = HPos.LEFT;
            } else {
                return;
            }

            String url = Objects.requireNonNull(Symphony.class.getResource(path)).toURI().getPath();
            if (url.startsWith("/")) url = url.replaceFirst("/", "");
            Button leafButton = getLeafButton(url);

            controller.mainGridPane.getChildren().add(leafButton);
            GridPane.setColumnIndex(leafButton, 1);
            GridPane.setRowIndex(leafButton, 3);
            GridPane.setValignment(leafButton, VPos.BOTTOM);
            GridPane.setHalignment(leafButton, pos);

            defineLeafButtonBehavior(leafButton, side);

        }

        private @NotNull Button getLeafButton(String url) {
            Image image = new Image(url);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);

            Button leafButton = new Button();
            leafButton.setStyle("-fx-background-color: transparent;");
            leafButton.setOpacity(0.5);
            leafButton.setOnMouseEntered(mouse -> {
                leafButton.setOpacity(1);
            });
            leafButton.setOnMouseExited(mouse -> {
                leafButton.setOpacity(0.5);
            });
            leafButton.setPrefWidth(50);
            leafButton.setPrefHeight(50);
            leafButton.setMaxWidth(50);
            leafButton.setMaxHeight(50);
            leafButton.setGraphic(imageView);
            return leafButton;
        }

        private void defineLeafButtonBehavior(Button leafButton, String side) {
            leafButton.setOnAction(action -> {
                if (side.equals("right")) controller.chapterListView.getSelectionModel().selectNext();
                else controller.chapterListView.getSelectionModel().selectPrevious();
            });
        }

    }

    static class HoverPanelComponent {

        private static MainController controller;

        private static class Holder {
            private static final HoverPanelComponent INSTANCE = new HoverPanelComponent();
        }

        public static HoverPanelComponent getInstance(MainController _controller) {
            if (controller == null) controller = _controller;
            return HoverPanelComponent.Holder.INSTANCE;
        }

        public void initHoverPanel() {

            defineHoverPanel();

        }

        private void defineHoverPanel() {

            HBox hoverSelectionPanel = new HBox();
            hoverSelectionPanel.setVisible(false);
            hoverSelectionPanel.setMaxHeight(30);
            hoverSelectionPanel.setMaxWidth(200);
            hoverSelectionPanel.getStyleClass().add("hover_selection_panel");

            controller.mainGridPane.getChildren().add(hoverSelectionPanel);

            GridPane.setColumnIndex(hoverSelectionPanel, 0);
            GridPane.setColumnSpan(hoverSelectionPanel, 3);
            GridPane.setRowIndex(hoverSelectionPanel, 0);
            GridPane.setRowSpan(hoverSelectionPanel, 4);
            GridPane.setValignment(hoverSelectionPanel, VPos.TOP);
            GridPane.setHalignment(hoverSelectionPanel, HPos.LEFT);
            defineHoverPanelBehavior(hoverSelectionPanel);
            defineHoverPanelCopyButton(hoverSelectionPanel);
            defineHoverPanelBookmarkButton(hoverSelectionPanel);

        }

        private void defineHoverPanelBehavior(HBox hoverSelectionPanel) {
            AtomicReference<Double> deltaX = new AtomicReference<>(0d);
            AtomicReference<Double> deltaY = new AtomicReference<>(0d);

            defineDeltas(deltaX, deltaY);

            controller.mainTextArea.selectionProperty().addListener((ov, i1, i2) -> {

                if (i1.getStart() != i1.getEnd() && !controller.mainTextArea.getSelectedText().isEmpty()) {
                    hoverSelectionPanel.setVisible(true);
                    Bounds bounds = controller.mainTextArea.getCharacterBoundsOnScreen(i1.getStart(), i1.getStart()).orElse(null);

                    if (bounds != null) {
                        hoverSelectionPanel.translateXProperty().set(bounds.getMinX() - deltaX.get());
                        hoverSelectionPanel.translateYProperty().set(bounds.getMinY() - deltaY.get() - hoverSelectionPanel.getMaxHeight());

                        if (hoverSelectionPanel.translateXProperty().get() + hoverSelectionPanel.getMaxWidth()
                                > currentWindowWidth.get() - 200) {
                            hoverSelectionPanel.translateXProperty().set(currentWindowWidth.get() - 200 - hoverSelectionPanel.getMaxWidth());
                        }

                    }
                }
                if (controller.mainTextArea.getSelectedText().isEmpty()) {
                    hoverSelectionPanel.setVisible(false);
                }
            });
        }

        private void defineDeltas(AtomicReference<Double> deltaX, AtomicReference<Double> deltaY) {

            controller.mainTextArea.setOnMousePressed(mouseEvent -> {
                deltaX.set(getDeltaX(mouseEvent));
                deltaY.set(getDeltaY(mouseEvent));
            });

        }

        private double getDeltaX(MouseEvent mouse) {
            return mouse.getScreenX() - mouse.getSceneX();
        }

        private double getDeltaY(MouseEvent mouse) {
            return mouse.getScreenY() - mouse.getSceneY();
        }

        private void defineHoverPanelCopyButton(HBox hoverSelectionPanel) {

            Button copyButton = new Button();
            copyButton.setText("");
            copyButton.setPrefWidth(26);
            copyButton.setPrefHeight(26);
            HBox.setMargin(copyButton, new Insets(0, 0, 1, 3));
            copyButton.getStyleClass().add("copy_button");
            hoverSelectionPanel.alignmentProperty().set(Pos.CENTER_LEFT);
            hoverSelectionPanel.getChildren().add(copyButton);
            defineCopyButtonBehavior(copyButton);

        }

        private void defineHoverPanelBookmarkButton(HBox hoverSelectionPanel) {

            Button createBookmarkButton = new Button();
            createBookmarkButton.setText("");
            createBookmarkButton.setPrefWidth(26);
            createBookmarkButton.setPrefHeight(26);
            HBox.setMargin(createBookmarkButton, new Insets(0, 0, 1, 3));
            createBookmarkButton.getStyleClass().add("copy_button");
            hoverSelectionPanel.alignmentProperty().set(Pos.CENTER_LEFT);
            hoverSelectionPanel.getChildren().add(createBookmarkButton);
            createBookmarkButton.onActionProperty().set(actionEvent -> {
                controller.doCreateBookmark();
            });

        }

        private void defineCopyButtonBehavior(Button copyButton) {

            copyButton.onActionProperty().set(action -> {
                if (!controller.mainTextArea.getSelectedText().isEmpty()) {

                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(controller.mainTextArea.getSelectedText());
                    clipboard.setContent(content);

                }
            });

        }

    }

}