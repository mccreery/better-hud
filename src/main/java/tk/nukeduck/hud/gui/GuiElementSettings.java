package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
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
import tk.nukeduck.hud.util.SettingsIO;

@SideOnly(Side.CLIENT)
public class GuiElementSettings extends GuiScreen {
	private static final int REPEAT_SPEED	  = 20; // Rate of speed-up to 20/s
	private static final int REPEAT_SPEED_FAST = 10; // Rate of speed-up beyond 20/s

	public HudElement element;
	ArrayList<GuiTextField> textboxList = new ArrayList<GuiTextField>();
	public HashMap<Gui, Setting<?>> callbacks = new HashMap<Gui, Setting<?>>();

	private Bounds viewport;

	private final GuiScreen prev;
	private GuiButton done;
	private GuiScrollbar scrollbar;

	public SettingAbsolutePosition currentPicking = null;
	private int clickTimer = 0;
	private GuiButton clickedUpDown = null;
	public static final Map<HudElement, Bounds> boundsCache = new HashMap<HudElement, Bounds>();

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

			// Notify the rest of the elements that a button has been pressed
			for(Setting<?> setting : callbacks.values()) {
				setting.otherAction(callbacks.values());
			}
		}
	}

	@Override
	public void updateScreen() {
		for(GuiTextField field : this.textboxList) {
			field.updateCursorCounter();
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

		if(currentPicking != null) {
			Bounds b = new Bounds(GuiElementSettings.boundsCache.get(element));
			b.x(Mouse.getEventX() * this.width / this.mc.displayWidth);
			b.y(this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1);

			if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				ArrayList<Bounds> bounds = new ArrayList<Bounds>();
				for(HudElement element : HudElement.ELEMENTS) {
					if(element == this.element || !element.settings.get()) continue;

					Bounds elementBounds = GuiElementSettings.boundsCache.get(element);
					if(elementBounds != null && elementBounds != Bounds.EMPTY) {
						bounds.add(elementBounds);
					}
				}
				b.snapTest(10, bounds.toArray(new Bounds[bounds.size()]));
				b.snapTest(10, new Bounds(this.width, 0, -this.width, this.height));
				b.snapTest(10, new Bounds(0, this.height, this.width, -this.height));
			}

			currentPicking.set(new Point(b.position));
			currentPicking.updateText();
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1) {
			SettingsIO.saveSettings(BetterHud.LOGGER);
		}
		super.keyTyped(typedChar, keyCode);

		// TODO can keyTyped and otherAction be combined? maybe "update"?
		for(GuiTextField field : this.textboxList) {
			field.textboxKeyTyped(typedChar, keyCode);

			if(callbacks.containsKey(field)) {
				callbacks.get(field).keyTyped(typedChar, keyCode);
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
		super.mouseClicked(mouseX, mouseY + getMouseOffset(), button);

		for(GuiTextField field : this.textboxList) {
			field.mouseClicked(mouseX, mouseY + getMouseOffset(), button);
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

		if(currentPicking != null && !currentPicking.pick.isMouseOver()) {
			currentPicking.pick.displayString = I18n.format("betterHud.menu.pick");
			currentPicking.isPicking = false;

			currentPicking = null;
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

		GL11.glPushMatrix();
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
		GL11.glPopMatrix();

		if(this.currentPicking != null) {
			String key = Keyboard.getKeyName(Keyboard.KEY_LCONTROL);
			drawString(fontRenderer, I18n.format("betterHud.text.unsnap", key), SPACER, this.height - fontRenderer.FONT_HEIGHT - SPACER, Colors.WHITE);

			drawBounds();
		}

		scrollbar.drawScrollbar(mouseX, mouseY);
		drawResolution(10, 10, 100);
	}

	/** Add to {@code mouseY} to get the effective {@code mouseY} taking into account scroll */
	private int getMouseOffset() {
		return scrollbar.getScroll() - viewport.top();
	}

	/** Draws bounds for all elements on the screen */
	private void drawBounds() {
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

		for(HudElement element : HudElement.ELEMENTS) {
			if(element.isEnabled()) {
				Bounds bounds = GuiElementSettings.boundsCache.get(element);

				if(bounds != null && bounds != Bounds.EMPTY) {
					HudElement.drawRect(bounds, Colors.setAlpha(Colors.RED, element == this.element ? 255 : 63));
				}
			}
		}

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	/** Draws a diagram of the size of the HUD */
	private void drawResolution(int x, int y, int width) {
		int height = width * this.height / this.width;

		// Precalculate width
		String widthDisplay = String.valueOf(this.width);
		int stringWidth = fontRenderer.getStringWidth(widthDisplay);

		// Horizontal
		int textX = x + (width - stringWidth) / 2;
		drawRect(x, y, textX - SPACER, y + 1, Colors.WHITE);
		drawRect(x + (width + stringWidth) / 2 + SPACER, y, x + width, y + 1, Colors.WHITE);
		fontRenderer.drawString(widthDisplay, textX, y, Colors.WHITE);

		// Vertical
		int textY = y + (height - fontRenderer.FONT_HEIGHT) / 2;
		drawRect(x, y, x + 1, textY - SPACER, Colors.WHITE);
		drawRect(x, y + (height + fontRenderer.FONT_HEIGHT) / 2 + SPACER, x + 1, y + height, Colors.WHITE);
		fontRenderer.drawString(String.valueOf(this.height), x, textY, Colors.WHITE);
	}
}
