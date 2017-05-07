package com.sitexa.ktor.common

import com.google.gson.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.joda.time.DateTime
import java.lang.reflect.Type

/**
 * Created by open on 21/04/2017.
 *
 */
class JodaMoshiAdapter {
    @ToJson
    fun toJson(date: DateTime) = date.millis

    @FromJson
    fun fromJson(json: Long) = DateTime(json)
}

class JodaGsonAdapter : JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
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
