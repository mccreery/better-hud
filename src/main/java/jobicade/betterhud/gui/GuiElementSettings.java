package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.IGuiController;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
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

public class GuiElementSettings extends GuiMenuScreen {
	private static final int REPEAT_SPEED	   = 20; // Rate of speed-up to 20/s
	private static final int REPEAT_SPEED_FAST = 10; // Rate of speed-up beyond 20/s

	public HudElement<?> element;
	private ArrayList<GuiTextField> textboxList = new ArrayList<>();
	public HashMap<Gui, IGuiController> callbacks = new HashMap<>();

	private Rect viewport;

	private final GuiActionButton done = new GuiActionButton(I18n.format("gui.done"));
	private GuiScrollbar scrollbar;

	private int repeatTimer = 0;

	public GuiElementSettings(HudElement<?> element, GuiScreen prev) {
		this.element = element;
		done.setCallback(b -> Minecraft.getMinecraft().displayGuiScreen(prev));
	}

	@Override
	public void initGui() {
		setTitle(I18n.format("betterHud.menu.settings", this.element.getLocalizedName()));
		buttonList.clear();
		textboxList.clear();
		labelList.clear();

		Keyboard.enableRepeatEvents(true);
		done.setBounds(new Rect(200, 20).align(getOrigin(), Direction.NORTH));

		int contentHeight = populateGui(element.settings, new Point(width / 2, SPACER));

		viewport = new Rect(width / 2 - 200, height / 16 + 40 + SPACER, 400, 0).withBottom(height - 20);
		scrollbar = new GuiScrollbar(viewport, contentHeight);

		otherUpdate();
	}

	private int populateGui(Setting setting, Point topAnchor) {
		if (setting.isHidden()) {
			return 0;
		}
		int height = 0;

		for (Setting childSetting : setting.getChildren()) {
			height += populateGui(childSetting, topAnchor.add(0, height));
		}

		IGuiController controller = setting.getGuiController();
		if (controller != null) {
			height += controller.populateGui(topAnchor.add(0, height), new Populator(controller));
		}
		return height;
	}

	private void otherUpdate() {
		for (IGuiController controller : callbacks.values()) {
			controller.otherUpdate();
		}
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		BetterHud.getProxy().getConfig().saveSettings();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(callbacks.containsKey(button)) {
			callbacks.get(button).actionPerformed(button);
			otherUpdate();
		} else {
			super.actionPerformed(button);
		}
	}

	/** @see GuiScreen#handleMouseInput() */
	@Override
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
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		for(GuiTextField field : this.textboxList) {
			field.textboxKeyTyped(typedChar, keyCode);

			if(callbacks.containsKey(field)) {
				callbacks.get(field).textboxKeyTyped(typedChar, keyCode);
				otherUpdate();
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
		if(mouseY >= viewport.getTop() && mouseY < viewport.getBottom()) {
			super.mouseClicked(mouseX, mouseY + getMouseOffset(), button);

			for(GuiTextField field : this.textboxList) {
				field.mouseClicked(mouseX, mouseY + getMouseOffset(), button);
			}
		}

		// Done button isn't in buttonList, have to handle it manually
		if(done.mousePressed(this.mc, mouseX, mouseY)) {
			ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, done, buttonList);
			if(MinecraftForge.EVENT_BUS.post(event)) return;

			GuiButton eventResult = event.getButton();
			selectedButton = eventResult;
			eventResult.playPressSound(this.mc.getSoundHandler());
			actionPerformed(eventResult);

			if(this.equals(Minecraft.getMinecraft().currentScreen)) {
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
		drawTitle();

		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		done.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks);

		GlStateManager.pushMatrix();
		GlUtil.beginScissor(viewport, resolution);
		GL11.glTranslatef(0, -getMouseOffset(), 0);

		int viewportMouseY = mouseY + getMouseOffset();

		for(GuiButton button : buttonList) button.drawButton(mc, mouseX, viewportMouseY, partialTicks);
		for(GuiLabel label : labelList) label.drawLabel(mc, mouseX, viewportMouseY);

		for(GuiTextField field : this.textboxList) {
			field.drawTextBox();
		}
		element.settings.draw();

		GlUtil.endScissor();
		GlStateManager.popMatrix();

		scrollbar.drawScrollbar(mouseX, mouseY);
		drawResolution(10, 10, 100);
	}

	/** Add to {@code mouseY} to get the effective {@code mouseY} taking into account scroll */
	@Deprecated private int getMouseOffset() {
		return scrollbar.getScroll() - viewport.getTop();
	}

	/** Draws a diagram of the size of the HUD */
	private void drawResolution(int x, int y, int width) {
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
	}

	private class Populator implements IGuiController.Populator {
		private final IGuiController controller;

		private Populator(IGuiController controller) {
			this.controller = controller;
		}

		@Override
		public void addButton(GuiButton button) {
			buttonList.add(button);
			callbacks.put(button, controller);
		}

		@Override
		public void addLabel(GuiLabel label) {
			labelList.add(label);
			callbacks.put(label, controller);
		}

		@Override
		public void addTextField(GuiTextField textField) {
			textboxList.add(textField);
			callbacks.put(textField, controller);
		}
	}
}
