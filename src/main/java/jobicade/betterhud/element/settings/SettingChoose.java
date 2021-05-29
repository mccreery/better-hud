package jobicade.betterhud.element.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SettingChoose extends SettingAlignable<String> {
    protected Button last, next, backing;
    protected final String[] modes;

    private int index = 0;
    private int length;

    public SettingChoose(int length) {
        this("mode", length);
    }

    public SettingChoose(String name, int length) {
        this(name);
        this.length = length;
    }

    public SettingChoose(String name, String... modes) {
        this(name, Direction.CENTER, modes);
    }

    public SettingChoose(String name, Direction alignment, String... modes) {
        super(name, alignment);

        this.modes = modes;
        this.length = modes.length;
    }

    public void setIndex(int index) {
        if(index >= 0 && index < length) {
            this.index = index;
        }
    }

    public int getIndex() {
        return index;
    }

    public void last() {
        int index = getIndex();

        if(index == 0) {
            index = length;
        }
        setIndex(--index);
    }

    public void next() {
        int index = getIndex() + 1;

        if(index == length) {
            index = 0;
        }
        setIndex(index);
    }

    @Override
    public void set(String value) {
        int index = ArrayUtils.indexOf(modes, value);
        if(index == -1) index = Integer.parseUnsignedInt(value);

        setIndex(index);
    }

    @Override
    public String get() {
        return index < modes.length ? modes[index] : String.valueOf(index);
    }

    @Override public JsonElement save() {return new JsonPrimitive(get());}
    @Override public void load(JsonElement save) {set(save.getAsString());}

    @Override
    public void getGuiParts(List<AbstractGui> parts, Map<AbstractGui, Setting<?>> callbacks, Rect bounds) {
        parts.add(backing = new Button(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), StringTextComponent.EMPTY, null));
        parts.add(last = new Button(bounds.getLeft(), bounds.getY(), 20, bounds.getHeight(), new StringTextComponent("<"), b -> last()));
        parts.add(next = new Button( bounds.getRight() - 20, bounds.getY(), 20, bounds.getHeight(), new StringTextComponent(">"), b -> next()));
        backing.active = false;

        callbacks.put(last, this);
        callbacks.put(next, this);
    }

    protected String getUnlocalizedValue() {
        return "betterHud.value." + modes[getIndex()];
    }

    protected String getLocalizedValue() {
        int index = getIndex();

        if(index >= 0 && index < modes.length) {
            return I18n.get(getUnlocalizedValue());
        } else {
            return I18n.get("betterHud.value.mode", index);
        }
    }

    @Override
    public void actionPerformed(GuiElementSettings gui, Button button) {
    }

    @Override
    public void draw() {
        Point center = new Point(backing.x + backing.getWidth() / 2, backing.y + backing.getHeight() / 2);
        GlUtil.drawString(new MatrixStack(), getLocalizedValue(), center, Direction.CENTER, Color.WHITE);
    }

    @Override
    public void updateGuiParts(Collection<Setting<?>> settings) {
        last.active = next.active = enabled();
    }
}
