package jobicade.betterhud.element.settings;

import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.lang3.ArrayUtils;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

public class SettingChoose extends SettingAlignable {
    protected Button last, next, backing;
    protected final String[] modes;

    private int index = 0;
    private int length;

    public SettingChoose(String name, int length) {
        this(name);
        this.length = length;
    }

    public SettingChoose(String name, String... modes) {
        super(name);

        this.modes = modes;
        this.length = modes.length;
    }

    public String get() {
        if (index < modes.length) {
            return modes[index];
        } else {
            return String.valueOf(index);
        }
    }

    public void set(String mode) {
        try {
            int index = ArrayUtils.indexOf(modes, mode);
            if (index == -1) {
                index = Integer.parseInt(mode);
            }
            setIndex(index);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            String[] allModes = Arrays.copyOf(modes, length);
            for (int i = modes.length; i < length; i++) {
                allModes[i] = String.valueOf(i);
            }
            throw new IllegalArgumentException("Invalid mode " + mode + ". Valid modes are " + Arrays.toString(allModes));
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        if(index >= 0 && index < length) {
            this.index = index;
        } else {
            throw new IndexOutOfBoundsException("mode: " + index + ", max: " + (length - 1));
        }
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
    public JsonElement saveJson(Gson gson) {
        return gson.toJsonTree(get());
    }

    @Override
    public void loadJson(Gson gson, JsonElement element) throws JsonSyntaxException {
        set(gson.fromJson(element, String.class));
    }

    @Override
    public void getGuiParts(GuiElementSettings.Populator populator, Rect bounds) {
        backing = populator.add(new Button(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), "", b -> {}));
        backing.active = false;

        last = populator.add(new Button(bounds.getLeft(), bounds.getY(), 20, bounds.getHeight(), "<", b -> last()));
        next = populator.add(new Button(bounds.getRight() - 20, bounds.getY(), 20, bounds.getHeight(), ">", b -> next()));
    }

    protected String getUnlocalizedValue() {
        return "betterHud.value." + modes[getIndex()];
    }

    protected String getLocalizedValue() {
        int index = getIndex();

        if(index >= 0 && index < modes.length) {
            return I18n.format(getUnlocalizedValue());
        } else {
            return I18n.format("betterHud.value.mode", index);
        }
    }

    @Override
    public void draw() {
        Point center = new Point(backing.x + backing.getWidth() / 2, backing.y + backing.getHeight() / 2);
        GlUtil.drawString(getLocalizedValue(), center, Direction.CENTER, Color.WHITE);
    }

    @Override
    public void updateGuiParts() {
        last.active = next.active = enabled();
    }
}
