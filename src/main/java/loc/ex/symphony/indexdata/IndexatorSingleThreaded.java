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
    //private final DbHandler dbHandler;


    public IndexatorSingleThreaded(ObservableList<Book> books) throws SQLException, ClassNotFoundException {
        this.books = books;
        //dbHandler = new DbHandler();
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
        logger.info("number of book :-> " + books.size());
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

                        /*
                        char key;
                        if (word.isEmpty()) continue;
                        key = word.toLowerCase().charAt(0);

                        dictionary.computeIfAbsent(key, a -> new HashMap<>())
                                .computeIfAbsent(word.toLowerCase(), k -> new ArrayList<>()).add(index);*/

                        //dbHandler.put(index);
                    }
                }
            }

            logger.info("1st Stage: \"basic idx\", percent of success: " + String.format("%.2f", ((bookId+1) * 100.0)/ books.size()) + "%");
        }

        List<String[]> morphSynonymGroupsOfWords = getMorphSynonymGroupOfWords();

        int varOfSuccess = 1;

        for (String[] morphSynonymGroupsOfWord : morphSynonymGroupsOfWords) {
            for (int fixedWord = 0; fixedWord < morphSynonymGroupsOfWord.length; fixedWord++)
                for (int nextWord = fixedWord + 1; nextWord < morphSynonymGroupsOfWord.length; nextWord++) {
                    //List<IndexStruct> fixedStruct = indexData.get(morphSynonymGroupsOfWord[fixedWord]);
                    char keyOfFixed;
                    char keyOfNext;

                    if (morphSynonymGroupsOfWord[fixedWord].isEmpty()) continue;
                    if (morphSynonymGroupsOfWord[nextWord].isEmpty()) continue;

                    List<IndexStruct> fixed = indexData.get(morphSynonymGroupsOfWord[fixedWord]);
                    List<IndexStruct> next = indexData.get(morphSynonymGroupsOfWord[nextWord]);

                    if (fixed == null || fixed.isEmpty() || next == null || next.isEmpty()) continue;

                    fixed.getFirst().getSynonymsKeys().add(uniqueWordsHelp.get(morphSynonymGroupsOfWord[nextWord]));
                    next.getFirst().getSynonymsKeys().add(uniqueWordsHelp.get(morphSynonymGroupsOfWord[fixedWord]));


                   /* keyOfFixed = morphSynonymGroupsOfWord[fixedWord].toLowerCase().charAt(0);
                    keyOfNext = morphSynonymGroupsOfWord[nextWord].toLowerCase().charAt(0);

                    if (dictionary.containsKey(keyOfFixed) && dictionary.containsKey(keyOfNext)) {
                        List<IndexStruct> fixedStruct = dictionary.get(keyOfFixed).get(morphSynonymGroupsOfWord[fixedWord]);
                        //List<IndexStruct> nextStruct = indexData.get(morphSynonymGroupsOfWord[nextWord]);
                        List<IndexStruct> nextStruct = dictionary.get(keyOfNext).get(morphSynonymGroupsOfWord[nextWord]);

                        if (fixedStruct == null || nextStruct == null) continue;

                        fixedStruct.get(0).getSynonyms().add(morphSynonymGroupsOfWord[nextWord]);
                        nextStruct.get(0).getSynonyms().add(morphSynonymGroupsOfWord[fixedWord]);
                    }*/

                   /* List<IndexStruct> fixed = dbHandler.get(morphSynonymGroupsOfWord[fixedWord]);
                    List<IndexStruct> next = dbHandler.get(morphSynonymGroupsOfWord[nextWord]);

                    if (fixed.isEmpty() || next.isEmpty()) continue;

                    dbHandler.addSynonym(fixed.getFirst().getId(), morphSynonymGroupsOfWord[nextWord]);
                    dbHandler.addSynonym(next.getFirst().getId(), morphSynonymGroupsOfWord[fixedWord]);*/
                }

            logger.info("2st Stage: \"next idx\", percent of success: " + String.format("%.2f", ((varOfSuccess++) * 100.0)/ morphSynonymGroupsOfWords.size()) + "%");
        }
    }

    private List<String[]> getMorphSynonymGroupOfWords() throws IOException {
        Path filePath = Paths.get("index/words.txt");
        String wordsString = "";

        if (Files.exists(filePath)) wordsString = Files.readString(filePath, Charset.forName("windows-1251"));
        else logger.info("can't load morph-synonym group of words file!");

        String[] groupsOfWords = wordsString.split("г1лава");

        logger.info("number of groups: " + groupsOfWords.length);

        List<String[]> handledGroupsOfWords = new ArrayList<>();

        for (String groupOfWords : groupsOfWords) {
            handledGroupsOfWords.add(groupOfWords.split("[^a-zA-Zа-яА-Я0-9]+"));
        }

        return handledGroupsOfWords;
    }
}
