package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiUpDownButton;
import tk.nukeduck.hud.util.Point;

public class SettingAbsolutePosition extends Setting<Point> {
	public GuiTextField xBox, yBox;
	public GuiButton pick;
	private GuiButton xUp, xDown, yUp, yDown;

	private Point position = Point.ZERO;
	private Point cancelPosition;

	public SettingAbsolutePosition(String name) {
		super(name);
	}

	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, int width, int y) {
		parts.add(xBox = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, width / 2 - 106, y + 1, 80, 18));
		xBox.setText(String.valueOf(position.x));
		parts.add(yBox = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, width / 2 + 2, y + 1, 80, 18));
		yBox.setText(String.valueOf(position.y));
		parts.add(xUp = new GuiUpDownButton(0, width / 2 - 22, y, 0));
		parts.add(xDown = new GuiUpDownButton(1, width / 2 - 22, y + 10, 1));
		parts.add(yUp = new GuiUpDownButton(2, width / 2 + 86, y, 0));
		parts.add(yDown = new GuiUpDownButton(3, width / 2 + 86, y + 10, 1));

		parts.add(pick = new GuiButton(4, width / 2 - 75, y + 22, 150, 20, I18n.format("betterHud.menu.pick")));

		callbacks.put(xBox, this);
		callbacks.put(yBox, this);
		callbacks.put(xUp, this);
		callbacks.put(xDown, this);
		callbacks.put(yUp, this);
		callbacks.put(yDown, this);
		callbacks.put(pick, this);

		return y + 42 + SPACER;
	}

	public boolean isPicking = false;

	public void updateText() {
		xBox.setText(String.valueOf(position.x));
		yBox.setText(String.valueOf(position.y));
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		switch(button.id) {
			case 0:
				xBox.setText(String.valueOf(++position.x));
				break;
			case 1:
				xBox.setText(String.valueOf(--position.x));
				break;
			case 2:
				yBox.setText(String.valueOf(++position.y));
				break;
			case 3:
				yBox.setText(String.valueOf(--position.y));
				break;
			case 4:
				if(isPicking = !isPicking) {
					cancelPosition = new Point(position);
				} else {
					position = cancelPosition;
				}

				gui.currentPicking = isPicking ? this : null;
				button.displayString = I18n.format(isPicking ? "betterHud.menu.picking" : "betterHud.menu.pick");
				updateText();
				break;
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		if(!pick.enabled) return;

		xUp.enabled = true;
		xDown.enabled = true;
		try {
			position.x = Integer.parseInt(xBox.getText());
		} catch(NumberFormatException e) {
			position.x = 0;
			xUp.enabled = false;
			xDown.enabled = false;
		}
		yUp.enabled = true;
		yDown.enabled = true;
		try {
			position.y = Integer.parseInt(yBox.getText());
		} catch(NumberFormatException e) {
			position.y = 0;
			yUp.enabled = false;
			yDown.enabled = false;
		}
	}

	@Override
	public void set(Point value) {
		position = value;
	}

	@Override
	public Point get() {
		return position;
	}

	@Override
	public String save() {
		return get().save();
	}

	@Override
	public void load(String val) {
		position.load(val);
	}

	@Override
	public void otherAction(Collection<Setting<?>> settings) {
		boolean enabled = enabled();
		xBox.setEnabled(enabled);
		yBox.setEnabled(enabled);
		pick.enabled = enabled;
		xUp.enabled = enabled;
		xDown.enabled = enabled;
		yUp.enabled = enabled;
		yDown.enabled = enabled;
	}
}
