package loc.ex.symphony.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import loc.ex.symphony.indexdata.IndexSaver;
import loc.ex.symphony.indexdata.IndexSaverSingleThreaded;
import loc.ex.symphony.indexdata.IndexStruct;
import loc.ex.symphony.listview.Book;
import loc.ex.symphony.listview.Link;
import loc.ex.symphony.listview.PathsEnum;
import loc.ex.symphony.ui.MainController;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

public class Searcher {

    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private ObservableList<Book> resource;
    private HashMap<String, List<IndexStruct>> indexData;
    private final HashMap<Integer, String> uniqueWords;

    private static byte COUNT_OF_LOADED_BOOK_INDICES = 0;

    public Searcher(PathsEnum mode, HashMap<Integer, String> uniqueWords) throws IOException {
        Task<HashMap<String, List<IndexStruct>>> task = IndexSaverSingleThreaded.loadTask(mode);
        task.setOnSucceeded(a -> {
            indexData = task.getValue();
            COUNT_OF_LOADED_BOOK_INDICES++;
            if (COUNT_OF_LOADED_BOOK_INDICES == 2)
                MainController.usabilityButtonListener.set("enable_button");
        });

        task.setOnFailed(a -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Данные индексации не были загружены");
            alert.show();
        });

        new Thread(task).start();

        this.uniqueWords = uniqueWords;
    }

    public void setResource(ObservableList<Book> resource) {
        this.resource = resource;
    }

    /**
     * @param prompt This user search request parameter
     * @return List of Link
     */
    public ObservableList<Link> search(String prompt, PathsEnum pathsEnum) {
        List<Link> foundOccurrences = new ArrayList<>();


        String[] words = prompt.split("[\\s\\p{Punct}]+");
        List<List<IndexStruct>> onlyPrimaryWordsList = new ArrayList<>();
        List<List<IndexStruct>> fullWordsList = new ArrayList<>();

        for (String word : words) {
            //indexData = IndexSaverSingleThreaded.load(word.toLowerCase().substring(0, 1), pathsEnum);

            if (indexData == null) continue;

            List<IndexStruct> primaryWords = indexData.get(word.toLowerCase());
            List<IndexStruct> fullWords = new ArrayList<>(primaryWords);

            for (int synonymKey : primaryWords.getFirst().getSynonymsKeys()) {
                fullWords.addAll(indexData.get(uniqueWords.get(synonymKey)));
            }

            /*for (String synonym : primaryWords.get(0).getSynonyms()) {
                if (indexData.get(synonym) == null || indexData.get(synonym).isEmpty()) {
                    *//*indexData.putAll(
                            IndexSaverSingleThreaded.load(synonym.toLowerCase().substring(0, 1), pathsEnum)
                    );*//*
                }
                fullWords.addAll(indexData.get(synonym));
            }*/

            Collections.sort(primaryWords);
            Collections.sort(fullWords);

            onlyPrimaryWordsList.add(primaryWords);
            fullWordsList.add(fullWords);
        }

        List<IndexStruct> references = new ArrayList<>();
        List<ListIterator<IndexStruct>> iterators = new ArrayList<>();

        fillReferencesAndIterators(references, iterators, onlyPrimaryWordsList);
        selectionOfValidEntries(references, iterators, foundOccurrences, words, pathsEnum);

        fillReferencesAndIterators(references, iterators, fullWordsList);
        selectionOfValidEntries(references, iterators, foundOccurrences, words, pathsEnum);


        return FXCollections.observableArrayList(foundOccurrences);
    }

    private void selectionOfValidEntries(List<IndexStruct> references, List<ListIterator<IndexStruct>> iterators,
                                         List<Link> foundOccurrences, String[] words, PathsEnum pathsEnum) {

        int fixedID = 0;
        boolean isContinue = !references.isEmpty();

        while (isContinue) {
            int countOfValid = 1;
            for (int i = fixedID + 1; i < references.size(); i++) {

                if (references.get(fixedID).getBookID() == references.get(i).getBookID()) {

                    if (references.get(fixedID).getChapterID() == references.get(i).getChapterID()) {

                        if (references.get(fixedID).getFragmentID() == references.get(i).getFragmentID())
                            countOfValid++;
                        else {
                            while (references.get(fixedID).getFragmentID() > references.get(i).getFragmentID() &&
                                    references.get(fixedID).getBookID() == references.get(i).getBookID() &&
                                    references.get(fixedID).getChapterID() == references.get(i).getChapterID()) {
                                if (iterators.get(i).hasNext()) references.set(i, iterators.get(i).next());
                                else {
                                    isContinue = false;
                                    break;
                                }
                            }
                            while (references.get(fixedID).getFragmentID() < references.get(i).getFragmentID() &&
                                    references.get(fixedID).getBookID() == references.get(i).getBookID() &&
                                    references.get(fixedID).getChapterID() == references.get(i).getChapterID()) {
                                if (iterators.get(fixedID).hasNext())
                                    references.set(fixedID, iterators.get(fixedID).next());
                                else {
                                    isContinue = false;
                                    break;
                                }
                            }
                        }

                    } else {
                        while (references.get(fixedID).getChapterID() > references.get(i).getChapterID() &&
                                references.get(fixedID).getBookID() == references.get(i).getBookID()) {
                            if (iterators.get(i).hasNext()) references.set(i, iterators.get(i).next());
                            else {
                                isContinue = false;
                                break;
                            }
                        }
                        while (references.get(fixedID).getChapterID() < references.get(i).getChapterID() &&
                                references.get(fixedID).getBookID() == references.get(i).getBookID()) {
                            if (iterators.get(fixedID).hasNext())
                                references.set(fixedID, iterators.get(fixedID).next());
                            else {
                                isContinue = false;
                                break;
                            }
                        }
                    }

                } else {
                    while (references.get(fixedID).getBookID() > references.get(i).getBookID()) {
                        if (iterators.get(i).hasNext()) references.set(i, iterators.get(i).next());
                        else {
                            isContinue = false;
                            break;
                        }
                    }
                    while (references.get(fixedID).getBookID() < references.get(i).getBookID()) {
                        if (iterators.get(fixedID).hasNext()) references.set(fixedID, iterators.get(fixedID).next());
                        else {
                            isContinue = false;
                            break;
                        }
                    }
                }


            }

            if (countOfValid == references.size()) {
                Link toAdd = new Link(references, resource, pathsEnum, words);
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
            references.add(wordsList.getFirst());
            iterators.add(wordsList.listIterator());
        }

    }

}
