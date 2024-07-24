package loc.ex.symphony.file;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import loc.ex.symphony.listview.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileAdapter {

    public ObservableList<Book> getBible() throws IOException {
        return doHandleBibleList();
    }

    public ObservableList<Book> getEllen() throws IOException {
        return doHandleEllenList();
    }

    public ObservableList<Book> getOther() throws IOException {
        return doHandleOtherList();
    }

    private ObservableList<Book> doHandleBibleList() throws IOException {
        List<RawBook> rawBible = new FileLoader().getBible();

        Pattern splitByChapterPattern = Pattern.compile("(?i)ГЛАВА\\s*\\d+");
        ObservableList<Book> bookObservableList = FXCollections.observableArrayList();

        for (RawBook book : rawBible) {
            String[] rawChapters = splitByChapterPattern.split(book.text.get());
            Book handledBook = new Book(book.name.get(), PathsEnum.Bible);

            for (int numOfChapter = 1; numOfChapter <= rawChapters.length; numOfChapter++) {
                Pattern splitByFragmentPattern = Pattern.compile("(\\d+)\\s+(.*?)\\n");
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

    private ObservableList<Book> doHandleEllenList() throws IOException {
        ObservableList<Book> observableBooks = FXCollections.observableArrayList();
        List<RawBook> rawEllen = new FileLoader().getEllen();

        for (RawBook rawBook : rawEllen) {
            String[] rawChapters = rawBook.text.get().split("Г1лава\\s*\\d+\\.\\n*|Г1лава\\s*\\d+\\n*|Г1лава\\s*\\n*|г1лава\\s*\\n*");
            Book handledBook = new Book(rawBook.name.get(), PathsEnum.EllenWhite);

            for(int ichapter = 0; ichapter < rawChapters.length; ichapter++) {
                String[] rawFragments = rawChapters[ichapter].split("\\."); //\[\d+]\s+
                List<String> splitedFragmets = new ArrayList<>();
                int minLength = 25;
                StringBuilder buffer = new StringBuilder();

                for (String part : rawFragments) {
                    part = part.trim(); // Убираем пробелы по краям

                    if (buffer.isEmpty()) {
                        buffer.append(part);
                    } else {
                        if (buffer.length() + part.length() + 1 < minLength) {
                            buffer.append(".").append(part);
                        } else {
                            splitedFragmets.add(buffer.append(".").toString());
                            buffer.setLength(0);
                            buffer.append(part);
                        }
                    }
                }
                if (!buffer.isEmpty())
                    splitedFragmets.add(buffer.append(".").toString());

                List<String> fragments = new ArrayList<>(splitedFragmets)
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

    private ObservableList<Book> doHandleOtherList() throws IOException {
        ObservableList<Book> observableBooks = FXCollections.observableArrayList();
        List<RawBook> rawOther = new FileLoader().getOther();

        for (RawBook rawBook : rawOther) {
            String[] rawChapters = rawBook.text.get().split("Г1лава\\s*\\d+\\.\\n*|Г1лава\\s*\\d+\\n*|Г1лава\\s*\\n*|г1лава\\s*\\n*");
            Book handledBook = new Book(rawBook.name.get(), PathsEnum.Other);

            for(int ichapter = 0; ichapter < rawChapters.length; ichapter++) {
                String[] rawFragments = rawChapters[ichapter].split("\\."); //\[\d+]\s+
                List<String> splitedFragmets = new ArrayList<>();
                int minLength = 25;
                StringBuilder buffer = new StringBuilder();

                for (String part : rawFragments) {
                    part = part.trim(); // Убираем пробелы по краям

                    if (buffer.isEmpty()) {
                        buffer.append(part);
                    } else {
                        if (buffer.length() + part.length() + 1 < minLength) {
                            buffer.append(".").append(part);
                        } else {
                            splitedFragmets.add(buffer.append(".").toString());
                            buffer.setLength(0);
                            buffer.append(part);
                        }
                    }
                }
                if (!buffer.isEmpty())
                    splitedFragmets.add(buffer.append(".").toString());

                List<String> fragments = new ArrayList<>(splitedFragmets)
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
