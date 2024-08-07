package loc.ex.symphony.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import loc.ex.symphony.indexdata.IndexSaver;
import loc.ex.symphony.indexdata.IndexStruct;
import loc.ex.symphony.listview.Book;
import loc.ex.symphony.listview.Link;

import java.util.*;
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
            List<List<IndexStruct>> onlyPrimaryWordsList = new ArrayList<>();
            List<List<IndexStruct>> fullWordsList = new ArrayList<>();

            for (String word : words) {
                List<IndexStruct> primaryWords = indexData.get(word.toLowerCase());
                List<IndexStruct> fullWords = new ArrayList<>(primaryWords);

                for (String synonym : primaryWords.get(0).getSynonyms()) fullWords.addAll(indexData.get(synonym));

                Collections.sort(primaryWords);
                Collections.sort(fullWords);

                onlyPrimaryWordsList.add(primaryWords);
                fullWordsList.add(fullWords);
            }

            List<IndexStruct> references = new ArrayList<>();
            List<ListIterator<IndexStruct>> iterators = new ArrayList<>();

            fillReferencesAndIterators(references, iterators, onlyPrimaryWordsList);
            selectionOfValidEntries(references, iterators, foundOccurrences, words);

            fillReferencesAndIterators(references, iterators, fullWordsList);
            selectionOfValidEntries(references, iterators, foundOccurrences, words);
        }


        return FXCollections.observableArrayList(foundOccurrences);
    }

    private void selectionOfValidEntries(List<IndexStruct> references, List<ListIterator<IndexStruct>> iterators,
                                         List<Link> foundOccurrences, String[] words) {

        int fixedID = 0;
        boolean isContinue = !references.isEmpty();

        while (isContinue) {
            int countOfValid = 1;
            for (int i = fixedID+1; i < references.size(); i++) {

                if (references.get(fixedID).getBookID() == references.get(i).getBookID()) {

                    if (references.get(fixedID).getChapterID() == references.get(i).getChapterID()) {

                        if (references.get(fixedID).getFragmentID() == references.get(i).getFragmentID())
                            countOfValid++;
                        else {
                            while(references.get(fixedID).getFragmentID() > references.get(i).getFragmentID() &&
                                    references.get(fixedID).getBookID() == references.get(i).getBookID() &&
                                    references.get(fixedID).getChapterID() == references.get(i).getChapterID()) {
                                if (iterators.get(i).hasNext()) references.set(i, iterators.get(i).next());
                                else {
                                    isContinue = false;
                                    break;
                                }
                            }
                            while(references.get(fixedID).getFragmentID() < references.get(i).getFragmentID() &&
                                    references.get(fixedID).getBookID() == references.get(i).getBookID() &&
                                    references.get(fixedID).getChapterID() == references.get(i).getChapterID()) {
                                if (iterators.get(fixedID).hasNext()) references.set(fixedID, iterators.get(fixedID).next());
                                else {
                                    isContinue = false;
                                    break;
                                }
                            }
                        }

                    } else {
                        while(references.get(fixedID).getChapterID() > references.get(i).getChapterID() &&
                                references.get(fixedID).getBookID() == references.get(i).getBookID()) {
                            if (iterators.get(i).hasNext()) references.set(i, iterators.get(i).next());
                            else {
                                isContinue = false;
                                break;
                            }
                        }
                        while(references.get(fixedID).getChapterID() < references.get(i).getChapterID() &&
                                references.get(fixedID).getBookID() == references.get(i).getBookID()) {
                            if (iterators.get(fixedID).hasNext()) references.set(fixedID, iterators.get(fixedID).next());
                            else {
                                isContinue = false;
                                break;
                            }
                        }
                    }

                } else {
                    while(references.get(fixedID).getBookID() > references.get(i).getBookID()) {
                        if (iterators.get(i).hasNext()) references.set(i, iterators.get(i).next());
                        else {
                            isContinue = false;
                            break;
                        }
                    }
                    while(references.get(fixedID).getBookID() < references.get(i).getBookID()) {
                        if (iterators.get(fixedID).hasNext()) references.set(fixedID, iterators.get(fixedID).next());
                        else {
                            isContinue = false;
                            break;
                        }
                    }
                }


            }

            if (countOfValid == references.size()) {
                Link toAdd = new Link(references, resource, words);
                if (foundOccurrences.stream().noneMatch(x -> x.toString().equals(toAdd.toString())))
                    foundOccurrences.add(toAdd);
                if (iterators.get(fixedID).hasNext()) references.set(fixedID, iterators.get(fixedID).next());
                else isContinue = false;
            }

        }

    }

    private void fillReferencesAndIterators(List<IndexStruct> references, List<ListIterator<IndexStruct>> iterators,
                                            List<List<IndexStruct>> wordsMatrix) {

        references.clear();
        iterators.clear();

        for (List<IndexStruct> wordsList : wordsMatrix) {
            if (wordsList.isEmpty()) return;
            references.add(wordsList.get(0));
            iterators.add(wordsList.listIterator());
        }

    }

}
