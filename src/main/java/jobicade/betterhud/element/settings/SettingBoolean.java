package jobicade.betterhud.element.settings;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiActionButton;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.util.IGetSet.IBoolean;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.common.config.Property.Type;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SettingBoolean extends SettingAlignable<Boolean> implements IBoolean {
    public static final String VISIBLE = "betterHud.value.visible";

    protected GuiActionButton toggler;
    protected boolean value = false;

    private String unlocalizedValue = "options";

    public SettingBoolean(String name) {
        this(name, Direction.CENTER);
    }

    public SettingBoolean(String name, Direction alignment) {
        super(name, alignment);
    }

    public SettingBoolean setValuePrefix(String value) {
        this.unlocalizedValue = value;
        return this;
    }

    @Override
    protected Type getPropertyType() {
        return Type.BOOLEAN;
    }

    public Boolean get() {return enabled() && value;}
    public void set(Boolean value) {this.value = value;}

    @Override
    public void getGuiParts(List<AbstractGui> parts, Map<AbstractGui, Setting<?>> callbacks, Rect bounds) {
        toggler = new GuiActionButton("").setBounds(bounds).setCallback(b -> toggle());
        parts.add(toggler);
        callbacks.put(toggler, this);
    }

    @Override
    public void actionPerformed(GuiElementSettings gui, Button button) {
        toggler.actionPerformed();
    }

    @Override
    public String save() {
        return get().toString();
    }

    @Override
    public void load(String save) {
        set(Boolean.valueOf(save));
    }

    @Override
    public void updateGuiParts(Collection<Setting<?>> settings) {
        super.updateGuiParts(settings);
        toggler.field_146124_l = enabled();
        toggler.updateText(getUnlocalizedName(), unlocalizedValue, get());
    }
}
