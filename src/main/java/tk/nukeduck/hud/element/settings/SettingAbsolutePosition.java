package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiUpDownButton;
import tk.nukeduck.hud.util.Point;

public class SettingAbsolutePosition extends Setting<Point> {
	public GuiTextField xBox, yBox;
	public GuiButton pick;
	private GuiButton xUp, xDown, yUp, yDown;

	protected int x, y, cancelX, cancelY;
	protected boolean isPicking = false;

	public boolean isPicking() {
		return isPicking;
	}

	public SettingAbsolutePosition(String name) {
		super(name);
	}

	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting<?>> callbacks, int width, int y) {
		parts.add(xBox = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, width / 2 - 106, y + 1, 80, 18));
		xBox.setText(String.valueOf(x));
		parts.add(yBox = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, width / 2 + 2, y + 1, 80, 18));
		yBox.setText(String.valueOf(y));
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

	public void updateText() {
		if(xBox != null && yBox != null) {
			xBox.setText(String.valueOf(x));
			yBox.setText(String.valueOf(y));
		}
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		switch(button.id) {
			case 0: xBox.setText(String.valueOf(++x)); break;
			case 1: xBox.setText(String.valueOf(--x)); break;
			case 2: yBox.setText(String.valueOf(++y)); break;
			case 3: yBox.setText(String.valueOf(--y)); break;
			case 4:
				if(isPicking) {
					x = cancelX;
					y = cancelY;
					finishPicking();
				} else {
					cancelX = x;
					cancelY = y;
					isPicking = true;
					button.displayString = I18n.format("betterHud.menu.picking");
				}
				updateText();
				break;
		}
	}

	/** Sets the picked value based on {@code mousePosition}
	 * @param element The element being positioned */
	public void pickMouse(Point mousePosition, Point resolution, HudElement element) {
		set(mousePosition);
	}

	/** Forgets the original position and keeps the current picked position */
	public void finishPicking() {
		isPicking = false;
		pick.displayString = I18n.format("betterHud.menu.pick");
	}

	@Override
	public void set(Point value) {
		x = value.getX();
		y = value.getY();
		updateText();
	}

	@Override
	public Point get() {
		return new Point(x, y);
	}

	@Override
	public String save() {
		return get().toString();
	}

	@Override
	public void load(String val) {
		set(Point.fromString(val));
	}

	@Override
	public void updateGuiParts(Collection<Setting<?>> settings) {
		boolean enabled = enabled();
		xBox.setEnabled(enabled);
		yBox.setEnabled(enabled);

		pick.enabled = enabled;

		if(enabled) {
			try {
				x = Integer.parseInt(xBox.getText());
				xUp.enabled = xDown.enabled = true;
			} catch(NumberFormatException e) {
				x = 0;
				xUp.enabled = xDown.enabled = false;
			}

			try {
				y = Integer.parseInt(yBox.getText());
				yUp.enabled = yDown.enabled = true;
			} catch(NumberFormatException e) {
				y = 0;
				yUp.enabled = yDown.enabled = false;
			}
		} else {
			xUp.enabled = xDown.enabled = yUp.enabled = yDown.enabled = false;
		}
	}

	public Point getAbsolute() {
		return get();
	}
}
