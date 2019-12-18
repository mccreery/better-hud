package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiElementSettings extends GuiMenuScreen {
	private static final int REPEAT_SPEED	   = 20; // Rate of speed-up to 20/s
	private static final int REPEAT_SPEED_FAST = 10; // Rate of speed-up beyond 20/s

	public HudElement element;
	private ArrayList<GuiTextField> textboxList = new ArrayList<GuiTextField>();
	public HashMap<Gui, Setting<?>> callbacks = new HashMap<Gui, Setting<?>>();

	private Rect viewport;

	private final GuiActionButton done = new GuiActionButton(I18n.format("gui.done"));
	private GuiScrollbar scrollbar;

	private int repeatTimer = 0;

	public GuiElementSettings(HudElement element, GuiScreen prev) {
		this.element = element;
		done.setCallback(b -> Minecraft.getInstance().displayGuiScreen(prev));

		//children.add(scrollbar); // TODO
		children.add(done);
	}

	@Override
	public void initGui() {
		setTitle(I18n.format("betterHud.menu.settings", this.element.getLocalizedName()));
		buttons.clear();
		textboxList.clear();
		labels.clear();

		done.setBounds(new Rect(200, 20).align(getOrigin(), Direction.NORTH));

		List<Gui> parts = new ArrayList<Gui>();
		int contentHeight = element.settings.getGuiParts(parts, callbacks, new Point(width / 2, SPACER)).getY();

		for(Gui gui : parts) {
			if(gui instanceof GuiButton) {
				buttons.add((GuiButton)gui);
			} else if(gui instanceof GuiLabel) {
				labels.add((GuiLabel)gui);
			} else if(gui instanceof GuiTextField) {
				textboxList.add((GuiTextField)gui);
			}
		}

		viewport = new Rect(width / 2 - 200, height / 16 + 40 + SPACER, 400, 0).withBottom(height - 20);
		scrollbar = new GuiScrollbar(viewport, contentHeight);

		for(Setting<?> setting : callbacks.values()) {
			setting.updateGuiParts(callbacks.values());
		}
	}

	@Override
	public void onGuiClosed() {
		BetterHud.getConfigManager().getConfig().saveSettings();
	}

	/*@Override
	protected void actionPerformed(GuiButton button) {
		if(callbacks.containsKey(button)) {
			callbacks.get(button).actionPerformed(this, button);

			// Notify the rest of the elements that a button has been pressed
			for(Setting<?> setting : callbacks.values()) {
				setting.updateGuiParts(callbacks.values());
			}
		} else {
			super.actionPerformed(button);
		}
	}*/

	/** @see GuiScreen#handleMouseInput() */
	/*@Override
	public void updateScreen() {
		for(GuiTextField field : this.textboxList) {
			field.updateCursorCounter();
		}

		if(selectedButton instanceof GuiActionButton && ((GuiActionButton)selectedButton).getRepeat()) {
			// Slowly build up speed until 1/tick after REPEAT_SPEED ticks
			if(++repeatTimer % Math.max(1, Math.round(REPEAT_SPEED / repeatTimer)) == 0) {
				// When above REPEAT_SPEED, repeat multiple times per tick
				int c = Math.max(1, (repeatTimer - REPEAT_SPEED) / REPEAT_SPEED_FAST);

				for(int i = 0; i < c; i++) {
					actionPerformed(selectedButton);
				}
			}
		} else {
			repeatTimer = 0;
		}
	}*/

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if(super.charTyped(typedChar, keyCode)) return true;

		for(GuiTextField field : this.textboxList) {
			field.charTyped(typedChar, keyCode);

			if(callbacks.containsKey(field)) {
				callbacks.get(field).updateGuiParts(callbacks.values());
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int modifiers) {
		if(mouseY >= viewport.getTop() && mouseY < viewport.getBottom()) {
			//super.mouseClicked(mouseX, mouseY + getMouseOffset(), button);

			for(GuiTextField field : this.textboxList) {
				//field.mouseClicked(mouseX, mouseY + getMouseOffset(), button);
			}
		}

		// Done button isn't in buttons, have to handle it manually
		/*if(done.mousePressed(this.mc, mouseX, mouseY)) {
			ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, done, buttons);
			if(MinecraftForge.EVENT_BUS.post(event)) return;

			GuiButton eventResult = event.getButton();
			selectedButton = eventResult;
			eventResult.playPressSound(this.mc.getSoundHandler());
			actionPerformed(eventResult);

			if(this.equals(MC.currentScreen)) {
				MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, done, buttons));
			}
			return true;
		}*/
		return true;//
	}

	/*@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawTitle();

		done.render(mouseX, mouseY, partialTicks);

		GlStateManager.pushMatrix();
		GlUtil.beginScissor(viewport);
		GL11.glTranslatef(0, -getMouseOffset(), 0);

		int viewportMouseY = mouseY + getMouseOffset();

		for(GuiButton button : buttons) button.render(mouseX, viewportMouseY, partialTicks);
		for(GuiLabel label : labels) label.render(mouseX, viewportMouseY, partialTicks);

		for(GuiTextField field : this.textboxList) {
			field.drawTextField(mouseX, viewportMouseY, partialTicks);
		}
		element.settings.draw();

		GlUtil.endScissor();
		GlStateManager.popMatrix();

		scrollbar.drawScrollbar(mouseX, mouseY);
		drawResolution(10, 10, 100);
	}*/

	/** Add to {@code mouseY} to get the effective {@code mouseY} taking into account scroll */
	/*@Deprecated private int getMouseOffset() {
		return scrollbar.getScroll() - viewport.getTop();
	}*/

	/** Draws a diagram of the size of the HUD */
	/*private void drawResolution(int x, int y, int width) {
		int height = width * this.height / this.width;

		// Precalculate width
		String widthDisplay = String.valueOf(this.width);
		int stringWidth = fontRenderer.getStringWidth(widthDisplay);

		// Horizontal
		int textX = x + (width - stringWidth) / 2;
		drawHorizontalLine(x, textX - SPACER, y, Color.WHITE.getPacked());
		drawHorizontalLine(x + (width + stringWidth) / 2 + SPACER, x + width, y, Color.WHITE.getPacked());
		fontRenderer.drawString(widthDisplay, textX, y, Color.WHITE.getPacked());

		// Vertical
		int textY = y + (height - fontRenderer.FONT_HEIGHT) / 2;
		drawVerticalLine(x, y, textY - SPACER, Color.WHITE.getPacked());
		drawVerticalLine(x, y + (height + fontRenderer.FONT_HEIGHT) / 2 + SPACER, y + height, Color.WHITE.getPacked());
		fontRenderer.drawString(String.valueOf(this.height), x, textY, Color.WHITE.getPacked());
	}*/
}
