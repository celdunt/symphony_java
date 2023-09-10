package loc.ex.symphony.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import loc.ex.symphony.indexdata.IndexSaver;
import loc.ex.symphony.indexdata.IndexStruct;
import loc.ex.symphony.listview.Book;
import loc.ex.symphony.listview.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;

public class Searcher {

    private ObservableList<Book> resource;
    private final ConcurrentHashMap<String, List<IndexStruct>> indexData;

    public Searcher() {
        indexData = IndexSaver.load();
    }

    public void setResource(ObservableList<Book> resource) {
        this.resource = resource;
    }

    public ObservableList<Link> search(String prompt) {
        if (indexData != null) {
            List<IndexStruct> linkData = indexData.get(prompt.toLowerCase());
            if (linkData != null)
                return FXCollections.observableArrayList(linkData.stream().map(x -> new Link(x, resource, prompt)).toList());
        }
        return FXCollections.observableArrayList();
    }

    public ObservableList<Link> search_1(String prompt) {
        if (indexData != null) {
            String[] words = prompt.split("[\\s\\p{Punct}]+");
            List<List<IndexStruct>> wordsData = new ArrayList<>();
            //ищем слова из запроса и их синонимы и кидаем в один лист
            for (String word : words) {
                List<IndexStruct> another = indexData.get(word.toLowerCase());
                for (String synonym : another.get(0).getSynonyms()) another.addAll(indexData.get(synonym));
                wordsData.add(another);
            }


            List<IndexStruct> references = new ArrayList<>();
            List<ListIterator<IndexStruct>> iterators = new ArrayList<>();

            for (List<IndexStruct> wordData : wordsData) {
                iterators.add(wordData.listIterator());
                references.add(wordData.get(0));
            }

            for (int iref = 0; iref < references.size(); iref++) {
                int countEquals = 0;

                for (int jref = 0; jref < references.size(); jref++) {
                    if (iref != jref) {

                        if (references.get(iref).getBookId() < references.get(jref).getBookId()) {
                            while(references.get(iref).getBookId() < references.get(jref).getBookId()) {
                                if (iterators.get(iref).hasNext()) references.set(iref, iterators.get(iref).next());
                                else {
                                    // exitKey
                                    break;
                                }
                            }
                            break;
                        }
                        while (references.get(jref).getBookId() < references.get(iref).getBookId()) {
                            if (iterators.get(jref).hasNext()) references.set(jref, iterators.get(jref).next());
                            else {
                                // exitKey
                                break;
                            }
                        }
                        if (references.get(iref).getBookId() == references.get(jref).getBookId()) countEquals++;

                        /*

                        Необходимо прописать всё то же самое(что написано для bookId) для chapterId и fragmentId.
                        Запихнуть ПОДГОНКУ ПОД ГЛАВЫ в условие countEquals == references.size()-1
                        Запихнуть ПОДГОНКУ ПОД ФРАГМЕНТЫ в условие, находящееся в ПОДГОНКЕ ПОД ГЛАВЫ

                         */
                    }
                }

                if (countEquals == references.size()-1) {
                    // Запускаем ПОДГОНКУ ПОД ГЛАВЫ, а там запустим ПОДГОНКУ ПОД ФРАГМЕНТЫ, а там слздадим нужный линк
                }

            }
        }


        return FXCollections.observableArrayList();
    }
}
