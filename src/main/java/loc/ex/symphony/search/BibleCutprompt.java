package loc.ex.symphony.search;

import loc.ex.symphony.listview.PathsEnum;

public class BibleCutprompt extends Cutprompt {

    private final int chapter;
    private final int fragment;


    BibleCutprompt(int bookId, PathsEnum mode, int chapter, int fragment) {

        this.bookId = bookId;
        this.mode = mode;
        this.chapter = chapter;
        this.fragment = fragment;

    }

    public int getChapter() {
        return chapter;
    }

    public int getFragment() {
        return fragment;
    }

}
