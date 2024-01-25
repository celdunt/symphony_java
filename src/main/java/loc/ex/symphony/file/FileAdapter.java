package loc.ex.symphony.file;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import loc.ex.symphony.listview.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileAdapter {

    private final List<RawBook> bible;
    private final List<RawBook> ellen;


    public FileAdapter() throws IOException {
        FileLoader fileLoader = new FileLoader();
        bible = fileLoader.getBible();
        ellen = fileLoader.getEllen();
    }

    public ObservableList<Book> getBible() {
        return getHandledObservableList("(?i)ГЛАВА\\s*\\d+", "(\\d+)\\s+(.*?)\\n", PathsEnum.Bible);
        //return getObservableList(PathsEnum.Bible);
    }

    public ObservableList<Book> getEllen() {
        //return getHandledObservableList("(?i)Г1лава\\s*\\d+", "\\[(\\d+)\\](.*?)\\n", PathsEnum.EllenWhite);
        return getObservableList(PathsEnum.EllenWhite);
    }

    private ObservableList<Book> getHandledObservableList(String splitByChapterRegex, String splitByFragmentsRegex, PathsEnum root) {
        List<RawBook> rawBooks = new ArrayList<>();

        if (root == PathsEnum.Bible) rawBooks = bible;
        else if (root == PathsEnum.EllenWhite) rawBooks = ellen;

        Pattern splitByChapterPattern = Pattern.compile(splitByChapterRegex);
        ObservableList<Book> bookObservableList = FXCollections.observableArrayList();

        for (RawBook book : rawBooks) {
            String[] rawChapters = splitByChapterPattern.split(book.text.get());
            Book handledBook = new Book(book.name.get(), root);

            for (int numOfChapter = 1; numOfChapter <= rawChapters.length; numOfChapter++) {
                Pattern splitByFragmentPattern = Pattern.compile(splitByFragmentsRegex);
                Matcher matcher = splitByFragmentPattern.matcher(rawChapters[numOfChapter-1]);

                List<String> fragments = new ArrayList<>();

                while (matcher.find()) {
                    String part1 = matcher.group(1);
                    String part2 = "";

                    if (matcher.groupCount() > 1)
                        part2 = matcher.group(2);

                    fragments.add(part1 + " " + part2 + "\n");
                }
                Chapter chapter = new Chapter(numOfChapter);

                chapter.getFragments().addAll(fragments);
                handledBook.getChapters().add(chapter);
            }

            bookObservableList.add(handledBook);
        }


        return bookObservableList;
    }

    private ObservableList<Book> getObservableList(PathsEnum root) {
        ObservableList<Book> observableBooks = FXCollections.observableArrayList();
        List<RawBook> rawBooks;
        String splitByChapterPattern;
        String splitByFragmentPattern;

        if (root == PathsEnum.Bible) {
            rawBooks = bible;
            splitByChapterPattern = "(?i)ГЛАВА\\s*\\d+";
            splitByFragmentPattern = "\\d+\\.\\s+";
        } else {
            rawBooks = ellen;
            splitByChapterPattern = "Г1лава\\s*\\d+\\.\\n*|Г1лава\\s*\\d+\\n*|Г1лава\\s*\\n*|г1лава\\s*\\n*";
            splitByFragmentPattern = "\\[\\d+]\\s+";
        }

        for (RawBook rawBook : rawBooks) {
            String[] rawChapters = rawBook.text.get().split(splitByChapterPattern);
            Book handledBook = new Book(rawBook.name.get(), root);

            for(int ichapter = 0; ichapter < rawChapters.length; ichapter++) {
                String[] rawFragments = rawChapters[ichapter].split(splitByFragmentPattern);
                List<String> fragments = new ArrayList<>(List.of(rawFragments))
                        .stream().map(fr -> fr.replaceAll("^\\s*\\n+", ""))
                        .toList();
                Chapter chapter = new Chapter(ichapter+1);
                chapter.getFragments().addAll(fragments);
                handledBook.getChapters().add(chapter);
            }

            observableBooks.add(handledBook);
        }

        return  observableBooks;
    }
}
