package loc.ex.symphony.search;

import loc.ex.symphony.listview.PathsEnum;

public class EllenCutprompt extends Cutprompt {

    int page;

    EllenCutprompt(int bookId, PathsEnum mode, int page) {

        this.bookId = bookId;
        this.mode = mode;
        this.page = page;

    }

    public int getPage() {
        return page;
    }

}
