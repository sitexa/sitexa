package com.sitexa.ktor

import com.google.gson.*
import org.joda.time.DateTime
import java.lang.reflect.Type

/**
 * Created by open on 21/04/2017.
 *
 */
class JodaDateAdapter : JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
    override fun serialize(src: DateTime, srcType: Type, context: JsonSerializationContext): JsonElement {
        //return JsonPrimitive(src.toString())
        return JsonPrimitive(src.millis)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): DateTime {
        try {
            return DateTime(json.asLong)
        } catch (e: IllegalArgumentException) {
            val date = context.deserialize<DateTime>(json, DateTime::class.java)
            return date
        }
    }
}
