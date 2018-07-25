package tk.nukeduck.hud.gui;

import net.minecraft.client.Minecraft;
import tk.nukeduck.hud.element.HudElement;

class PriorityButton extends GuiUpDownButton {
	private final GuiHudMenu menu;
	private final HudElement element;
	private final boolean up;

	public PriorityButton(GuiHudMenu callback, HudElement element, boolean up) {
		super(up);

		this.menu = callback;
		this.element = element;
		this.up = up;
	}

	@Override
	public void actionPerformed() {
		menu.swapPriority(element, up ? -1 : 1);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(menu.showArrows()) {
			super.drawButton(mc, mouseX, mouseY, partialTicks);
		} else {
			hovered = false; // Stop click events
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return menu.showArrows() && super.mousePressed(mc, mouseX, mouseY);
	}
}
