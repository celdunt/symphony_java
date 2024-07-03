package loc.ex.symphony.file;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import javafx.beans.value.ObservableValue;

import java.io.IOException;

public class ObservableValueSerializer extends StdSerializer<ObservableValue> {

    public ObservableValueSerializer() {
        super(ObservableValue.class);
    }


    @Override
    public void serialize(ObservableValue stringObservableValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (stringObservableValue.getValue() instanceof String) {
            jsonGenerator.writeString((String)stringObservableValue.getValue());
        } else {
            throw new IOException("Unsupported type");
        }
    }
}
