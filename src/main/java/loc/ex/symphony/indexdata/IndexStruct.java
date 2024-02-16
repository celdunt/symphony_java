package loc.ex.symphony.indexdata;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IndexStruct implements Serializable, Comparable<IndexStruct> {
    private int bookId;
    private int chapterId;
    private int fragmentId;
    private int position;
    private int wordLength;
    private String word;

    private final List<String> synonyms = new ArrayList<>();


    public List<String> getSynonyms() {
        return synonyms;
    }

    public int getBookID() {
        return bookId;
    }

    public void setBookID(int bookId) {
        this.bookId = bookId;
    }

    public int getChapterID() {
        return chapterId;
    }

    public void setChapterID(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getFragmentID() {
        return fragmentId;
    }

    public void setFragmentID(int fragmentId) {
        this.fragmentId = fragmentId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getWordLength() {
        return wordLength;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    @Override
    public int compareTo(@NotNull IndexStruct other) {
        int firstStage = Integer.compare(this.getBookID(), other.getBookID());
        int secondStage = Integer.compare(this.getChapterID(), other.getChapterID());
        int thirdStage = Integer.compare(this.getFragmentID(), other.getFragmentID());

        if (firstStage == 0)
            if (secondStage == 0)
                return thirdStage;
            else return secondStage;
        else return firstStage;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public static class PositionComparator implements Comparator<loc.ex.symphony.indexdata.IndexStruct> {
        @Override
        public int compare(loc.ex.symphony.indexdata.IndexStruct o1, loc.ex.symphony.indexdata.IndexStruct o2) {
            return Integer.compare(o1.getPosition(), o2.getPosition());
        }
    }
}
