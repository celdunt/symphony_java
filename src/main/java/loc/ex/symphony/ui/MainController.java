package loc.ex.symphony.ui;


import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.file.ArticleSerializer;
import loc.ex.symphony.file.BookSerializer;
import loc.ex.symphony.file.FileAdapter;
import loc.ex.symphony.file.FileResaver;
import loc.ex.symphony.indexdata.*;
import loc.ex.symphony.listview.*;
import loc.ex.symphony.search.*;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
    public TabPane linkTabPane;
    public Button indexButton;
    public Button articleButton;
    public Button bookmarkButton;
    public Button createArticleButton;
    public Button resaveArticleButton;
    public ListView<Book> otherListView;
    public ListView<Link> otherLinkView;
    public Tab booksTab;
    public Button eraseCurLink;
    public Button eraseAllLink;
    public ToggleButton searchMode;
    public StackPane toggleContainer;
    public Label searchModeLabel;
    public SplitPane midSplitPane;
    public GridPane midGridPane;
    public SplitPane mainSplitPane;
    public ToggleButton splitReadButton;
    public ToggleButton splitReadMod2;
    public HBox splitReadContainer;
    public ToggleButton splitReadMod3;
    public ToggleButton splitReadMod4;
    private Searcher b_searcher;
    private Searcher e_searcher;
    private Searcher o_searcher;

    public TextField searchByTextField;
    public Button searchButton;
    public TextField searchByNameField;
    public TextField searchByLinkField;
    public ListView<Integer> chapterListView;
    public ListView<Book> bibleListView;
    public ListView<Book> ellenListView;
    public ListView<Link> bibleLinkView;
    public ListView<Link> ellenLinkView;

    private OpenChapterData mainCD = new OpenChapterData();

    private StyleClassedTextArea currentTArea = new StyleClassedTextArea();

    public FilteredList<Link> filteredBibleList;
    public FilteredList<Link> filteredEllenList;
    public FilteredList<Link> filteredOtherList;
    public ObservableList<Link> obsBibleLink = FXCollections.observableArrayList();
    public ObservableList<Link> obsEllenLink = FXCollections.observableArrayList();
    public ObservableList<Link> obsOtherLink = FXCollections.observableArrayList();

    public Tab bibleTab;
    public Book selectedBook;

    public HashMap<Integer, String> b_uniqueWord = new HashMap<>();
    public HashMap<Integer, String> e_uniqueWord = new HashMap<>();
    public HashMap<Integer, String> o_uniqueWord = new HashMap<>();
    private HashMap<String, Integer> b_uniqueWordH = new HashMap<>();
    private HashMap<String, Integer> e_uniqueWordH = new HashMap<>();
    private HashMap<String, Integer> o_uniqueWordH = new HashMap<>();

    public static AtomicReference<Double> currentWindowWidth = new AtomicReference<>(0d);
    public static AtomicReference<Double> currentWindowHeight = new AtomicReference<>(0d);

    public BookmarksWindow bookmarksWindow;
    public ArticlesWindow articlesWindow;

    public static SimpleStringProperty listener = new SimpleStringProperty();
    public static SimpleStringProperty usabilityButtonListener = new SimpleStringProperty();
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static ObjectProperty<BookmarkStruct> openingBookmark = new SimpleObjectProperty<>();
    public static ObjectProperty<Article> openingArticle = new SimpleObjectProperty<>();

    public int splitReadMode = 0;

    ChangeListener<Integer> selectedChapterListener = (_obs, _old, _new) -> {
        if (_new != null) {
            currentTArea.clear();
            currentTArea.setStyleClass(0, 0, "jtext");
            currentTArea.insertText(0, selectedBook.getChapters().get(_new).getEntireText());

            currentTArea.moveTo(0);
            currentTArea.requestFollowCaret();

            int bookID = Integer.max(Integer.max(
                    bibleListView.getSelectionModel().getSelectedIndex(),
                    ellenListView.getSelectionModel().getSelectedIndex()
            ), otherListView.getSelectionModel().getSelectedIndex());

            int tab = bibleTab.isSelected()? 0: ellenTab.isSelected()? 1: 2;

            if (currentTArea == mainTextArea) {
                mainCD.setBook(bookID);
                mainCD.setChapter(chapterListView.getSelectionModel().getSelectedIndex());
                mainCD.setTab(tab);
            } else {
                OpenChapterData cd = SplitReadComponent.getInstance(this).getChapterData(currentTArea);
                cd.setBook(bookID);
                cd.setChapter(chapterListView.getSelectionModel().getSelectedIndex());
                cd.setTab(tab);
            }

            try {
                getNotesForSelectedChapter().display(currentTArea);
                getParallelLinkForSelectedChapter().display(currentTArea);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public MainController() {
    }

    public void initialize() throws IOException, URISyntaxException {

        initBookmarkWindow();
        initArticlesWindow();
        initUniqueWordsFields();
        initSearchByTextEnterPressed();
        initBookLinkSelectionHandler();
        initIndexButtonHandler();
        initBookmarkButton();
        initSearchButton();
        initEraseLinkButtons();
        initArticleButtons();
        initSplitReadButton();
        MainTextAreaComponent.getInstance(this).initMainTextArea();
        LeafButtonComponent.getInstance(this).initLeafButtons();
        HoverPanelComponent.getInstance(this).initHoverPanel();
        initializeBookFiles__OnAction();
        selectedBibleList__OnAction();
        selectedEllenList__OnAction();
        selectedOtherList__OnAction();
        selectedChapterList__OnAction();
        initSearchModeButton();
        LinkComponent.getInstance(this).initLinkListSelection();
        EraseButtonComponent.getInstance(this).initEraseButtons();
        initOpenBookmarkAction();
        initOpenArticleAction();
        initSearchByLink();
        initSearchByLinkCut();
        initSplitReadModeButtons();
        Platform.runLater(this::initializeSceneHandler);
        bibleListView.setCellFactory(param -> new RichCell<>());
        bibleLinkView.setCellFactory(param -> new LinkCell<>((int) bibleLinkView.getWidth(), this));
        ellenListView.setCellFactory(param -> new RichCell<>());
        ellenLinkView.setCellFactory(param -> new LinkCell<>((int) ellenLinkView.getWidth(), this));
        otherListView.setCellFactory(param -> new RichCell<>());
        otherLinkView.setCellFactory(param -> new LinkCell<>((int) otherLinkView.getWidth(), this));
        logger.info("resource is set");
        searchButton.setDisable(true);
        usabilityButtonListener.addListener(listener -> {
            searchButton.setDisable(false);
        });
        b_searcher = new Searcher(PathsEnum.Bible, b_uniqueWord);
        b_searcher.setResource(bibleListView.getItems());
        e_searcher = new Searcher(PathsEnum.EllenWhite, e_uniqueWord);
        e_searcher.setResource(ellenListView.getItems());
        o_searcher = new Searcher(PathsEnum.Other, o_uniqueWord);
        o_searcher.setResource(otherListView.getItems());
        mainTextArea.editableProperty().set(false);
        selectTabBible__OnAction();
    }

    public void initFocusChange() {

        currentTArea = mainTextArea;


        mainTextArea.focusedProperty().addListener(lis -> {
            if (mainTextArea.isFocused()) {
                chapterListView.getSelectionModel().selectedItemProperty().removeListener(selectedChapterListener);


                currentTArea = mainTextArea;
                ListView<Book> lb;

                bookTabPane.getSelectionModel().select(mainCD.getTab());
                if (mainCD.getTab() == 0)
                    lb = bibleListView;
                else if (mainCD.getTab() == 1)
                    lb = ellenListView;
                else lb = otherListView;

                lb.getSelectionModel().select(mainCD.getBook());
                lb.scrollTo(mainCD.getBook());

                chapterListView.getSelectionModel().select(mainCD.getChapter());
                chapterListView.scrollTo(mainCD.getChapter());

                chapterListView.getSelectionModel().selectedItemProperty().addListener(selectedChapterListener);
            }
        });

        SplitReadComponent.getInstance(this).initFocused();

    }

    public void initSplitReadModeButtons() {
        ToggleGroup group = new ToggleGroup();
        splitReadMod2.setToggleGroup(group);
        splitReadMod3.setToggleGroup(group);
        splitReadMod4.setToggleGroup(group);

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
            } else {
                if (newValue == splitReadMod2) {
                    splitReadMode = 1;
                } else if (newValue == splitReadMod3) {
                    splitReadMode = 2;
                } else if (newValue == splitReadMod4) {
                    splitReadMode = 3;
                }
                SplitReadComponent.getInstance(this).display(splitReadMode);
            }
        });
    }

    public void initSearchByTextEnterPressed() {

        searchByTextField.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.ENTER) {
                doSearch__OnAction();
            }
        });

    }

    public void initBookLinkSelectionHandler() {

        bookLinkSelectionHandler(bibleLinkView);
        bookLinkSelectionHandler(ellenLinkView);
        bookLinkSelectionHandler(otherLinkView);

    }

    public void bookLinkSelectionHandler(ListView<Link> linkView) {

        linkView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        linkView.setOnKeyPressed(key -> {
           if (key.getCode() == KeyCode.DELETE) {
                if (linkView == bibleLinkView) {
                    obsBibleLink.removeAll(linkView.getSelectionModel().getSelectedItems());
                } else if (linkView == ellenLinkView) {
                    obsEllenLink.removeAll(linkView.getSelectionModel().getSelectedItems());
                } else if (linkView == otherLinkView) {
                    obsOtherLink.removeAll(linkView.getSelectionModel().getSelectedItems());
                }
            }
        });

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
        o_uniqueWord = IndexSaverSingleThreaded.loadUniqueWords(PathsEnum.Other);
        o_uniqueWordH = IndexSaverSingleThreaded.loadUniqueWordsHelp(PathsEnum.Other);

    }

    public void initOpenBookmarkAction() {

        openingBookmark.addListener(change -> {

            if (openingBookmark.get() != null) {
                ListView<Book> openingBook;
                if (openingBookmark.get().getRoot() == PathsEnum.Bible) {
                    openingBook = bibleListView;
                    bookTabPane.getSelectionModel().select(bibleTab);
                } else if (openingBookmark.get().getRoot() == PathsEnum.EllenWhite) {
                    openingBook = ellenListView;
                    bookTabPane.getSelectionModel().select(ellenTab);
                } else {
                    openingBook = otherListView;
                    bookTabPane.getSelectionModel().select(booksTab);
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

    public void initOpenArticleAction() {

        openingArticle.addListener(change -> {

            if (openingArticle.get() != null) {
                obsBibleLink.clear();
                obsEllenLink.clear();
                obsOtherLink.clear();
                obsBibleLink.addAll(openingArticle.get().getbLinks());
                obsEllenLink.addAll(openingArticle.get().geteLinks());
                obsOtherLink.addAll(openingArticle.get().getoLinks());
            }

        });

    }

    public void highlightBookmark(BookmarkStruct bookmark) {

        String mainText = currentTArea.getText();

        int start = bookmark.getPosition();
        int end = start + bookmark.getText().length();

        while (!mainText.substring(start, end).equalsIgnoreCase(bookmark.getText())) {
            start++;
            end++;
        }

        currentTArea.setStyleClass(start, end, "fill-text");

        currentTArea.moveTo(start);
        currentTArea.requestFollowCaret();

    }

    public void doCreateBookmark() {
        BookmarksController.additionBookmark.set(createBookmark());
    }

    public void doCreateArticle() throws IOException {

        Article article = new Article(
                LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
                "",
                bibleLinkView.getItems(),
                ellenLinkView.getItems(),
                otherLinkView.getItems());

        new ConfirmNamingWindow(article).stage().show();

    }

    private void doCreateNote() throws IOException, URISyntaxException {

        if (!currentTArea.getSelectedText().isEmpty()) {
            int start = currentTArea.getSelection().getStart();
            int end = currentTArea.getSelection().getEnd();

            Note note = new Note(start, end, "");

            getNotesForSelectedChapter().add(note);

            getNotesForSelectedChapter().display(currentTArea);

            MainTextAreaComponent.getInstance(this).selectSpecialTextAction(currentTArea);
        }

    }

    private void doCreateParallelLink() throws IOException, URISyntaxException {

        if (!currentTArea.getSelectedText().isEmpty()){
            int start = currentTArea.getSelection().getStart();
            int end = currentTArea.getSelection().getEnd();

            ParallelLink link = new ParallelLink(start, end, new ArrayList<>());

            getParallelLinkForSelectedChapter().add(link);
            getParallelLinkForSelectedChapter().display(currentTArea);

            MainTextAreaComponent.getInstance(this).selectSpecialTextAction(currentTArea);
        }

    }

    private NotesSubStorage getNotesForSelectedChapter() throws IOException {
        if (bibleTab.isSelected()) {
            return NotesStorage.getBible(
                            bibleListView.getSelectionModel().getSelectedIndex(),
                            chapterListView.getItems().size()
                    ).get(chapterListView.getSelectionModel().getSelectedIndex());
        } else if (ellenTab.isSelected()) {
            return NotesStorage.getEllen(
                            ellenListView.getSelectionModel().getSelectedIndex(),
                            chapterListView.getItems().size()
                    ).get(chapterListView.getSelectionModel().getSelectedIndex());
        } else {
            return NotesStorage.getOther(
                    otherListView.getSelectionModel().getSelectedIndex(),
                    chapterListView.getItems().size()
            ).get(chapterListView.getSelectionModel().getSelectedIndex());
        }
    }

    private ParallelsLinksSubStorage getParallelLinkForSelectedChapter() throws IOException {
        if (bibleTab.isSelected()) {
            return ParallelsLinksStorage.getBible(
                    bibleListView.getSelectionModel().getSelectedIndex(),
                    chapterListView.getItems().size()
            ).get(chapterListView.getSelectionModel().getSelectedIndex());
        } else if (ellenTab.isSelected()){
            return ParallelsLinksStorage.getEllen(
                    ellenListView.getSelectionModel().getSelectedIndex(),
                    chapterListView.getItems().size()
            ).get(chapterListView.getSelectionModel().getSelectedIndex());
        } else {
            return ParallelsLinksStorage.getOther(
                    otherListView.getSelectionModel().getSelectedIndex(),
                    chapterListView.getItems().size()
            ).get(chapterListView.getSelectionModel().getSelectedIndex());
        }
    }

    public BookmarkStruct createBookmark() {

        String selectedText = currentTArea.getSelectedText();
        ListView<Book> selectedList = bibleTab.isSelected() ? bibleListView : ellenTab.isSelected()? ellenListView : otherListView;
        PathsEnum root = bibleTab.isSelected() ? PathsEnum.Bible : ellenTab.isSelected()? PathsEnum.EllenWhite : PathsEnum.Other;
        String link = "";
        if (!selectedText.isEmpty()) {
            link += String.format("%s %d", new Cutser().getCutByRoot(selectedList.getSelectionModel().getSelectedIndex(), root), chapterListView.getSelectionModel().getSelectedItem());

            return new BookmarkStruct.Builder()
                    .with_content(selectedText)
                    .with_link(link)
                    .withRoot(root)
                    .withBookId(selectedList.getSelectionModel().getSelectedIndex())
                    .withChapterId(chapterListView.getSelectionModel().getSelectedItem())
                    .withPosition(currentTArea.getSelection().getStart())
                    .withText(selectedText)
                    .build();
        }
        return new BookmarkStruct.Builder().build();

    }

    private void initializeSceneHandler() {
        Set<KeyCode> pressedKeys = new HashSet<>();

        Symphony.scene.setOnKeyPressed(e -> {
            if (currentTArea.isFocused()) {
                if (e.getCode().getName().equals("Ctrl")) {
                    pressedKeys.add(e.getCode());
                }
                if (e.getCode().getName().equals("C")) {
                    pressedKeys.add(e.getCode());
                }
                if (pressedKeys.size() > 1) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(currentTArea.getSelectedText());
                    clipboard.setContent(content);

                    pressedKeys.clear();
                }
            }
        });

    }

    private void initializeBookFiles__OnAction() throws IOException {

        bibleListView.setItems(
                FXCollections.observableArrayList(BookSerializer.load(PathsEnum.Bible)));
        ellenListView.setItems(
                FXCollections.observableArrayList(BookSerializer.load(PathsEnum.EllenWhite)));
        otherListView.setItems(
                FXCollections.observableArrayList(BookSerializer.load(PathsEnum.Other)));

        if (bibleListView.getItems().isEmpty() &&
        ellenListView.getItems().isEmpty() &&
        otherListView.getItems().isEmpty()) {
            bibleListView.setItems(new FileAdapter().getBible());
            ellenListView.setItems(new FileAdapter().getEllen());
            otherListView.setItems(new FileAdapter().getOther());
            BookSerializer.save(bibleListView.getItems());
            BookSerializer.save(ellenListView.getItems());
            BookSerializer.save(otherListView.getItems());
        }

    }

    private void highlightText(List<IndexStruct> selectedReferences, String[] words) {
        HashMap<Integer, String> uniqueWords = bibleLinkTab.isSelected() ? b_uniqueWord : ellenLinkTab.isSelected() ? e_uniqueWord : o_uniqueWord;

        String mainText = currentTArea.getText();

        int positionCaret = 0;

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

            if (positionCaret == 0) positionCaret = start;
            currentTArea.setStyleClass(start, end, "fill-text");

        }

        currentTArea.moveTo(positionCaret);
        currentTArea.requestFollowCaret();
    }

    private void selectedChapterList__OnAction() {
        chapterListView.getSelectionModel().selectedItemProperty().addListener(selectedChapterListener);
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
            bibleLinkView.getSelectionModel().select(-1);
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
        } else if (ellenTab.isSelected()) {
            enu = PathsEnum.EllenWhite;
            listView = ellenLinkView;
            filteredList = filteredEllenList;
        } else {
            enu = PathsEnum.Other;
            listView = otherLinkView;
            filteredList = filteredOtherList;
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
        chapterList.remove(chapterList.size()-1);
        chapterListView.setItems(chapterList);

        chapterListView.getSelectionModel().select(0);
    }

    private void selectedEllenList__OnAction() {
        ellenListView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
            selectEllenList();
        });
    }

    private void selectedOtherList__OnAction() {
        otherListView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
            selectOtherList();
        });
    }

    private void selectEllenList() {

        Book _selectedBook = ellenListView.getSelectionModel().getSelectedItem();
        if (_selectedBook != null) {
            ellenLinkView.getSelectionModel().select(-1);
            selectedBook = _selectedBook;
            setChapterListView(_selectedBook);
            if (ellenListView.getSelectionModel().getSelectedIndex() > -1 && !ellenLinkView.getItems().isEmpty())
                sortLinkView(ellenListView.getSelectionModel().getSelectedIndex());
        }

    }

    private void selectOtherList() {
        Book _selectedBook = otherListView.getSelectionModel().getSelectedItem();
        if (_selectedBook != null) {
            otherLinkView.getSelectionModel().select(-1);
            selectedBook = _selectedBook;
            setChapterListView(_selectedBook);
            if (otherListView.getSelectionModel().getSelectedIndex() > -1 && !otherListView.getItems().isEmpty())
                sortLinkView(otherListView.getSelectionModel().getSelectedIndex());
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

    public void initIndexButtonHandler() {
        indexButton.onActionProperty().set(action -> {
            try {
                doIndex__OnAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void doIndex__OnAction() throws IOException, SQLException, ClassNotFoundException {

        IndexatorSingleThreaded indexator = new IndexatorSingleThreaded(bibleListView.getItems());

        indexator.index();

        IndexSaverSingleThreaded.saveUniqueWords(indexator.getUniqueWords(), PathsEnum.Bible);
        IndexSaverSingleThreaded.saveUniqueWordsHelp(indexator.getUniqueWordsHelp(), PathsEnum.Bible);
        IndexSaverSingleThreaded.save(indexator.getIndexData(), PathsEnum.Bible);


        indexator = new IndexatorSingleThreaded(ellenListView.getItems());

        indexator.index();

        IndexSaverSingleThreaded.saveUniqueWords(indexator.getUniqueWords(), PathsEnum.EllenWhite);
        IndexSaverSingleThreaded.saveUniqueWordsHelp(indexator.getUniqueWordsHelp(), PathsEnum.EllenWhite);
        IndexSaverSingleThreaded.save(indexator.getIndexData(), PathsEnum.EllenWhite);

        indexator = new IndexatorSingleThreaded(otherListView.getItems());

        indexator.index();

        IndexSaverSingleThreaded.saveUniqueWords(indexator.getUniqueWords(), PathsEnum.Other);
        IndexSaverSingleThreaded.saveUniqueWordsHelp(indexator.getUniqueWordsHelp(), PathsEnum.Other);
        IndexSaverSingleThreaded.save(indexator.getIndexData(), PathsEnum.Other);

    }

    public void initArticleButtons() {
        articleButton.onActionProperty().set(action -> {
            try {
                openArticleWindowOnAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        createArticleButton.onActionProperty().set(action -> {
            try {
                doCreateArticle();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        resaveArticleButton.onActionProperty().set(action -> {
            if (openingArticle.get() != null) {
                try {
                    ObservableList<Article> obs = FXCollections.observableArrayList(ArticleSerializer.load());

                    for (Article article : obs) {
                        if (article.getName().equals(openingArticle.get().getName())) {
                            article.oLinks = obsOtherLink;
                            article.bLinks = obsBibleLink;
                            article.eLinks = obsEllenLink;
                        }
                    }

                    ArticleSerializer.save(obs);
                } catch (IOException exception) {
                    System.err.println(exception.getMessage());
                }
            }
        });
    }

    public void initBookmarkButton() {
        bookmarkButton.onActionProperty().set(action -> {
            try {
                openBookmarkWindowOnAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void openBookmarkWindowOnAction() throws IOException {
        bookmarksWindow.stage().show();
    }

    public void openArticleWindowOnAction() throws IOException {
        articlesWindow = new ArticlesWindow();
        articlesWindow.stage().show();
    }

    public void selectTabBible__OnAction() {
        bibleListView.getSelectionModel().select(0);
        selectBibleList();
    }

    public void selectTabEllen__OnAction() {
        ellenListView.getSelectionModel().select(0);
        selectEllenList();
    }

    public void selectTabOther__OnAction() {
        otherListView.getSelectionModel().select(0);
        selectOtherList();
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

        filteredOtherList = new FilteredList<>(obsOtherLink, p -> true);
        searchByLinkField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredOtherList.setPredicate(data -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String[] findTexts = newValue.toLowerCase().split(" ");

                return checkContains(data.getLinkContent().toLowerCase(), findTexts);
            });
        });
        otherLinkView.setItems(filteredOtherList);

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
        switch (cutprompt) {
            case ErrorCutprompt errorCutprompt -> System.err.println("link isn't valid: error prompt");
            case BibleCutprompt bibleCutprompt -> searchByCutBiblePart(bibleCutprompt);
            case EllenCutprompt ellenCutprompt -> searchByCutEllenPart(ellenCutprompt);
            default -> {
            }
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
                if (ParallelsLinkComponent.getInstance(this).isOpened())
                    ParallelsLinkComponent.getInstance(this).add(link);
                else obsEllenLink.add(link);
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

            if (ParallelsLinkComponent.getInstance(this).isOpened())
                ParallelsLinkComponent.getInstance(this).add(link);
            else obsBibleLink.add(link);
        } else System.err.println("link isn't valid: invalid values");

    }

    public void initSearchButton() {
        searchButton.setOnAction(action -> {
            doSearch__OnAction();
        });
    }

    public void doSearch__OnAction() {
        String prompt = searchByTextField.getText();
        if (!prompt.isEmpty()) {
            if (bibleTab.isSelected()) {
                /* *********** ПРОПИСАТЬ ОПЦИИ ПОИСКА ************* */

                obsBibleLink.clear();
                if (!searchMode.isSelected())
                    obsBibleLink.addAll(b_searcher.search(prompt, PathsEnum.Bible));
                else obsBibleLink.addAll(b_searcher.search(prompt, PathsEnum.Bible, b_uniqueWordH));

                sortLinkView(bibleListView.getSelectionModel().getSelectedIndex());

                /*filteredBibleList = new FilteredList<>(b_searcher.search(prompt, PathsEnum.Bible), p -> true);
                *//*searchByLinkField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredBibleList.setPredicate(data -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String[] findTexts = newValue.toLowerCase().split(" ");

                        return checkContains(data.getLinkContent().toLowerCase(), findTexts);
                    });
                });*//*
                bibleLinkView.setItems(filteredBibleList);*/
            }
            if (ellenTab.isSelected()) {

                obsEllenLink.clear();
                if (!searchMode.isSelected())
                    obsEllenLink.addAll(e_searcher.search(prompt, PathsEnum.EllenWhite));
                else obsEllenLink.addAll(e_searcher.search(prompt, PathsEnum.EllenWhite, e_uniqueWordH));

                sortLinkView(ellenListView.getSelectionModel().getSelectedIndex());

                /*filteredEllenList = new FilteredList<>(e_searcher.search(prompt, PathsEnum.EllenWhite), p -> true);
                *//*searchByLinkField.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredEllenList.setPredicate(data -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String[] findTexts = newValue.toLowerCase().split(" ");

                        return checkContains(data.getLinkContent().toLowerCase(), findTexts);
                    });
                });*//*
                ellenLinkView.setItems(filteredEllenList);*/
            }
            if (booksTab.isSelected()) {
                obsOtherLink.clear();
                if (!searchMode.isSelected())
                    obsOtherLink.addAll(o_searcher.search(prompt, PathsEnum.Other));
                else obsOtherLink.addAll(o_searcher.search(prompt, PathsEnum.Other, o_uniqueWordH));
                sortLinkView(otherListView.getSelectionModel().getSelectedIndex());
            }

        }
    }

    private boolean checkContains(String str, String[] arr) {
        for (String text : arr) {
            if (!str.contains(text)) return false;
        }

        return true;
    }

    private void initEraseLinkButtons() {
        eraseAllLink.setOnAction(action -> {
            obsEllenLink.clear();
            obsBibleLink.clear();
            obsOtherLink.clear();
        });
        eraseCurLink.setOnAction(action-> {
            if (bibleLinkTab.isSelected()) obsBibleLink.clear();
            else if (ellenLinkTab.isSelected()) obsEllenLink.clear();
            else obsOtherLink.clear();
        });
    }

    private void initSearchModeButton() {
        searchMode.selectedProperty().addListener(lis -> {
            if (searchMode.isSelected()) {
                toggleContainer.getStyleClass().remove("stack-pane-unselected");
                toggleContainer.getStyleClass().add("stack-pane-selected");
                searchModeLabel.setText("Поиск частей слов");
            }
            else {
                toggleContainer.getStyleClass().remove("stack-pane-selected");
                toggleContainer.getStyleClass().add("stack-pane-unselected");
                searchModeLabel.setText("Морфологический поиск");
            }
        });
        searchMode.setSelected(!searchMode.isSelected());
        searchMode.setSelected(!searchMode.isSelected());
        toggleContainer.setOnMouseClicked(action -> {
            searchMode.setSelected(!searchMode.isSelected());
        });
    }

    private void initSplitReadButton() {
        splitReadButton.selectedProperty().addListener(action -> {
            if (splitReadButton.isSelected()) {
                splitReadContainer.setVisible(true);
                splitReadMod2.setSelected(true);
            } else {
                splitReadContainer.setVisible(false);
                SplitReadComponent.getInstance(this).hide();
            }
        });
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
            defineOtherLinkSelection();

        }

        private void defineBibleLinkSelection() {

            controller.bibleLinkView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
                if (_new != null) {
                    List<IndexStruct> selectedReferences = _new.getReferences();

                    //controller.bibleLinkView.scrollTo(controller.bibleLinkView.getSelectionModel().getSelectedIndex());

                    if (_new.root == PathsEnum.Bible)
                        controller.bookTabPane.getSelectionModel().select(controller.bibleTab);
                    else controller.bookTabPane.getSelectionModel().select(controller.ellenTab);

                    controller.bibleListView.getSelectionModel().select(selectedReferences.get(0).getBookID());
                    controller.bibleListView.scrollTo(selectedReferences.get(0).getBookID());

                    controller.chapterListView.getSelectionModel().select(selectedReferences.get(0).getChapterID() - 1);
                    controller.chapterListView.scrollTo(selectedReferences.get(0).getChapterID());

                    controller.highlightText(selectedReferences, _new.getWords());
                }
            });

        }

        private void defineEllenLinkSelection() {

            controller.ellenLinkView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
                if (_new != null) {
                    List<IndexStruct> selectedReferences = _new.getReferences();
                    controller.ellenLinkView.scrollTo(controller.ellenLinkView.getSelectionModel().getSelectedIndex());

                    if (_new.root == PathsEnum.Bible)
                        controller.bookTabPane.getSelectionModel().select(controller.bibleTab);
                    else controller.bookTabPane.getSelectionModel().select(controller.ellenTab);

                    controller.ellenListView.getSelectionModel().select(selectedReferences.get(0).getBookID());
                    controller.ellenListView.scrollTo(selectedReferences.get(0).getBookID());

                    controller.chapterListView.getSelectionModel().select(selectedReferences.get(0).getChapterID() - 1);
                    controller.chapterListView.scrollTo(selectedReferences.get(0).getChapterID());

                    controller.highlightText(selectedReferences, _new.getWords());
                }
            });

        }

        private void defineOtherLinkSelection() {

            controller.otherLinkView.getSelectionModel().selectedItemProperty().addListener((_obs, _old, _new) -> {
                if (_new != null) {
                    List<IndexStruct> selectedReferences = _new.getReferences();
                    controller.otherLinkView.scrollTo(controller.otherLinkView.getSelectionModel().getSelectedIndex());

                    controller.bookTabPane.getSelectionModel().select(controller.booksTab);

                    controller.otherListView.getSelectionModel().select(selectedReferences.get(0).getBookID());
                    controller.otherListView.scrollTo(selectedReferences.get(0).getBookID());

                    controller.chapterListView.getSelectionModel().select(selectedReferences.get(0).getChapterID() - 1);
                    controller.chapterListView.scrollTo(selectedReferences.get(0).getChapterID());

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

            InputStream urlStream = Symphony.class.getResourceAsStream("buttons/erase2.png");
            if (urlStream == null) {
                throw new RuntimeException("Resource not found url");
            }

            Image image = new Image(urlStream);
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

        private ContextMenu noteContextMenu;
        private ContextMenu linkContextMenu;

        private Tooltip tooltip = new Tooltip();

        private void handle(MouseEvent mouse, StyleClassedTextArea tarea) {
            if (mouse.getButton() == MouseButton.PRIMARY) {
                noteContextMenu.hide();
                linkContextMenu.hide();
                try {
                    selectSpecialTextAction(tarea);
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                if (tarea.getSelectedText().isEmpty()) {
                    String text = tarea.getText();
                    int caretPosition = tarea.getCaretPosition();

                    int start = caretPosition;
                    int end = caretPosition;

                    while (start > 0 && Character.isLetterOrDigit(text.charAt(start - 1))) {
                        start--;
                    }

                    while (end < text.length() && Character.isLetterOrDigit(text.charAt(end))) {
                        end++;
                    }

                    tarea.selectRange(start, end + 1);
                    tarea.selectRange(start, end);
                }
            } else if (mouse.getButton() == MouseButton.SECONDARY) {
                try {
                    int pos = controller.currentTArea.hit(mouse.getX(), mouse.getY()).getInsertionIndex();
                    controller.currentTArea.moveTo(pos);
                    deleteSpecialTextAction(mouse, tarea);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        private static class Holder {
            private static final MainTextAreaComponent INSTANCE = new MainTextAreaComponent();
        }

        public static MainTextAreaComponent getInstance(MainController _controller) {
            if (controller == null) controller = _controller;
            return MainTextAreaComponent.Holder.INSTANCE;
        }

        public void initMainTextArea() {

            defineContextMenu();
            defineMainTextArea();
            defineMainTextAreaHandler();
            controller.initFocusChange();

        }

        public void defineMainTextArea() {


            controller.mainTextArea = new StyleClassedTextArea();

            controller.mainTextArea.setWrapText(true);

            controller.mainTextArea.setPadding(new Insets(0, 0, 0, 5));

            VirtualizedScrollPane scroll = new VirtualizedScrollPane(controller.mainTextArea);

            controller.mainTextArea.setStyle("""
                    -fx-font-size: 14px;
                    """);

            controller.midGridPane.getChildren().add(scroll);

            /*controller.mainGridPane.getChildren().add(scroll);
            controller.mainTextArea.setPadding(new Insets(0, 0, 0, 5));

            GridPane.setColumnIndex(scroll, 1);
            GridPane.setRowIndex(scroll, 3);
            GridPane.setHgrow(scroll, Priority.ALWAYS);
            GridPane.setVgrow(scroll, Priority.ALWAYS);
            GridPane.setMargin(scroll, new Insets(3, 0, 0, 0));*/

        }

        private void defineContextMenu() {

            noteContextMenu = new ContextMenu();
            linkContextMenu = new ContextMenu();
            MenuItem delete = new MenuItem("Удалить заметку");
            MenuItem delete1 = new MenuItem("Удалить параллельное место");
            noteContextMenu.getItems().add(delete);
            linkContextMenu.getItems().add(delete1);

        }

        public void defineMainTextAreaHandler() {

            controller.mainTextArea.setOnMouseClicked(mouse -> {
                handle(mouse, controller.mainTextArea);
            });

            for (int i = 0; i < 3; i++) {
                int finalI = i;
                SplitReadComponent.getInstance(controller).tareas.get(i).setOnMouseClicked(mouse -> {
                    handle(mouse, SplitReadComponent.getInstance(controller).tareas.get(finalI));
                });
                SplitReadComponent.getInstance(controller).tareas.get(i).addEventFilter(ScrollEvent.SCROLL, event -> {
                    if (event.isControlDown()) {
                        String currentStyle = SplitReadComponent.getInstance(controller).tareas.get(finalI).getStyle();
                        double currentFontSize = extractFontSize(currentStyle);
                        double newFontSize = currentFontSize + (event.getDeltaY() > 0 ? 1 : -1);
                        if (newFontSize > 45) newFontSize = 45;
                        if (newFontSize < 12) newFontSize = 12;
                        String newStyle = updateFontSize(currentStyle, newFontSize);
                        SplitReadComponent.getInstance(controller).tareas.get(finalI).setStyle(newStyle);
                        event.consume();
                    }
                });
            }

            controller.mainTextArea.addEventFilter(ScrollEvent.SCROLL, event -> {
                if (event.isControlDown()) {
                    String currentStyle = controller.mainTextArea.getStyle();
                    double currentFontSize = extractFontSize(currentStyle);
                    double newFontSize = currentFontSize + (event.getDeltaY() > 0 ? 1 : -1);
                    if (newFontSize > 45) newFontSize = 45;
                    if (newFontSize < 12) newFontSize = 12;
                    String newStyle = updateFontSize(currentStyle, newFontSize);
                    controller.mainTextArea.setStyle(newStyle);
                    event.consume();
                }
            });

            controller.mainTextArea.setOnMouseMoved(mouse -> {
                int index = controller.mainTextArea.hit(mouse.getX(), mouse.getY()).getInsertionIndex();
                Collection<String> style = controller.mainTextArea.getStyleOfChar(index);
                if (style != null && style.contains("note")) {
                    try {
                        showTooltip(controller.mainTextArea, mouse.getScreenX()+30, mouse.getScreenY(), controller.getNotesForSelectedChapter().getFromPos(index).text);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Tooltip.uninstall(controller.mainTextArea, tooltip);
                }
            });

        }

        private void showTooltip(StyleClassedTextArea textArea, double x, double y, String tooltipText) {
            tooltip.setText(tooltipText);
            tooltip.setShowDelay(Duration.ZERO);
            tooltip.setHideDelay(Duration.ZERO);
            Tooltip.install(textArea, tooltip);
            tooltip.show(textArea, x, y);
        }

        private String updateFontSize(String style, double newSize) {
            String[] styles = style.split(";");
            StringBuilder newStyle = new StringBuilder();
            for (String s : styles) {
                if (s.trim().startsWith("-fx-font-size")) {
                    newStyle.append("-fx-font-size: ").append(newSize).append("px;");
                } else {
                    newStyle.append(s).append(";");
                }
            }
            return newStyle.toString();
        }

        private double extractFontSize(String style) {
            String[] styles = style.split(";");
            for (String s : styles) {
                if (s.trim().startsWith("-fx-font-size")) {
                    String size = s.split(":")[1].trim().replace("px", "");
                    return Double.parseDouble(size);
                }
            }
            return 14.0;
        }

        private void deleteSpecialTextAction(MouseEvent mouse, StyleClassedTextArea tarea) throws IOException {

            int clickPos =  tarea.getCaretPosition();
            StyleSpans<Collection<String>> styles = tarea.getStyleSpans(0, tarea.getLength());
            int index = 0;
            int inote = 0;
            int ilink = 0;
            for (StyleSpan<Collection<String>> span : styles) {
                if (span.getStyle().contains("note")) {
                    if (clickPos >= index && clickPos <= index + span.getLength()) {

                        int finalInote = inote;
                        noteContextMenu.getItems().get(0).onActionProperty().set(actionEvent -> {
                            Note note = null;
                            try {
                                note = controller.getNotesForSelectedChapter().get(finalInote);
                                tarea.setStyleClass(note.from, note.to+1, "");
                                tarea.deleteText(note.to, note.to+1);
                                controller.getNotesForSelectedChapter().remove(finalInote);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        noteContextMenu.show(tarea,
                                mouse.getScreenX(), mouse.getScreenY());
                        break;
                    }
                    inote++;
                } else if (span.getStyle().contains("parallel-link")) {
                    if (clickPos >= index && clickPos <= index + span.getLength()) {

                        int finalLink = ilink;
                        linkContextMenu.getItems().get(0).onActionProperty().set(actionEvent -> {
                            ParallelLink link = null;
                            try {
                                link = controller.getParallelLinkForSelectedChapter().get(finalLink);
                                tarea.setStyleClass(link.from, link.to, "");
                                controller.getParallelLinkForSelectedChapter().remove(finalLink);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        linkContextMenu.show(tarea,
                                mouse.getScreenX(), mouse.getScreenY());
                        break;
                    }
                    ilink++;
                } else {
                    noteContextMenu.hide();
                    linkContextMenu.hide();
                }

                index += span.getLength();
            }

        }

        private void selectSpecialTextAction(StyleClassedTextArea tarea) throws IOException, URISyntaxException {

            int clickPos =  tarea.getCaretPosition();
            StyleSpans<Collection<String>> styles = tarea.getStyleSpans(0, tarea.getLength());
            int index = 0;
            int inote = 0;
            int ilink = 0;
            for (StyleSpan<Collection<String>> span : styles) {
                if (span.getStyle().contains("note")) {
                    if (clickPos >= index && clickPos <= index + span.getLength()) {
                        String title;
                        if (controller.bibleTab.isSelected())
                            title = new Cutser().getBibleCut(controller.bibleListView.getSelectionModel().getSelectedIndex());
                        else if (controller.ellenTab.isSelected())
                            title = new Cutser().getEllenCut(controller.ellenListView.getSelectionModel().getSelectedIndex());
                        else title = new Cutser().getOtherCut(controller.otherListView.getSelectionModel().getSelectedIndex());
                        title = String.format("%s %d :%s", title, controller.chapterListView.getSelectionModel().getSelectedItem(),
                                tarea.getText(index, index+span.getLength()));
                        Note note = controller.getNotesForSelectedChapter().get(inote);
                        if (!note.isOpened()) {
                            new NoteWindow(title, note).stage().show();
                        }
                        break;
                    }
                    inote++;
                } else if (span.getStyle().contains("parallel-link")) {
                    if (clickPos >= index && clickPos <= index + span.getLength()) {
                        String oldPrompt = controller.searchByLinkField.getPromptText();
                        controller.searchByLinkField.setPromptText("Добавление ссылки");
                        ParallelsLinkComponent.getInstance(controller).display(controller.getParallelLinkForSelectedChapter().get(ilink));
                        break;
                    }
                    ilink++;
                }

                index += span.getLength();
            }

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


            Button leafButton = getLeafButton(path);

            controller.midGridPane.getChildren().add(leafButton);
            GridPane.setValignment(leafButton, VPos.BOTTOM);
            GridPane.setHalignment(leafButton, pos);

            defineLeafButtonBehavior(leafButton, side);

        }

        private @NotNull Button getLeafButton(String url) {
            InputStream urlStream = Symphony.class.getResourceAsStream(url);
            if (urlStream == null) {
                throw new RuntimeException("Resource not found url");
            }

            Image image = new Image(urlStream);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);

            return getButton(imageView);
        }

        private static @NotNull Button getButton(ImageView imageView) {
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

        public void initHoverPanel() throws URISyntaxException {

            defineHoverPanel();

        }

        private void defineHoverPanel() throws URISyntaxException {

            HBox hoverSelectionPanel = new HBox();
            hoverSelectionPanel.setVisible(false);
            hoverSelectionPanel.setPrefWidth(Region.USE_COMPUTED_SIZE);
            hoverSelectionPanel.setMaxHeight(40);
            hoverSelectionPanel.setPrefHeight(40);
            hoverSelectionPanel.setMinHeight(40);
            hoverSelectionPanel.setMaxWidth(146);
            hoverSelectionPanel.getStyleClass().add("hover-selection-panel");

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
            defineHoverPaneArticleButton(hoverSelectionPanel);
            defineHoverPaneNoteButton(hoverSelectionPanel);
            defineHoverPaneParallelLinkButton(hoverSelectionPanel);

        }

        private void defineHoverPanelBehavior(HBox hoverSelectionPanel) {
            AtomicReference<Double> deltaX = new AtomicReference<>(0d);
            AtomicReference<Double> deltaY = new AtomicReference<>(0d);

            defineDeltas(deltaX, deltaY);

            controller.currentTArea.selectionProperty().addListener((ov, i1, i2) -> {

                if (i1.getStart() != i1.getEnd() && !controller.currentTArea.getSelectedText().isEmpty()) {
                    hoverSelectionPanel.setVisible(true);
                    Bounds bounds = controller.currentTArea.getCharacterBoundsOnScreen(i1.getStart(), i1.getStart()).orElse(null);

                    if (bounds != null) {
                        hoverSelectionPanel.translateXProperty().set(bounds.getMinX() - deltaX.get());
                        hoverSelectionPanel.translateYProperty().set(bounds.getMinY() - deltaY.get() - hoverSelectionPanel.getHeight());

                        if (hoverSelectionPanel.translateXProperty().get() + hoverSelectionPanel.getWidth()
                                > currentWindowWidth.get() - 200) {
                            hoverSelectionPanel.translateXProperty().set(currentWindowWidth.get() - 200 - hoverSelectionPanel.getWidth());
                        }

                    }
                }
                if (controller.currentTArea.getSelectedText().isEmpty()) {
                    hoverSelectionPanel.setVisible(false);
                }
            });

            SplitReadComponent.getInstance(controller).initSelection(hoverSelectionPanel, deltaX, deltaY);
        }

        private void defineDeltas(AtomicReference<Double> deltaX, AtomicReference<Double> deltaY) {

            controller.currentTArea.setOnMousePressed(mouseEvent -> {
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

        private void defineHoverPanelCopyButton(HBox hoverSelectionPanel) throws URISyntaxException {

            Button copyButton = new Button();
            copyButton.setTooltip(new Tooltip("Добавить в поиск"));
            copyButton.setText("");
            copyButton.setPrefWidth(26);
            copyButton.setPrefHeight(26);
            HBox.setMargin(copyButton, new Insets(0, 0, 1, 3));
            copyButton.getStyleClass().add("hover-panel-button");
            copyButton.setGraphic(defineGraphic("buttons/to-copy.png"));
            hoverSelectionPanel.alignmentProperty().set(Pos.CENTER_LEFT);
            hoverSelectionPanel.getChildren().add(copyButton);
            defineCopyButtonBehavior(copyButton);

        }

        private StackPane defineGraphic(String url) throws URISyntaxException {

            InputStream urlStream = Symphony.class.getResourceAsStream(url);
            if (urlStream == null) {
                throw new RuntimeException("Resource not found url");
            }

            Image image = new Image(urlStream);
            double k = image.getWidth()/ image.getHeight();
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(26*k);
            imageView.setFitHeight(26);

            StackPane stackPane = new StackPane(imageView);
            stackPane.setPrefSize(26, 26);
            stackPane.setMaxSize(26, 26);
            stackPane.setMinSize(26, 26);
            StackPane.setAlignment(imageView, Pos.CENTER);
            return stackPane;

        }

        private void defineHoverPanelBookmarkButton(HBox hoverSelectionPanel) throws URISyntaxException {

            Button createBookmarkButton = new Button();
            createBookmarkButton.setText("");
            createBookmarkButton.setTooltip(new Tooltip("Создать закладку"));
            createBookmarkButton.setPrefWidth(26);
            createBookmarkButton.setPrefHeight(26);
            HBox.setMargin(createBookmarkButton, new Insets(0, 0, 1, 3));
            createBookmarkButton.getStyleClass().add("hover-panel-button");
            createBookmarkButton.setGraphic(defineGraphic("buttons/to-bookmark.png"));
            hoverSelectionPanel.alignmentProperty().set(Pos.CENTER_LEFT);
            hoverSelectionPanel.getChildren().add(createBookmarkButton);
            createBookmarkButton.onActionProperty().set(actionEvent -> {
                controller.doCreateBookmark();
            });

        }

        private void defineCopyButtonBehavior(Button copyButton) {

            copyButton.onActionProperty().set(action -> {
                if (!controller.currentTArea.getSelectedText().isEmpty()) {

                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(controller.currentTArea.getSelectedText());
                    clipboard.setContent(content);
                    controller.searchByTextField.setText(controller.currentTArea.getSelectedText());

                }
            });

        }

        private void defineHoverPaneArticleButton(HBox hoverPanel) throws URISyntaxException {

            Button createArticleButton = new Button();
            createArticleButton.setText("");
            createArticleButton.setTooltip(new Tooltip("Добавить в тему"));
            createArticleButton.setPrefWidth(26);
            createArticleButton.setPrefHeight(26);
            HBox.setMargin(createArticleButton, new Insets(0, 0, 1, 3));
            createArticleButton.getStyleClass().add("hover-panel-button");
            createArticleButton.setGraphic(defineGraphic("buttons/to-article.png"));
            hoverPanel.alignmentProperty().set(Pos.CENTER_LEFT);
            hoverPanel.getChildren().add(createArticleButton);
            createArticleButton.onActionProperty().set(actionEvent -> {
                PathsEnum mode = controller.bibleTab.isSelected()? PathsEnum.Bible:
                        controller.ellenTab.isSelected()? PathsEnum.EllenWhite: PathsEnum.Other;
                ListView<Book> listView = mode == PathsEnum.Bible? controller.bibleListView:
                        mode == PathsEnum.EllenWhite? controller.ellenListView : controller.otherListView;
                HashMap<String, Integer> whelp = mode == PathsEnum.Bible? controller.b_uniqueWordH:
                        mode == PathsEnum.EllenWhite? controller.e_uniqueWordH : controller.o_uniqueWordH;

                Link link = getLink(listView, mode, whelp);

                if (mode == PathsEnum.Bible) {
                    try {
                        new ArticlesWindow(List.of(link), new ArrayList<>(), new ArrayList<>()).stage().show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (mode == PathsEnum.EllenWhite){
                    try {
                        new ArticlesWindow(new ArrayList<>(), List.of(link), new ArrayList<>()).stage().show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        new ArticlesWindow(new ArrayList<>(), new ArrayList<>(), List.of(link)).stage().show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }


            });

        }

        private static @NotNull Link getLink(ListView<Book> listView, PathsEnum mode, HashMap<String, Integer> whelp) {
            int fragmentId = 0;
            int index = 0;
            int pos = 0;
            int endPos = 0;
            String text = "";
            for (String fragment : listView.getSelectionModel().getSelectedItem().getChapters().get(
                    controller.chapterListView.getSelectionModel().getSelectedIndex()+1).fragments) {
                if (controller.currentTArea.getSelection().getStart() >= index &&
                        controller.currentTArea.getSelection().getStart() <= index + fragment.length()) {
                   pos = fragment.indexOf(controller.currentTArea.getSelectedText());
                   endPos = pos;
                   if (pos > 0) {
                       while (pos-1 > 0 && Character.isLetterOrDigit(fragment.charAt(pos-1))) {
                           pos--;
                       }
                   } else endPos = pos = 0;
                   while (endPos < fragment.length() && Character.isLetterOrDigit(fragment.charAt(endPos))) {
                        endPos++;
                   }
                   text = fragment.substring(pos, endPos);
                   break;
                }

                fragmentId++;
                index += fragment.length();
            }

            int wordKey = 0;


            wordKey = whelp.get(text.split(" ")[0].toLowerCase());


            Link link = new Link(List.of(new IndexStruct(
                    listView.getSelectionModel().getSelectedIndex(),
                    controller.chapterListView.getSelectionModel().getSelectedIndex()+1,
                    fragmentId, pos,
                    wordKey,
                    null
            )), listView.getItems(), mode, controller.currentTArea.getSelectedText());
            return link;
        }

        private void defineHoverPaneNoteButton(HBox hoverPanel) throws URISyntaxException {

            Button createNoteButton = new Button();
            createNoteButton.setText("");
            createNoteButton.setPrefWidth(26);
            createNoteButton.setPrefHeight(26);
            createNoteButton.setTooltip(new Tooltip("Создать заметку"));
            HBox.setMargin(createNoteButton, new Insets(0, 0, 1, 3));
            createNoteButton.getStyleClass().add("hover-panel-button");
            createNoteButton.setGraphic(defineGraphic("buttons/to-note.png"));
            hoverPanel.alignmentProperty().set(Pos.CENTER_LEFT);
            hoverPanel.getChildren().add(createNoteButton);
            createNoteButton.onActionProperty().set(actionEvent -> {
                try {
                    controller.doCreateNote();
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });

        }

        private void defineHoverPaneParallelLinkButton(HBox hoverPanel) throws URISyntaxException {

            Button createParallelLinkButton = new Button();
            createParallelLinkButton.setText("");
            createParallelLinkButton.setPrefWidth(26);
            createParallelLinkButton.setPrefHeight(26);
            createParallelLinkButton.setTooltip(new Tooltip("Создать параллельное место"));
            HBox.setMargin(createParallelLinkButton, new Insets(0, 0, 1, 3));
            createParallelLinkButton.getStyleClass().add("hover-panel-button");
            createParallelLinkButton.setGraphic(defineGraphic("buttons/to-parallel.png"));
            hoverPanel.alignmentProperty().set(Pos.CENTER_LEFT);
            hoverPanel.getChildren().add(createParallelLinkButton);
            createParallelLinkButton.onActionProperty().set(actionEvent -> {
                try {
                    controller.doCreateParallelLink();
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });

        }

    }

    static class ParallelsLinkComponent {

        private static MainController controller;
        private ListView<Link> listView;
        private ObservableList<Link> obs = FXCollections.observableArrayList();
        private Button close;
        private ParallelLink link;
        private boolean isOpened = false;

        private static class Holder {
            private static final ParallelsLinkComponent INSTANCE = new ParallelsLinkComponent();
        }

        public static ParallelsLinkComponent getInstance(MainController _controller) {
            if (controller == null) controller = _controller;
            return ParallelsLinkComponent.Holder.INSTANCE;
        }

        public void display(ParallelLink link) {

             if (listView == null) {
                 defineParallelLinkGraphic();
             }
             if (isOpened)
                 hide();

             this.link = link;
             isOpened = true;
             controller.mainGridPane.getChildren().addAll(listView, close);
             GridPane.setMargin(close, new Insets(8, 8, 0, 0));
             obs.addAll(link.getParallelLink());
             listView.setItems(obs);

        }

        public void hide() {
            this.link = null;
            obs.clear();
            isOpened = false;
            controller.mainGridPane.getChildren().removeAll(listView, close);
        }

        public void add(Link link) {
            if (this.link != null) {
                this.link.addLink(link);
                this.obs.add(link);
            }
        }

        public boolean isOpened() {
            return isOpened;
        }

        private void defineParallelLinkGraphic() {

            listView = new ListView<>();
            listView.setPadding(new Insets(23, 0, 0, 0));
            listView.getStyleClass().add("v-listview");
            listView.setCellFactory(cell -> new RichCell<>());
            close = new Button("");
            close.setMaxSize(15, 15);
            close.setMinSize(15, 15);
            close.setPrefSize(15, 15);
            GridPane.setColumnIndex(listView, 2);
            GridPane.setColumnIndex(close, 2);
            GridPane.setRowIndex(listView, 2);
            GridPane.setRowIndex(close, 2);
            GridPane.setRowSpan(listView, 2);
            GridPane.setMargin(listView, new Insets(3, 0, 0, 3));
            GridPane.setMargin(close, new Insets(3, 0, 0, 0));
            GridPane.setHalignment(close, HPos.RIGHT);
            GridPane.setValignment(close, VPos.TOP);
            close.onActionProperty().set(action -> {
                hide();
                controller.searchByLinkField.setPromptText("Поиск по ссылке");
            });
            defineCloseButtonGraphic();
            defineParallelLinkSelection();

        }

        private void defineParallelLinkSelection() {

            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            listView.setOnKeyPressed(key -> {
                if (key.getCode() == KeyCode.DELETE) {
                    if (link != null) {
                        link.parallelLink.removeAll(listView.getSelectionModel().getSelectedItems());
                        obs.removeAll(listView.getSelectionModel().getSelectedItems());
                    }
                }
            });

            listView.getSelectionModel().selectedItemProperty().addListener((obs, old, new_) -> {
                if (new_ != null) {
                    List<IndexStruct> selectedReferences = new_.getReferences();
                    ListView<Book> bookView;

                    if (new_.root == PathsEnum.Bible) {
                        controller.bookTabPane.getSelectionModel().select(controller.bibleTab);
                        controller.linkTabPane.getSelectionModel().select(controller.bibleLinkTab);
                        bookView = controller.bibleListView;
                    }
                    else {
                        bookView = controller.ellenListView;
                        controller.bookTabPane.getSelectionModel().select(controller.ellenTab);
                        controller.linkTabPane.getSelectionModel().select(controller.ellenLinkTab);
                    }

                    bookView.getSelectionModel().select(selectedReferences.get(0).getBookID());
                    bookView.scrollTo(selectedReferences.get(0).getBookID());

                    controller.chapterListView.getSelectionModel().select(selectedReferences.get(0).getChapterID() - 1);
                    controller.chapterListView.scrollTo(selectedReferences.get(0).getChapterID());

                    controller.highlightText(selectedReferences, new_.getWords());
                }
            });

        }

        private void defineCloseButtonGraphic() {

            InputStream urlStream = Symphony.class.getResourceAsStream("buttons/erase2.png");
            if (urlStream == null) {
                throw new RuntimeException("Resource not found url");
            }

            Image image = new Image(urlStream);

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(15);
            imageView.setFitHeight(15);

            StackPane stackPane = new StackPane(imageView);
            stackPane.setPrefSize(13, 13);
            stackPane.setMaxSize(13, 13);
            stackPane.setMinSize(13, 13);
            StackPane.setAlignment(imageView, Pos.CENTER);

            close.setGraphic(stackPane);
            close.getStyleClass().add("erase-button");

        }


    }

    static class SplitReadComponent {
        private static MainController controller;

        private List<SplitPane> spanes = new ArrayList<>();
        private List<GridPane> gpanes = new ArrayList<>();

        private List<StyleClassedTextArea> tareas = new ArrayList<>();

        private List<OpenChapterData> chapterData = new ArrayList<>();


        SplitReadComponent() {

            for (int i = 0; i < 3; i++) {
                SplitPane s = new SplitPane();
                GridPane g = new GridPane();
                StyleClassedTextArea t = new StyleClassedTextArea();
                t.setStyle("-fx-font-size: 14px");
                OpenChapterData cd = new OpenChapterData();
                SplitPane.setResizableWithParent(g, true);

                g.setPrefWidth(Region.USE_COMPUTED_SIZE);
                g.setPrefHeight(Region.USE_COMPUTED_SIZE);
                s.getItems().add(g);
                t.setWrapText(true);
                t.setEditable(false);
                t.setPadding(new Insets(0, 0, 0, 5));

                VirtualizedScrollPane v = new VirtualizedScrollPane(t);
                GridPane.setHgrow(v, Priority.ALWAYS);
                GridPane.setVgrow(v, Priority.ALWAYS);

                g.getChildren().add(v);

                gpanes.add(g);
                spanes.add(s);
                tareas.add(t);
                chapterData.add(cd);
            }

        }

        private static class Holder {
            private static final SplitReadComponent INSTANCE = new SplitReadComponent();
        }

        public static SplitReadComponent getInstance(MainController _controller) {
            if (controller == null) controller = _controller;
            return SplitReadComponent.Holder.INSTANCE;
        }

        private void display(int mode) {

            int s = controller.mainSplitPane.getItems().size()-1;

            if (mode < s) {
                for (int i = 2; i >= mode; i--) {
                    controller.mainSplitPane.getItems().remove(spanes.get(i));
                }
                controller.currentTArea = controller.mainTextArea;
            } else {
                for (int i = s; i < mode; i++) {
                    controller.currentTArea = tareas.get(i);
                    controller.chapterListView.getSelectionModel().select(-1);
                    controller.chapterListView.getSelectionModel().select(controller.mainCD.getChapter());
                    controller.mainSplitPane.getItems().add(spanes.get(i));
                    chapterData.get(i).copy(controller.mainCD);
                }
            }

            double dpos = 1d/controller.mainSplitPane.getItems().size();
            double[] poses = new double[controller.mainSplitPane.getItems().size()];
            for (int i = 0; i < poses.length; i++) poses[i] = dpos*(i+1);
            controller.mainSplitPane.setDividerPositions(poses);

        }

        private void initFocused() {

            for (int i = 0; i < 3; i++) {
                int finalI = i;
                tareas.get(i).focusedProperty().addListener(lis -> {
                    controller.chapterListView.getSelectionModel().selectedItemProperty().removeListener(controller.selectedChapterListener);

                    controller.currentTArea = tareas.get(finalI);

                    ListView<Book> lb;

                    controller.bookTabPane.getSelectionModel().select(chapterData.get(finalI).getTab());
                    if (chapterData.get(finalI).getTab() == 0)
                        lb = controller.bibleListView;
                    else if (chapterData.get(finalI).getTab() == 1)
                        lb = controller.ellenListView;
                    else lb = controller.otherListView;

                    lb.getSelectionModel().select(chapterData.get(finalI).getBook());
                    lb.scrollTo(chapterData.get(finalI).getBook());

                    controller.chapterListView.getSelectionModel().select(chapterData.get(finalI).getChapter());
                    controller.chapterListView.scrollTo(chapterData.get(finalI).getChapter());

                    controller.chapterListView.getSelectionModel().selectedItemProperty().addListener(controller.selectedChapterListener);
                });

            }

        }

        private void initSelection(HBox hoverSelectionPanel, AtomicReference<Double> deltaX, AtomicReference<Double> deltaY) {

            for (int i = 0; i < 3; i++) {
                tareas.get(i).setOnMousePressed(mouseEvent -> {
                    deltaX.set(HoverPanelComponent.getInstance(controller).getDeltaX(mouseEvent));
                    deltaY.set(HoverPanelComponent.getInstance(controller).getDeltaY(mouseEvent));
                });

                tareas.get(i).selectionProperty().addListener((ov, i1, i2) -> {

                    if (i1.getStart() != i1.getEnd() && !controller.currentTArea.getSelectedText().isEmpty()) {
                        hoverSelectionPanel.setVisible(true);
                        Bounds bounds = controller.currentTArea.getCharacterBoundsOnScreen(i1.getStart(), i1.getStart()).orElse(null);

                        if (bounds != null) {
                            hoverSelectionPanel.translateXProperty().set(bounds.getMinX() - deltaX.get());
                            hoverSelectionPanel.translateYProperty().set(bounds.getMinY() - deltaY.get() - hoverSelectionPanel.getHeight());

                            if (hoverSelectionPanel.translateXProperty().get() + hoverSelectionPanel.getWidth()
                                    > currentWindowWidth.get() - 200) {
                                hoverSelectionPanel.translateXProperty().set(currentWindowWidth.get() - 200 - hoverSelectionPanel.getWidth());
                            }

                        }
                    }
                    if (controller.currentTArea.getSelectedText().isEmpty()) {
                        hoverSelectionPanel.setVisible(false);
                    }

                });
            }

        }

        private OpenChapterData getChapterData(StyleClassedTextArea tarea) {
            for (int i = 0; i < 3; i++) {
                if (tarea.equals(tareas.get(i))) {
                    return chapterData.get(i);
                }
            }
            return chapterData.get(0);
        }

        private void hide() {

            for (int i = 2; i >= 0; i--) {
                controller.mainSplitPane.getItems().remove(spanes.get(i));
            }
            controller.currentTArea = controller.mainTextArea;

        }

    }

}