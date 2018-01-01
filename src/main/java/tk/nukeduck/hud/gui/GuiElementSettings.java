package tk.nukeduck.hud.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.primitives.Floats;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
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
	public HudElement element;
	ArrayList<GuiTextField> textboxList = new ArrayList<GuiTextField>();
	public HashMap<Gui, Setting> callbacks = new HashMap<Gui, Setting>();

	private float scrollFactor = 0.0F;
	private int scrollHeight = 0;

	private int totalHeight;
	private int top, barHeight, bottom;

	public GuiElementSettings(HudElement element, GuiScreen prev) {
		this.element = element;
		this.prev = prev;
	}

	GuiScreen prev;

	@Override
	public void initGui() {
		this.textboxList.clear();

		Keyboard.enableRepeatEvents(true);
		this.buttonList.add(new GuiButton(-1, this.width / 2 - 100, height / 16 + 20, I18n.format("gui.done")));

		List<Gui> parts = new ArrayList<Gui>();
		this.totalHeight = 0;
		totalHeight = element.settings.getGuiParts(parts, callbacks, width, totalHeight);

		for(Gui gui : parts) {
			if(gui instanceof GuiButton) {
				buttonList.add((GuiButton)gui);
			} else if(gui instanceof GuiLabel) {
				labelList.add((GuiLabel)gui);
			} else if(gui instanceof GuiTextField) {
				textboxList.add((GuiTextField)gui);
			}
		}

		this.totalHeight += 25;
		this.scrollFactor = this.scrollHeight = 0;

		this.top = this.height / 16 + 20;
		this.bottom = this.height - 16;
		this.barHeight = this.bottom - this.top;
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
			for(Setting setting : callbacks.values()) {
				setting.otherAction(callbacks.values());
			}
		}
	}

	public SettingAbsolutePosition currentPicking = null;
	private int clickTimer = 0;

	private GuiButton clickedUpDown = null;

	private static final int REPEAT_SPEED = 20; // Rate of speed-up to 20/s
	private static final int REPEAT_SPEED_FAST = 10; // Rate of speed-up beyond 20/s

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
			Bounds b = new Bounds(BetterHud.boundsCache.get(element));
			b.x(Mouse.getEventX() * this.width / this.mc.displayWidth);
			b.y(this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1);

			if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				ArrayList<Bounds> bounds = new ArrayList<Bounds>();
				for(HudElement element : HudElement.ELEMENTS) {
					if(element == this.element || !element.settings.get()) continue;

					Bounds elementBounds = BetterHud.boundsCache.get(element);
					if(elementBounds != null && elementBounds != Bounds.EMPTY) {
						bounds.add(elementBounds);
					}
				}
				b.snapTest(10, bounds.toArray(new Bounds[bounds.size()]));
				b.snapTest(10, new Bounds(this.width, 0, -this.width, this.height));
				b.snapTest(10, new Bounds(0, this.height, this.width, -this.height));
			}

			currentPicking.position = new Point(b.position);

			currentPicking.updateText();
		}
	}

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
		if(!this.scrolling && this.totalHeight > barHeight) {
			int i = Mouse.getEventDWheel();
			this.setScroll(this.scrollFactor - (float) i / 1500.0F);
		}
	}

	public boolean scrolling;

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(this.totalHeight > barHeight) {
			int x = this.width / 2 + 160;
			if(this.scrolling = mouseX >= x && mouseX < x + 4 && mouseY >= top && mouseY < bottom) {
				this.setScroll((float) (mouseY - top - 15) / (float) (barHeight - 30));
			}
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);

		for(GuiTextField field : this.textboxList) {
			field.mouseClicked(mouseX, mouseY, mouseButton);
		}

		if(currentPicking != null && !currentPicking.pick.isMouseOver()) {
			currentPicking.pick.displayString = I18n.format("betterHud.menu.pick");
			currentPicking.isPicking = false;

			currentPicking = null;
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
		this.scrolling = false;
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		if(this.scrolling) {
			this.setScroll((float) (mouseY - top - 15) / (float) (barHeight - 30));
		}
	}

	public void setScroll(float scroll) {
		if(this.totalHeight <= barHeight) return;

		// Bring buttons back to 0
		this.scrollHeight = this.totalHeight - barHeight;
		this.scrollHeight *= this.scrollFactor;
		for(Object button : this.buttonList) ((GuiButton) button).y += this.scrollHeight;
		for(GuiTextField box : this.textboxList) box.y += this.scrollHeight;

		this.scrollFactor = Floats.constrainToRange(scroll, 0f, 1f);

		// Move buttons to new Y
		this.scrollHeight = this.totalHeight - barHeight;
		this.scrollHeight *= this.scrollFactor;
		for(Object button : this.buttonList) ((GuiButton) button).y -= this.scrollHeight;
		for(GuiTextField box : this.textboxList) box.y -= this.scrollHeight;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		this.drawDefaultBackground();
		ScaledResolution res = new ScaledResolution(mc);
		//super.drawScreen(mouseX, mouseY, p_73863_3_);

		this.drawCenteredString(this.fontRenderer, I18n.format("betterHud.menu.settings", this.element.getLocalizedName()), this.width / 2, height / 16 + 5, 16777215);

		if(this.totalHeight > barHeight) {
			int noduleY = top + Math.round(this.scrollFactor * (barHeight - 30));

			Gui.drawRect(this.width / 2 + 160, top, this.width / 2 + 164, top + barHeight, 0xAA555555);
			Gui.drawRect(this.width / 2 + 160, noduleY, this.width / 2 + 164, noduleY + 30, 0x66FFFFFF);
		} else this.setScroll(0);

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(0, 16 * res.getScaleFactor(), mc.displayWidth, (this.height - top - 15) * res.getScaleFactor());
		//GL11.glTranslatef(0, -scrollAmt, 0);

		super.drawScreen(mouseX, mouseY, p_73863_3_);
		for(GuiTextField field : this.textboxList) {
			field.drawTextBox();
		}

		/*int heightShown = this.height - (top + 16);
		int scrollAmt = this.totalHeight - heightShown;
		scrollAmt *= this.scrollFactor;*/
		//GL11.glTranslatef(0, -scrollAmt, 0);
		/*for(Setting setting : element.settings) {
			setting.render(this, this.scrollHeight);
		}*/
		GL11.glPopAttrib();
		GL11.glPopMatrix();

		int w = 100;
		int h = Math.round(100 * (float) height / (float) width);

		String wStr = String.valueOf(width);
		String hStr = String.valueOf(height);
		int wStrW = fontRenderer.getStringWidth(wStr) + 10;
		int hStrH = fontRenderer.FONT_HEIGHT + 10;

		Gui.drawRect(20, 10, 20 + (w - wStrW) / 2, 11, Colors.fromRGB(255, 255, 255));
		Gui.drawRect(20 + (w + wStrW) / 2, 10, 20 + w, 11, Colors.fromRGB(255, 255, 255));
		this.drawCenteredString(fontRenderer, wStr, 20 + w / 2, 10, Colors.fromRGB(255, 255, 255));

		Gui.drawRect(10, 20, 11, 20 + (h - hStrH) / 2, Colors.fromRGB(255, 255, 255));
		Gui.drawRect(10, 20 + (h + hStrH) / 2, 11, 20 + h, Colors.fromRGB(255, 255, 255));
		this.drawString(fontRenderer, hStr, 10, 20 + (h - hStrH + 10) / 2, Colors.fromRGB(255, 255, 255));

		if(this.currentPicking != null) {
			String disableSnap = I18n.format("betterHud.text.unsnap", Keyboard.getKeyName(Keyboard.KEY_LCONTROL));
			this.drawString(fontRenderer, disableSnap, 5, this.height - fontRenderer.FONT_HEIGHT - 5, 0xffffff);

			// TODO wot
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			for(HudElement element : HudElement.ELEMENTS) {
				Bounds b = BetterHud.boundsCache.get(element);

				if(element.settings.get() && b != null && b != Bounds.EMPTY) {
					Gui.drawRect(b.left(), b.top(), b.right(), b.bottom(), Colors.fromARGB(element == this.element ? 255 : 50, 255, 0, 0));
				}
			}
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
	}
}
