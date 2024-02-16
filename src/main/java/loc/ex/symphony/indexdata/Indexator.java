package loc.ex.symphony.indexdata;

import javafx.collections.ObservableList;
import loc.ex.symphony.listview.Book;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Indexator {

    private final ConcurrentHashMap<String, List<IndexStruct>> indexData = new ConcurrentHashMap<>();

    private final ObservableList<Book> books;

    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public Indexator(ObservableList<Book> books) {
        this.books = books;
    }

    public void index() throws IOException {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();

        logger.info("number of available threads :-> " + numberOfThreads);
        logger.info("number of book :-> " + books.size());

        for (int bookId = 0; bookId < books.size(); bookId++) {
            for (int chapterId = 0; chapterId < books.get(bookId).getChapters().size(); chapterId++) {

                List<String> fragments = books.get(bookId).getChapters().get(chapterId).getFragments();
                int periodValue = fragments.size() / numberOfThreads;
                int upperBound;
                for (int i = 0; i < numberOfThreads; i++) {
                    if (i == numberOfThreads - 1) upperBound = fragments.size();
                    else upperBound = i * periodValue + periodValue;
                    ThreadBasicIndexation thread = new ThreadBasicIndexation(bookId, chapterId, i * periodValue, upperBound, fragments);
                    thread.start();
                }

            }

            logger.info("1st stage: \"basic indexation\", percent of success :-> " + String.format("%.2f", ((bookId + 1) * 100.0) / books.size()) + "%");
        }

        Path filePath = Paths.get("index/words.txt");
        String wordsString = "";
        if (Files.exists(filePath)) wordsString = Files.readString(filePath, Charset.forName("windows-1251"));
        else logger.info("file path is doesn't exist");
        String[] groupsOfWords = wordsString.split("г1лава");

        logger.info("number of groups :-> " + groupsOfWords.length);

        List<String[]> handledGroupsOfWords = new ArrayList<>();
        for (String groupOfWords : groupsOfWords)
            handledGroupsOfWords.add(groupOfWords.split("[\\s\\p{Punct}]+"));

        if (numberOfThreads > 2) numberOfThreads /= 2;

        int periodGroupValue = handledGroupsOfWords.size() / numberOfThreads;
        int upperGroupBound;
        for (int i = 0; i < numberOfThreads; i++) {
            if (i == numberOfThreads - 1) upperGroupBound = handledGroupsOfWords.size();
            else upperGroupBound = i * periodGroupValue + periodGroupValue;
            ThreadNextIndexation thread = new ThreadNextIndexation(handledGroupsOfWords, i * periodGroupValue, upperGroupBound);
            thread.start();
        }

        logger.info("2nd stage is completed");
    }

    public ConcurrentHashMap<String, List<IndexStruct>> getIndexData() {
        return indexData;
    }


    private class ThreadNextIndexation extends Thread {
        private Thread _this;

        private final List<String[]> words;

        private int start;
        private final int end;

        public ThreadNextIndexation(List<String[]> words, int start, int end) {
            this.words = words;
            this.start = start;
            this.end = end;
        }

        public void run() {
            for (; start < end; start++)
                for (int fixedWord = 0; fixedWord < words.get(start).length; fixedWord++)
                    for (int nextWord = fixedWord + 1; nextWord < words.get(start).length; nextWord++) {
                        List<IndexStruct> fixedStruct = indexData.get(words.get(start)[fixedWord]);
                        List<IndexStruct> nextStruct = indexData.get(words.get(start)[nextWord]);
                        if (fixedStruct == null || nextStruct == null) continue;
                        fixedStruct.get(0).getSynonyms().add(words.get(start)[nextWord]);
                        nextStruct.get(0).getSynonyms().add(words.get(start)[fixedWord]);
                    }
        }

        public void start() {
            if (_this == null) {
                _this = new Thread(this, generateThreadName());
                _this.start();
            }
        }

        private String generateThreadName() {
            StringBuilder name = new StringBuilder();

            for (int i = 0; i < new Random().nextInt(10, 25); i++)
                name.append((char) new Random().nextInt(1, 32));

            return name.toString();
        }
    }

    private class ThreadBasicIndexation extends Thread {

        private Thread _this;

        private final int bookId;
        private final int chapterId;
        private int start;
        private final int end;

        private final List<String> fragments;

        public ThreadBasicIndexation(int bookId, int chapterId, int start, int end, List<String> fragments) {
            this.bookId = bookId;
            this.chapterId = chapterId;
            this.start = start;
            this.end = end;

            this.fragments = fragments;
        }

        public void run() {
            for (; start < end; start++) {
                String[] words = fragments.get(start).split("[\\s\\p{Punct}]+");
                int currentWordPosition = 0;
                for (String word : words) {
                    IndexStruct index = new IndexStruct();
                    index.setBookID(bookId);
                    index.setChapterID(chapterId);
                    index.setFragmentID(start);
                    index.setPosition(currentWordPosition);
                    index.setWordLength(word.length());
                    index.setWord(word);
                    currentWordPosition += word.length();

                    indexData.computeIfAbsent(word.toLowerCase(), k -> new ArrayList<>()).add(index);
                }
            }
        }

        public void start() {
            if (_this == null) {
                _this = new Thread(this, generateThreadName());
                _this.start();
            }
        }

        private String generateThreadName() {
            StringBuilder name = new StringBuilder();

            for (int i = 0; i < new Random().nextInt(8, 15); i++)
                name.append((char) new Random().nextInt(1, 32));

            return name.toString();
        }
    }
}
