package jobicade.betterhud.element.settings;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiActionButton;
import jobicade.betterhud.gui.GuiElementChooser;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.registry.HudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class SettingElement extends SettingAlignable {
	private HudElement<?> value;
	private GuiActionButton button;

	public SettingElement(String name, Direction alignment) {
		super(name, alignment);
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
	public String getDefaultValue() {
		return "null";
	}

	@Override
	public void loadStringValue(String save) {
		value = HudElements.get().getRegistered(save);
	}

	@Override
	public void loadDefaultValue() {
		value = null;
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiElementChooser(gui, gui.element, this));
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Rect bounds) {
		String text = getLocalizedName() + ": " + (value != null ? value.getLocalizedName() : I18n.format("betterHud.value.none"));
		button = new GuiActionButton(text);
		button.setBounds(bounds);

		parts.add(button);
		callbacks.put(button, this);
	}

	@Override
	public void updateGuiParts(Collection<Setting> settings) {
		button.enabled = enabled();
	}

	@Override
	public SettingElement setEnableOn(BooleanSupplier enableOn) {
		super.setEnableOn(enableOn);
		return this;
	}
}
