package loc.ex.symphony.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import loc.ex.symphony.indexdata.IndexSaver;
import loc.ex.symphony.indexdata.IndexStruct;
import loc.ex.symphony.listview.Book;
import loc.ex.symphony.listview.Link;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Searcher {

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private ObservableList<Book> resource;
    private final ConcurrentHashMap<String, List<IndexStruct>> indexData;

    public Searcher() {
        indexData = IndexSaver.load();
    }

    public void setResource(ObservableList<Book> resource) {
        this.resource = resource;
    }

    /**
     *
     * @param prompt This user search request parameter
     * @return List of Link
     *
     */
    public ObservableList<Link> search(String prompt) {
        List<Link> foundOccurrences = new ArrayList<>();

        if (indexData != null) {
            String[] words = prompt.split("[\\s\\p{Punct}]+");
            List<List<IndexStruct>> primaryWordsList = new ArrayList<>();
            List<List<IndexStruct>> synonymsWordsList = new ArrayList<>();


            for (String word : words) {
                List<IndexStruct> primaryWords = indexData.get(word.toLowerCase());
                List<IndexStruct> synonymsWords = new ArrayList<>();
                for (String synonym : primaryWords.get(0).getSynonyms()) synonymsWords.addAll(indexData.get(synonym));

                Collections.sort(primaryWords);
                Collections.sort(synonymsWords);

                primaryWordsList.add(primaryWords);
                synonymsWordsList.add(synonymsWords);
            }

            // реализовать функцию сочетания данных -> берем 0-вой элемент и подбираем под него подходящие прочие элементы.

        }


        return FXCollections.observableArrayList(foundOccurrences);
    }
}
