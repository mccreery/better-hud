package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.mode.GlMode;

public class GuiActionButton extends GuiButton implements ActionCallback {
	private ActionCallback callback;
	private boolean repeat;

	private String tooltip;

	public GuiActionButton(String buttonText) {
		super(0, 0, 0, buttonText);
	}

	public GuiActionButton setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	public GuiActionButton setCallback(ActionCallback callback) {
		this.callback = callback;
		return this;
	}

	public GuiActionButton setRepeat() {
		repeat = true;
		return this;
	}

	public boolean getRepeat() {
		return repeat;
	}

	public GuiActionButton setId(int id) {
		this.id = id;
		return this;
	}

	public GuiActionButton setBounds(Bounds bounds) {
		this.x = bounds.getX();
		this.y = bounds.getY();
		this.width = bounds.getWidth();
		this.height = bounds.getHeight();

		return this;
	}

	public Bounds getBounds() {
		return new Bounds(x, y, width, height);
	}

	@Override
	public void actionPerformed() {
		if(callback != null) callback.actionPerformed();
	}

	protected void drawButton(Bounds bounds, Point mousePosition, float partialTicks) {
		super.drawButton(BetterHud.MC, mousePosition.getX(), mousePosition.getY(), partialTicks);
	}

	public void updateText(String name, String value) {
		this.displayString = name + ": " + value;
	}

	@Override
	public final void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(!visible) return;

		GlMode.set(GlMode.DEFAULT);
		Bounds bounds = getBounds();
		hovered = bounds.contains(mouseX, mouseY);

		drawButton(bounds, new Point(mouseX, mouseY), partialTicks);

		if(tooltip != null && hovered) {
			GlMode.push(new GlMode(GlStateManager::enableDepth, GlStateManager::disableDepth));
			GlStateManager.pushMatrix();

			GlStateManager.translate(0, 0, 1);
			MC.currentScreen.drawHoveringText(tooltip, mouseX, mouseY);

			GlStateManager.popMatrix();
			GlMode.pop();
		}
	}
}
