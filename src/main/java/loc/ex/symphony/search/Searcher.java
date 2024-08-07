package loc.ex.symphony.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import loc.ex.symphony.indexdata.IndexSaver;
import loc.ex.symphony.indexdata.IndexSaverSingleThreaded;
import loc.ex.symphony.indexdata.IndexStruct;
import loc.ex.symphony.listview.Book;
import loc.ex.symphony.listview.Chapter;
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
            if (COUNT_OF_LOADED_BOOK_INDICES == 3)
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
            if (indexData == null) continue;

            List<IndexStruct> primaryWords = indexData.get(word.toLowerCase());
            List<IndexStruct> fullWords = new ArrayList<>(primaryWords);

            for (int synonymKey : primaryWords.get(0).getSynonymsKeys()) {
                fullWords.addAll(indexData.get(uniqueWords.get(synonymKey)));
            }

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
            references.add(wordsList.get(0));
            iterators.add(wordsList.listIterator());
        }

    }

    public ObservableList<Link> search(String prompt, PathsEnum pathsEnum, HashMap<String, Integer> uniqueWordH) {
        List<Link> foundOccurrences = new ArrayList<>();

        String[] words = prompt.split("[\\s\\p{Punct}]+");

        int bookID = 0;
        int chapterID;
        int fragmentID;
        boolean isFound;

        for (Book book : resource) {
            chapterID = 0;
            for (Chapter chapter : book.getChapters()) {
                fragmentID = 0;
                for (String fragment : chapter.getFragments()) {
                    isFound = true;
                    List<IndexStruct> indexStructs = new ArrayList<>();
                    for (String word : words) {
                        if (!fragment.toLowerCase().contains(word.toLowerCase())) {
                            isFound = false;
                            break;
                        } else {
                            indexStructs.add(getIndexStruct(
                                    bookID, chapterID, fragmentID,
                                    fragment, word, uniqueWordH
                            ));
                        }
                    }

                    if (isFound) {
                        foundOccurrences.add(new Link(indexStructs, resource, pathsEnum,
                                Arrays.toString(indexStructs.stream().map(f -> uniqueWords.get(f.getWordKey())).toArray())));
                    }

                    fragmentID++;
                }
                chapterID++;
            }
            bookID++;
        }

        return FXCollections.observableArrayList(foundOccurrences);

    }

    private IndexStruct getIndexStruct(int b, int c, int f, String fr, String w, HashMap<String, Integer> u) {
        int p = fr.toLowerCase().indexOf(w.toLowerCase());
        int e = p;
        while (p-1 > 0 && Character.isLetterOrDigit(fr.charAt(p-1))) {
            p--;
        }
        while (e < fr.length() && Character.isLetterOrDigit(fr.charAt(e))) {
            e++;
        }
        w = fr.substring(p, e).toLowerCase();
        return new IndexStruct(b, c, f, p, u.getOrDefault(w, 0), null);
    }

}
