package com.sitexa.ktor

import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

/**
 * Created by open on 21/04/2017.
 *
 */
class DateTypeConverter : JsonSerializer<Date>, JsonDeserializer<Date> {
    override fun serialize(src: Date, srcType: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, type: Type, context: JsonDeserializationContext): Date {
        try {
            return Date(json.asLong)
        } catch (e: IllegalArgumentException) {
            val date = context.deserialize<Date>(json, Date::class.java)
            return date
        }
    }
}