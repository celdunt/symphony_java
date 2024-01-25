package loc.ex.symphony.indexdata;

import loc.ex.symphony.listview.PathsEnum;

import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class IndexSaver {

    public static void save(ConcurrentHashMap<String, List<IndexStruct>> index, PathsEnum mode) {
        String name = mode == PathsEnum.Bible? "bible.id" : "ellen.id";
        synchronized (index) {
            try (FileOutputStream fileOut = new FileOutputStream(name)) {
                ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
                objOut.writeObject(index);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

    }

    public static ConcurrentHashMap<String, List<IndexStruct>> load(PathsEnum mode) {
        String name = mode == PathsEnum.Bible? "bible.id" : "ellen.id";
        ConcurrentHashMap<String, List<IndexStruct>> index = new ConcurrentHashMap<>();
        try (FileInputStream fileIn = new FileInputStream(name)) {
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            index = (ConcurrentHashMap<String, List<IndexStruct>>) objIn.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return index;
    }
}
