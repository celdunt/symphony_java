package loc.ex.symphony.file;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;

import java.io.IOException;

public class ObservableValueDeserializer extends JsonDeserializer<ObservableValue> {
    @Override
    public ObservableValue deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.getValueAsString();
        return new ObservableValueBase<>() {
            @Override
            public String getValue() {
                return value;
            }
        };
    }
}
