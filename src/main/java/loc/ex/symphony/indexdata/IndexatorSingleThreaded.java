package loc.ex.symphony.indexdata;

import javafx.collections.ObservableList;
import loc.ex.symphony.listview.Book;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class IndexatorSingleThreaded {

    private final HashMap<Character, HashMap<String, List<IndexStruct>>> dictionary = new HashMap<>();
    private final HashMap<String, List<IndexStruct>> indexData = new HashMap<>();
    private final HashMap<Integer, String> uniqueWords = new HashMap<>();

    private final HashMap<String, Integer> uniqueWordsHelp = new HashMap<>();
    private final ObservableList<Book> books;
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    public IndexatorSingleThreaded(ObservableList<Book> books) throws SQLException, ClassNotFoundException {
        this.books = books;
    }

    public HashMap<String, List<IndexStruct>> getIndexData() {
        return indexData;
    }
    public HashMap<Character, HashMap<String, List<IndexStruct>>> getDictionary() {
        return dictionary;
    }
    public HashMap<Integer, String> getUniqueWords() {
        return uniqueWords;
    }
    public HashMap<String, Integer> getUniqueWordsHelp() {
        return uniqueWordsHelp;
    }

    public void index() throws IOException {
        Integer keyOfUniqueWord = 1;

        for (int bookId = 0; bookId < books.size(); bookId++) {
            for (int chapterId = 0; chapterId < books.get(bookId).getChapters().size(); chapterId++) {
                List<String> fragments = books.get(bookId).getChapters().get(chapterId).getFragments();
                for (int fragmentId = 0; fragmentId < fragments.size(); fragmentId++) {
                    String[] words = fragments.get(fragmentId).split("[^a-zA-Zа-яА-Я0-9]+");//\s\p{Punct}]+
                    int currentWordPosition = 0;
                    for (String word_ : words) {
                        String word = word_.toLowerCase();
                        IndexStruct index = new IndexStruct();
                        index.setBookID(bookId);
                        index.setChapterID(chapterId);
                        index.setFragmentID(fragmentId);
                        index.setPosition(currentWordPosition);
                        currentWordPosition += word.length();

                        if (!uniqueWordsHelp.containsKey(word)) {
                            index.setWordKey(keyOfUniqueWord);
                            uniqueWords.put(keyOfUniqueWord, word);
                            uniqueWordsHelp.put(word, keyOfUniqueWord++);
                        } else {
                            index.setWordKey(uniqueWordsHelp.get(word));
                        }

                        indexData.computeIfAbsent(word.toLowerCase(), k -> new ArrayList<>()).add(index);
                    }
                }
            }
        }

        List<String[]> morphSynonymGroupsOfWords = getMorphSynonymGroupOfWords();

        for (String[] morphSynonymGroupsOfWord : morphSynonymGroupsOfWords) {
            for (int fixedWord = 0; fixedWord < morphSynonymGroupsOfWord.length; fixedWord++)
                for (int nextWord = fixedWord + 1; nextWord < morphSynonymGroupsOfWord.length; nextWord++) {

                    if (morphSynonymGroupsOfWord[fixedWord].isEmpty()) continue;
                    if (morphSynonymGroupsOfWord[nextWord].isEmpty()) continue;

                    List<IndexStruct> fixed = indexData.get(morphSynonymGroupsOfWord[fixedWord]);
                    List<IndexStruct> next = indexData.get(morphSynonymGroupsOfWord[nextWord]);

                    if (fixed == null || fixed.isEmpty() || next == null || next.isEmpty()) continue;

                    fixed.get(0).getSynonymsKeys().add(uniqueWordsHelp.get(morphSynonymGroupsOfWord[nextWord]));
                    next.get(0).getSynonymsKeys().add(uniqueWordsHelp.get(morphSynonymGroupsOfWord[fixedWord]));

                }
        }
    }

    private List<String[]> getMorphSynonymGroupOfWords() throws IOException {
        Path filePath = Paths.get("index/words.txt");
        String wordsString = "";

        if (Files.exists(filePath)) wordsString = Files.readString(filePath, Charset.forName("windows-1251"));

        String[] groupsOfWords = wordsString.split("г1лава");

        List<String[]> handledGroupsOfWords = new ArrayList<>();

        for (String groupOfWords : groupsOfWords) {
            handledGroupsOfWords.add(groupOfWords.toLowerCase().split("[^a-zA-Zа-яА-Я0-9]+"));
        }

        return handledGroupsOfWords;
    }
}
