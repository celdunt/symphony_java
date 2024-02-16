package loc.ex.symphony.indexdata;

import loc.ex.symphony.listview.PathsEnum;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class IndexSaverSingleThreaded {

    public static void save(HashMap<String, List<IndexStruct>> index, PathsEnum mode) {
        String name = mode == PathsEnum.Bible? "bible.id" : "ellen.id";
        try (FileOutputStream fileOut = new FileOutputStream(name)) {
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(index);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static HashMap<String, List<IndexStruct>> load(PathsEnum mode) {
        String name = mode == PathsEnum.Bible? "bible.id" : "ellen.id";
        HashMap<String, List<IndexStruct>> index = new HashMap<>();

        try (FileInputStream fileIn = new FileInputStream(name)) {
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            index = (HashMap<String, List<IndexStruct>>) objIn.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return index;
    }
}
