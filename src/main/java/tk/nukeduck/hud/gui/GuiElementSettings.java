package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.element.settings.SettingAbsolutePosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Point;

@SideOnly(Side.CLIENT)
public class GuiElementSettings extends GuiScreen {
	private static final int REPEAT_SPEED	  = 20; // Rate of speed-up to 20/s
	private static final int REPEAT_SPEED_FAST = 10; // Rate of speed-up beyond 20/s

	public HudElement element;
	private ArrayList<GuiTextField> textboxList = new ArrayList<GuiTextField>();
	public HashMap<Gui, Setting<?>> callbacks = new HashMap<Gui, Setting<?>>();
	private SettingAbsolutePosition picker = null;

	private Bounds viewport;

	private final GuiScreen prev;
	private GuiButton done;
	private GuiScrollbar scrollbar;

	private int clickTimer = 0;
	private GuiButton clickedUpDown = null;
	//public static final Map<HudElement, Bounds> boundsCache = new HashMap<HudElement, Bounds>();

	public GuiElementSettings(HudElement element, GuiScreen prev) {
		this.element = element;
		this.prev = prev;
	}

	@Override
	public void initGui() {
		buttonList.clear();
		textboxList.clear();
		labelList.clear();

		Keyboard.enableRepeatEvents(true);
		done = new GuiButton(-1, this.width / 2 - 100, height / 16 + 20, I18n.format("gui.done"));

		List<Gui> parts = new ArrayList<Gui>();
		int content = element.settings.getGuiParts(parts, callbacks, width, SPACER);

		for(Gui gui : parts) {
			if(gui instanceof GuiButton) {
				buttonList.add((GuiButton)gui);
			} else if(gui instanceof GuiLabel) {
				labelList.add((GuiLabel)gui);
			} else if(gui instanceof GuiTextField) {
				textboxList.add((GuiTextField)gui);
			}
		}

		viewport = new Bounds(width / 2 - 200, height / 16 + 40 + SPACER, 400, 0);
		viewport.bottom(height - 20);
		scrollbar = new GuiScrollbar(viewport, content);

		for(Setting<?> setting : callbacks.values()) {
			setting.updateGuiParts(callbacks.values());
		}
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button.id == -1) {
			mc.displayGuiScreen(prev);
		} else {
			if(callbacks.containsKey(button)) {
				callbacks.get(button).actionPerformed(this, button);
			}

			picker = null;

			// Notify the rest of the elements that a button has been pressed
			for(Setting<?> setting : callbacks.values()) {
				setting.updateGuiParts(callbacks.values());

				if(setting instanceof SettingAbsolutePosition) {
					if(((SettingAbsolutePosition)setting).isPicking()) {
						picker = (SettingAbsolutePosition)setting;
					}
				}
			}
		}
	}

	/** @see GuiScreen#handleMouseInput() */
	@Override
	public void updateScreen() {
		for(GuiTextField field : this.textboxList) {
			field.updateCursorCounter();
		}

		if(picker != null && (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0)) {
			Point mousePosition = new Point(
				Mouse.getEventX() * width / MC.displayWidth,
				height - Mouse.getEventY() * height / MC.displayHeight - 1
			);
			Point resolution = new Point(width, height);

			picker.pickMouse(mousePosition, resolution, element);
		}

		if(Mouse.isButtonDown(0)) {
			if(clickedUpDown == null) {
				for(Object obj : this.buttonList) {
					GuiButton button = (GuiButton) obj;
					if(button instanceof GuiUpDownButton && button.isMouseOver()) {
						clickedUpDown = button;
						break;
					}
				}
			}
		} else {
			clickedUpDown = null;
		}

		if(clickedUpDown != null) {
			if(++clickTimer % Math.max(1, Math.round(REPEAT_SPEED / clickTimer)) == 0) {
				int c = Math.max(1, (clickTimer - REPEAT_SPEED) / REPEAT_SPEED_FAST);
				for(int i = 0; i < c; i++) {
					this.actionPerformed(clickedUpDown);
				}
			}
		} else {
			clickTimer = 0;
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if(MC.currentScreen == null) {
			BetterHud.CONFIG.saveSettings();
		}

		for(GuiTextField field : this.textboxList) {
			field.textboxKeyTyped(typedChar, keyCode);

			if(callbacks.containsKey(field)) {
				callbacks.get(field).updateGuiParts(callbacks.values());
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		scrollbar.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		boolean wasPicking = picker != null;

		if(mouseY >= viewport.top() && mouseY < viewport.bottom()) {
			super.mouseClicked(mouseX, mouseY + getMouseOffset(), button);

			for(GuiTextField field : this.textboxList) {
				field.mouseClicked(mouseX, mouseY + getMouseOffset(), button);
			}
		}

		if(wasPicking && picker != null) {
			picker.finishPicking();
			picker = null;
			return;
		}

		// Done button isn't in buttonList, have to handle it manually
		if(done.mousePressed(this.mc, mouseX, mouseY)) {
			ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, done, buttonList);
			if(MinecraftForge.EVENT_BUS.post(event)) return;

			GuiButton eventResult = event.getButton();
			selectedButton = eventResult;
			eventResult.playPressSound(this.mc.getSoundHandler());
			actionPerformed(eventResult);

			if(this.equals(MC.currentScreen)) {
				MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, done, buttonList));
			}
		}
		scrollbar.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long heldTime) {
		super.mouseClickMove(mouseX, mouseY + getMouseOffset(), button, heldTime);
		scrollbar.mouseClickMove(mouseX, mouseY, button, heldTime);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int button) {
		super.mouseReleased(mouseX, mouseY + getMouseOffset(), button);
		scrollbar.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(this.fontRenderer, I18n.format("betterHud.menu.settings", this.element.getLocalizedName()), this.width / 2, height / 16 + 5, Colors.WHITE);

		ScaledResolution resolution = new ScaledResolution(MC);
		done.drawButton(MC, mouseX, mouseY, partialTicks);

		GlStateManager.pushMatrix();
		GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(viewport.x() * resolution.getScaleFactor(), (height - viewport.y() - viewport.height()) * resolution.getScaleFactor(), viewport.width() * resolution.getScaleFactor(), viewport.height() * resolution.getScaleFactor());

		GL11.glTranslatef(0, -getMouseOffset(), 0);

		super.drawScreen(mouseX, mouseY + getMouseOffset(), partialTicks);
		for(GuiTextField field : this.textboxList) {
			field.drawTextBox();
		}
		element.settings.draw();

		GL11.glPopAttrib();
		GlStateManager.popMatrix();

		if(picker != null) {
			String key = Keyboard.getKeyName(Keyboard.KEY_LCONTROL);
			drawString(fontRenderer, I18n.format("betterHud.text.unsnap", key), SPACER, this.height - fontRenderer.FONT_HEIGHT - SPACER, Colors.WHITE);

			drawBounds();
		}

		scrollbar.drawScrollbar(mouseX, mouseY);
		drawResolution(10, 10, 100);
	}

	/** Add to {@code mouseY} to get the effective {@code mouseY} taking into account scroll */
	@Deprecated private int getMouseOffset() {
		return scrollbar.getScroll() - viewport.top();
	}

	/** Draws bounds for all elements on the screen */
	private void drawBounds() {
		for(HudElement element : HudElement.ELEMENTS) {
			Bounds bounds = element.getLastBounds();

			if(!bounds.isEmpty()) {
				drawBounds(bounds, Colors.setAlpha(Colors.RED, element == this.element ? 255 : 63));
			} else if(element == this.element && picker != null) {
				drawCrosshair(picker.getAbsolute(), Colors.RED);
			}
		}
	}

	private void drawBounds(Bounds bounds, int color) {
		drawHorizontalLine(bounds.left(), bounds.right(), bounds.top(), color);
		drawHorizontalLine(bounds.left(), bounds.right(), bounds.bottom(), color);

		drawVerticalLine(bounds.left(), bounds.top(), bounds.bottom(), color);
		drawVerticalLine(bounds.right(), bounds.top(), bounds.bottom(), color);
	}

	private void drawCrosshair(Point center, int color) {
		drawHorizontalLine(center.x - 10, center.x + 10, center.y, color);
		drawVerticalLine(center.x, center.y - 10, center.y + 10, color);
	}

	/** Draws a diagram of the size of the HUD */
	private void drawResolution(int x, int y, int width) {
		int height = width * this.height / this.width;

		// Precalculate width
		String widthDisplay = String.valueOf(this.width);
		int stringWidth = fontRenderer.getStringWidth(widthDisplay);

		// Horizontal
		int textX = x + (width - stringWidth) / 2;
		drawHorizontalLine(x, textX - SPACER, y, Colors.WHITE);
		drawHorizontalLine(x + (width + stringWidth) / 2 + SPACER, x + width, y, Colors.WHITE);
		fontRenderer.drawString(widthDisplay, textX, y, Colors.WHITE);

		// Vertical
		int textY = y + (height - fontRenderer.FONT_HEIGHT) / 2;
		drawVerticalLine(x, y, textY - SPACER, Colors.WHITE);
		drawVerticalLine(x, y + (height + fontRenderer.FONT_HEIGHT) / 2 + SPACER, y + height, Colors.WHITE);
		fontRenderer.drawString(String.valueOf(this.height), x, textY, Colors.WHITE);
	}
}
