package loc.ex.symphony.search;

import loc.ex.symphony.listview.PathsEnum;

public abstract class Cutprompt {

    protected int bookId;
    protected PathsEnum mode;

    public static Cutprompt validationBuild(String prompt) {

        String[] splitPrompt = prompt.split("[ :]");
        if (splitPrompt.length == 2) {
            String cut = splitPrompt[0];
            if (!cut.endsWith(".")) cut += ".";
            int bookId = new Cutser().getEllenIndex(cut);
            if (bookId > -1) {
                int page;
                try {
                    page = Integer.parseInt(splitPrompt[1]);
                    return new EllenCutprompt(bookId, PathsEnum.EllenWhite, page);
                } catch (Exception exc) {
                    return new ErrorCutprompt();
                }
            } else return new ErrorCutprompt();
        } else if (splitPrompt.length == 3 || splitPrompt.length == 4) {
            String cut = splitPrompt[0];
            int i = 1;
            if (splitPrompt.length == 4) {
                cut = String.format("%s %s", splitPrompt[0], splitPrompt[1]);
                i++;
            }
            if (!cut.endsWith(".")) cut += ".";
            int bookId = new Cutser().getBibleIndex(cut);
            if (bookId > -1) {
                int chapter;
                int fragment;
                try {
                    chapter = Integer.parseInt(splitPrompt[i]);
                    fragment = Integer.parseInt(splitPrompt[i+1]);
                    return new BibleCutprompt(bookId, PathsEnum.Bible, chapter, fragment-1);
                } catch (Exception exc) {
                    return new ErrorCutprompt();
                }
            } else return new ErrorCutprompt();
        }

        return new ErrorCutprompt();

    }

    public int getBookId() {
        return bookId;
    }

    public PathsEnum getMode() {
        return mode;
    }


}
