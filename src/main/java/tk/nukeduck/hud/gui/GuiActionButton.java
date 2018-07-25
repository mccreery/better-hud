package tk.nukeduck.hud.gui;

import net.minecraft.client.gui.GuiButton;
import tk.nukeduck.hud.util.Bounds;

public class GuiActionButton extends GuiButton implements ActionCallback {
	private ActionCallback callback;
	private boolean repeat;

	public GuiActionButton(String buttonText) {
		super(0, 0, 0, buttonText);
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
		callback.actionPerformed();
	}
}
