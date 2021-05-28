package jobicade.betterhud.element.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.util.IGetSet.IBoolean;

import java.util.List;

public class RootSetting extends SettingStub<Boolean> implements IBoolean {
    private final HudElement element;
    private int priorityRank = 0;

    public final SettingBoolean enabled = (SettingBoolean)new SettingBoolean("enabled").setHidden();

    public final Setting<Integer> priority = new SettingStub<Integer>("priority") {
        @Override public void set(Integer value) {priorityRank = value;}
        @Override public Integer get() {return priorityRank;}

        @Override public JsonElement save() {return new JsonPrimitive(priorityRank);}
        @Override public void load(JsonElement save) {priorityRank = save.getAsInt();}

        @Override protected boolean hasValue() {return true;}
    }.setHidden();

    public RootSetting(HudElement element, List<Setting<?>> settings) {
        super();
        this.element = element;

        add(enabled);
        add(priority);
        addAll(settings);
    }

    @Override
    public Boolean get() {
        return enabled.get();
    }

    @Override
    public void set(Boolean value) {
        enabled.set(value);
    }

    @Override
    public boolean isEmpty() {
        return children.size() <= 2;
    }
}
