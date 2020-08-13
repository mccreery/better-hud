package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Collection;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementChooser;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.SuperButton;
import jobicade.betterhud.registry.HudElements;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

public class SettingElement extends SettingAlignable {
    private HudElement<?> value;
    private SuperButton button;

    public SettingElement(Setting parent, String name, Direction alignment) {
        super(parent, name, alignment);
    }

    public HudElement<?> get() {
        return value;
    }

    public void set(HudElement<?> value) {
        this.value = value;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public String getStringValue() {
        return value != null ? value.getName() : "null";
    }

    @Override
    public void loadStringValue(String save) {
        value = HudElements.get().getRegistered(save);
    }

    @Override
    public void actionPerformed(GuiElementSettings gui, Button button) {
        button.onPress();
    }

    @Override
    public void getGuiParts(GuiElementSettings.Populator populator, Rect bounds) {
        String text = getLocalizedName() + ": " + (value != null ? value.getLocalizedName() : I18n.format("betterHud.value.none"));
        // TODO pass current gui into setting
        button = populator.add(new SuperButton(b -> MC.displayGuiScreen(new GuiElementChooser(MC.currentScreen, ((GuiElementSettings)MC.currentScreen).element, this))));
        button.setBounds(bounds);
        button.setMessage(text);
    }

    @Override
    public void updateGuiParts(Collection<Setting> settings) {
        button.active = enabled();
    }
}
