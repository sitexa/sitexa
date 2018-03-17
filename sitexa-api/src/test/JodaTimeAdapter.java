import com.google.gson.*;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

/**
 * Created by open on 06/05/2017.
 */
public class JodaTimeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
    @Override
    public DateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            return new DateTime(jsonElement.getAsLong());
        } catch (Exception e) {
            return jsonDeserializationContext.deserialize(jsonElement, DateTime.class);
        }
    }

    @Override
    public JsonElement serialize(DateTime dateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateTime.getMillis());
    }
}
