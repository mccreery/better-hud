package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
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

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		super.drawButton(mc, mouseX, mouseY, partialTicks);

		if(isMouseOver() && !enabled) {
			MC.currentScreen.drawHoveringText(I18n.format("betterHud.unsupported"), mouseX, mouseY);
		}
	}
}
