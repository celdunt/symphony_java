package loc.ex.symphony.indexdata;

import loc.ex.symphony.file.FileAdapter;
import loc.ex.symphony.listview.Book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Indexator {

    private final ConcurrentHashMap<String, List<IndexStruct>> indexData = new ConcurrentHashMap<>();

    public void index() throws IOException {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<Book> bible = new FileAdapter().getBible();


        for (int bookId = 0; bookId < bible.size(); bookId++) {
            for (int chapterId = 0; chapterId < bible.get(bookId).getChapters().size(); chapterId++) {
                for (int fragmentId = 0; fragmentId < bible.get(bookId).getChapters().get(chapterId).getFragments().size(); fragmentId++) {

                    int finalBookId = bookId;
                    int finalChapterId = chapterId;
                    int finalFragmentId = fragmentId;
                    Runnable task = () -> {
                        String fragment = bible.get(finalBookId).getChapters().get(finalChapterId).getFragments().get(finalFragmentId);
                        String[] words = fragment.split("[\\s\\p{Punct}]+");
                        int currentWordPosition = 0;
                        for (String word : words) {
                            IndexStruct index = new IndexStruct();
                            index.bookId.set(finalBookId);
                            index.chapterId.set(finalChapterId);
                            index.fragmentId.set(finalFragmentId);
                            index.position.set(currentWordPosition);
                            index.wordLength.set(word.length());
                            currentWordPosition += word.length();

                            indexData.computeIfAbsent(word, k -> new ArrayList<>()).add(index);
                        }
                    };

                    executor.execute(task);
                }
            }
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

    }

    public ConcurrentHashMap<String, List<IndexStruct>> getIndexData() {
        return indexData;
    }
}
