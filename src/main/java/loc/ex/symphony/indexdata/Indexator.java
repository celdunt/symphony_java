package loc.ex.symphony.indexdata;

import javafx.collections.ObservableList;
import loc.ex.symphony.listview.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Indexator {

    private final ConcurrentHashMap<String, List<IndexStruct>> indexData = new ConcurrentHashMap<>();

    private final ObservableList<Book> bible;

    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
    public Indexator(ObservableList<Book> books) {
        this.bible = books;
    }
    
    public void index() {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();

        logger.info("number of available threads :-> " + numberOfThreads);

        logger.info("number of book :-> " + bible.size());

        for (int bookId = 0; bookId < bible.size(); bookId++) {
            for (int chapterId = 0; chapterId < bible.get(bookId).getChapters().size(); chapterId++) {

                List<String> fragments = bible.get(bookId).getChapters().get(chapterId).getFragments();
                int periodValue = fragments.size() / numberOfThreads;
                int upperBound;
                for (int i = 0; i < numberOfThreads; i++) {
                    if (i == numberOfThreads-1) upperBound = fragments.size();
                    else upperBound = i * periodValue + periodValue;
                    ThreadIndexation thread = new ThreadIndexation(bookId, chapterId, i * periodValue, upperBound, fragments);
                    thread.start();
                }

            }

            logger.info("1st stage: \"basic indexation\", percent of success :-> " + String.format("%.2f", ((bookId+1) * 100.0)/bible.size()) + "%");
        }
    }

    public ConcurrentHashMap<String, List<IndexStruct>> getIndexData() {
        return indexData;
    }


    private class ThreadIndexation extends Thread {

        private Thread _this;

        private final int bookId;
        private final int chapterId;
        private int start;
        private final int end;

        private final List<String> fragments;

        public ThreadIndexation(int bookId, int chapterId, int start, int end, List<String> fragments) {
            this.bookId = bookId;
            this.chapterId = chapterId;
            this.start = start;
            this.end = end;

            this.fragments = fragments;
        }

        public void run() {
            for (;start < end; start++) {
                String[] words = fragments.get(start).split("[\\s\\p{Punct}]+");
                int currentWordPosition = 0;
                for (String word : words) {
                    IndexStruct index = new IndexStruct();
                    index.setBookId(bookId);
                    index.setChapterId(chapterId);
                    index.setFragmentId(start);
                    index.setPosition(currentWordPosition);
                    index.setWordLength(word.length());
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
                name.append((char)new Random().nextInt(1, 32));

            return name.toString();
        }
    }
}
