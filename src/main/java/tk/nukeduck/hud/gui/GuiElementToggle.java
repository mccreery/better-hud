package tk.nukeduck.hud.gui;

import net.minecraft.client.gui.GuiScreen;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.HudElement.SortType;

public class GuiElementToggle extends GuiSettingToggle {
	private final GuiScreen callback;

	public GuiElementToggle(GuiScreen callback, HudElement element) {
		super(element.getUnlocalizedName(), element.settings);
		this.callback = callback;
	}

	@Override
	public void actionPerformed() {
		super.actionPerformed();

		HudElement.SORTER.markDirty(SortType.ENABLED);
		callback.initGui();
	}
}
