package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.util.Bounds;
import jobicade.betterhud.util.Point;
import jobicade.betterhud.util.mode.GlMode;

public class GuiActionButton extends GuiButton {
	private ActionCallback callback;
	private boolean repeat;

	public boolean glowing;

	private String tooltip;

	public GuiActionButton(String buttonText) {
		super(0, 0, 0, buttonText);
	}

	@Override
	protected int getHoverState(boolean mouseOver) {
		int state = super.getHoverState(mouseOver);
		return glowing && state == 1 ? 2 : state;
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

	public final void actionPerformed() {
		if(callback != null) callback.actionPerformed(this);
	}

	protected void drawButton(Bounds bounds, Point mousePosition, float partialTicks) {
		super.drawButton(BetterHud.MC, mousePosition.getX(), mousePosition.getY(), partialTicks);
	}

	public void updateText(String name, String value) {
		this.displayString = name + ": " + value;
	}

	public void updateText(String unlocalizedName, String valuePrefix, boolean value) {
		String valueDisplay;

		if(value) {
			valueDisplay = ChatFormatting.GREEN + I18n.format(valuePrefix + ".on");
		} else {
			valueDisplay = ChatFormatting.RED + I18n.format(valuePrefix + ".off");
		}
		updateText(I18n.format(unlocalizedName), valueDisplay);
		glowing = value;
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
