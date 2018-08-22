package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.gui.GuiActionButton;
import tk.nukeduck.hud.gui.GuiElementChooser;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class SettingElement extends SettingAlignable<HudElement> {
	private HudElement value;
	private GuiActionButton button;

	public SettingElement(String name, Direction alignment) {
		super(name, alignment);
	}

	@Override
	public HudElement get() {
		return value;
	}

	@Override
	public void set(HudElement value) {
		this.value = value;
	}

	@Override
	public String save() {
		return value != null ? value.getUnlocalizedName() : "null";
	}

	@Override
	public void load(String save) {
		for(HudElement element : HudElement.ELEMENTS) {
			if(element.getUnlocalizedName().equals(save)) {
				value = element;
				return;
			}
		}
		value = null;
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		MC.displayGuiScreen(new GuiElementChooser(gui, gui.element, this));
	}

	@Override
	public void getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, Bounds bounds) {
		String text = getLocalizedName() + ": " + (value != null ? value.getLocalizedName() : I18n.format("betterHud.value.none"));
		button = new GuiActionButton(text);
		button.setBounds(bounds);

		parts.add(button);
		callbacks.put(button, this);
	}

	@Override
	public void updateGuiParts(Collection<Setting<?>> settings) {
		button.enabled = enabled();
	}
}
