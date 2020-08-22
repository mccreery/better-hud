package jobicade.betterhud.util.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import jobicade.betterhud.geom.Point;

public class PointTypeAdapter implements JsonSerializer<Point>, JsonDeserializer<Point> {
    @Override
    public JsonElement serialize(Point src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(new int[] {src.getX(), src.getY()});
    }

    @Override
    public Point deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int[] coordinates = context.deserialize(json, int[].class);
        return new Point(coordinates[0], coordinates[1]);
    }
}
