package jobicade.betterhud.element.settings;

import java.util.ArrayList;
import java.util.List;

import jobicade.betterhud.element.settings.gui.SettingGui;
import jobicade.betterhud.element.settings.serializer.SettingSerializer;
import net.minecraft.client.resources.I18n;

public final class Setting2 {
    public Setting2(String name) {
        this.name = name;
    }

    private final String name;
    public final String getName() {
        return name;
    }

    public final String getUnlocalizedName() {
        return "betterHud.setting." + name;
    }

    public final String getLocalizedName() {
        return I18n.format(getUnlocalizedName());
    }

    private final List<Setting2> children = new ArrayList<>();
    public final List<Setting2> getChildren() {
        return children;
    }

    private Setting2 parent;
    public final Setting2 getParent() {
        return parent;
    }

    public final void addChild(Setting2 child) {
        children.add(child);
        child.parent = this;
    }

    public final void addChildren(Setting2... children) {
        for (Setting2 child : children) {
            addChild(child);
        }
    }

    public final void addChildren(Iterable<Setting2> children) {
        children.forEach(this::addChild);
    }

    private SettingSerializer serializer;
    public void setSerializer(SettingSerializer serializer) {
        this.serializer = serializer;
    }

    public SettingSerializer getSerializer() {
        return serializer;
    }

    private SettingGui gui;
    public void setGui(SettingGui gui) {
        this.gui = gui;
    }

    public SettingGui getGui() {
        return gui;
    }
}
