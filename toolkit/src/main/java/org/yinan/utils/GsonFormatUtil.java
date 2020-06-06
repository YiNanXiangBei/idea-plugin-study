package org.yinan.utils;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author yinan
 * @date 2020/5/31
 */
public class GsonFormatUtil {

    @NotNull
    public static String gsonFormat(Gson gson, JsonElement jsonElement) throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(Streams
                .writerForAppendable(Streams.writerForAppendable(writer)));
        gson.toJson(jsonElement, jsonWriter);
        return writer.toString();

    }

    @NotNull
    public static String gsonFormat(Gson gson, Object src) throws IOException {
        if (src == null) {
            return gson.toJson(JsonNull.INSTANCE);
        }

        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(Streams
                .writerForAppendable(Streams.writerForAppendable(writer)));
        gson.toJson(src, src.getClass(), jsonWriter);
        return writer.toString();
    }

    private static JsonWriter newJsonWriter(Writer writer) throws IOException {
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.setIndent("    ");
        jsonWriter.setSerializeNulls(false);
        return jsonWriter;
    }

}
