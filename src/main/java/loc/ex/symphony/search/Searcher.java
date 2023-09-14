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

    /*

        НЕОБХОДИМО ЖОСТКО ПРОТЕСТИРОВАТЬ МЕТОД СЁРЧ!!!!!!

     */


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
            List<List<IndexStruct>> wordsData = new ArrayList<>();

            for (String word : words) {
                List<IndexStruct> another = indexData.get(word.toLowerCase());

                if (another == null) return FXCollections.observableArrayList(foundOccurrences);

                for (String synonym : another.get(0).getSynonyms()) another.addAll(indexData.get(synonym));

                Collections.sort(another);

                wordsData.add(another);
            }


            List<IndexStruct> references = new ArrayList<>();
            List<ListIterator<IndexStruct>> iterators = new ArrayList<>();

            for (List<IndexStruct> wordData : wordsData) {
                iterators.add(wordData.listIterator());
                references.add(wordData.get(0));
            }

            while (adjustBookID(references, iterators)) {
                foundOccurrences.add(new Link(references, resource, words));
                if (iterators.get(0).hasNext()) references.set(0, iterators.get(0).next());
                else break;
            }

        }


        return FXCollections.observableArrayList(foundOccurrences);
    }


    /**
     *
     * Returns "false" if them couldn't find equal parameters "bookID"
     *
     * @param references This parameter will be changed so that all elements of this parameter will have the same "bookID" parameter
     * @param iterators This parameter stores a list of IndexStruct
     *
     */
    private boolean adjustBookID(List<IndexStruct> references, List<ListIterator<IndexStruct>> iterators) {
        for (int iref = 0; iref < references.size(); iref++) {
            int countEquals = 0;

            for (int jref = 0; jref < references.size(); jref++) {
                if (iref != jref) {

                    if (references.get(iref).getBookID() < references.get(jref).getBookID()) {
                        while(references.get(iref).getBookID() < references.get(jref).getBookID()) {
                            if (iterators.get(iref).hasNext())
                                references.set(iref, iterators.get(iref).next());
                            else
                                return false;
                        }
                        break;
                    }
                    while (references.get(jref).getBookID() < references.get(iref).getBookID()) {
                        if (iterators.get(jref).hasNext())
                            references.set(jref, iterators.get(jref).next());
                        else
                            return false;
                    }
                    if (references.get(iref).getBookID() == references.get(jref).getBookID()) countEquals++;
                }
            }

            if (countEquals == references.size()-1) {
                return adjustChapterID(references, iterators);
            }

        }

        return false;
    }


    /**
     *
     * Returns "false" if them couldn't find equal parameters "chapterID"
     *
     * @param references This parameter will be changed so that all elements of this parameter will have the same "chapterID" parameter
     * @param iterators his parameter stores a list of IndexStruct
     *
     */
    private boolean adjustChapterID(List<IndexStruct> references, List<ListIterator<IndexStruct>> iterators) {
        for (int iref = 0; iref < references.size(); iref++) {
            int countEquals = 0;

            for (int jref = 0; jref < references.size(); jref++) {
                if (iref != jref) {

                    if (references.get(iref).getChapterID() < references.get(jref).getChapterID()) {
                        while(references.get(iref).getChapterID() < references.get(jref).getChapterID()) {
                            if (iterators.get(iref).hasNext())
                                references.set(iref, iterators.get(iref).next());
                            else
                                return false;
                        }
                        break;
                    }
                    while (references.get(jref).getChapterID() < references.get(iref).getChapterID()) {
                        if (iterators.get(jref).hasNext())
                            references.set(jref, iterators.get(jref).next());
                        else
                            return false;
                    }
                    if (references.get(iref).getChapterID() == references.get(jref).getChapterID()) countEquals++;

                }
            }

            if (countEquals == references.size()-1) {
                return adjustFragmentID(references, iterators);
            }

        }

        return false;
    }

    /**
     *
     * Returns "false" if them couldn't find equal parameters "fragmentID"
     *
     * @param references This parameter will be changed so that all elements of this parameter will have the same "fragmentID" parameter
     * @param iterators his parameter stores a list of IndexStruct
     *
     */
    private boolean adjustFragmentID(List<IndexStruct> references, List<ListIterator<IndexStruct>> iterators) {
        for (int iref = 0; iref < references.size(); iref++) {
            int countEquals = 0;

            for (int jref = 0; jref < references.size(); jref++) {
                if (iref != jref) {

                    if (references.get(iref).getFragmentID() < references.get(jref).getFragmentID()) {
                        while(references.get(iref).getFragmentID() < references.get(jref).getFragmentID()) {
                            if (iterators.get(iref).hasNext())
                                references.set(iref, iterators.get(iref).next());
                            else
                                return false;
                        }
                        break;
                    }
                    while (references.get(jref).getFragmentID() < references.get(iref).getFragmentID()) {
                        if (iterators.get(jref).hasNext())
                            references.set(jref, iterators.get(jref).next());
                        else
                            return false;
                    }
                    if (references.get(iref).getFragmentID() == references.get(jref).getFragmentID()) countEquals++;

                }
            }

            if (countEquals == references.size()-1) {
                return true;
            }

        }

        return false;
    }
}
