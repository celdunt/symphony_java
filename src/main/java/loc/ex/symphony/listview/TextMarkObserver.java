package loc.ex.symphony.listview;

import java.util.ArrayList;
import java.util.List;

public class TextMarkObserver {

    private static List<TextMarkObservable> observableList = new ArrayList<TextMarkObservable>();

    public static void subscribe(TextMarkObservable observable, boolean toNotify) {
        if (toNotify)
            for (TextMarkObservable o : observableList) {
                o.notifySub(observable);
            }
        observableList.add(observable);
    }

    public static void unsubscribe(TextMarkObservable observable, boolean toNotify) {
        observableList.remove(observable);
        if (toNotify)
            for (TextMarkObservable o : observableList) {
                o.notifyUnsub(observable);
            }
    }

    public static void clearAll() {
        observableList.clear();
    }

    public static void subscribeAll(List<? extends TextMarkObservable> list) {
        observableList.addAll(list);
    }

}
