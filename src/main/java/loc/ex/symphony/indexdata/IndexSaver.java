package loc.ex.symphony.indexdata;

import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class IndexSaver {

    public void save(ConcurrentHashMap<String, List<IndexStruct>> index) {
        try (FileOutputStream fileOut = new FileOutputStream("index.id")) {
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(index);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public ConcurrentHashMap<String, List<IndexStruct>> load() {
        ConcurrentHashMap<String, List<IndexStruct>> index = new ConcurrentHashMap<>();
        try (FileInputStream fileIn = new FileInputStream("index.id")) {
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            index = (ConcurrentHashMap<String, List<IndexStruct>>) objIn.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return index;
    }
}
