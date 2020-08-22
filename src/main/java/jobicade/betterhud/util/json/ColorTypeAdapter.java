package jobicade.betterhud.util.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import jobicade.betterhud.render.Color;

public class ColorTypeAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
    @Override
    public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.getAlpha() == 255) {
            return new JsonPrimitive(getHexString(src));
        } else {
            ColorWithAlpha color = new ColorWithAlpha();
            color.rgb = getHexString(src);
            color.alpha = src.getAlpha();

            return context.serialize(color);
        }
    }

    private String getHexString(Color color) {
        return String.format("#%06x", color.getPacked() & 0xffffff);
    }

    @Override
    public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            ColorWithAlpha color = context.deserialize(json, ColorWithAlpha.class);
            return parseHexString(color.rgb).withAlpha(color.alpha);
        } else {
            String rgb = context.deserialize(json, String.class);
            return parseHexString(rgb);
        }
    }

    private Color parseHexString(String hex) {
        if (hex.charAt(0) != '#') {
            throw new JsonParseException("color must begin with #");
        }
        return new Color(Integer.parseInt(hex.substring(1), 16)).withAlpha(255);
    }

    private static class ColorWithAlpha {
        String rgb;
        int alpha;
    }
}
