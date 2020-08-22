package jobicade.betterhud.util.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.registry.HudRegistry;

public class ElementTypeAdapter implements JsonSerializer<HudElement<?>>, JsonDeserializer<HudElement<?>> {
    private final HudRegistry<?> registry;

    public ElementTypeAdapter(HudRegistry<?> registry) {
        this.registry = registry;
    }

    @Override
    public JsonElement serialize(HudElement<?> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src != null) {
            return context.serialize(src.getName());
        } else {
            return JsonNull.INSTANCE;
        }
    }

    @Override
    public HudElement<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) {
            return null;
        } else {
            String name = context.deserialize(json, String.class);
            HudElement<?> element = registry.getRegistered(name);

            if (element != null) {
                return element;
            } else {
                throw new JsonParseException("unknown element");
            }
        }
    }
}
