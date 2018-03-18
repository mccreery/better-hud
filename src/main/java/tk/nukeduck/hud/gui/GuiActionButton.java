package tk.nukeduck.hud.gui;

import net.minecraft.client.gui.GuiButton;
import tk.nukeduck.hud.util.Bounds;

public abstract class GuiActionButton extends GuiButton implements ActionCallback {
	public GuiActionButton(String buttonText) {
		super(0, 0, 0, buttonText);
	}

	public final GuiActionButton setId(int id) {
		this.id = id;
		return this;
	}

	public final GuiActionButton setBounds(Bounds bounds) {
		this.x = bounds.x();
		this.y = bounds.y();
		this.width = bounds.width();
		this.height = bounds.height();

		return this;
	}

	public final Bounds getBounds() {
		return new Bounds(x, y, width, height);
	}

	public static class GuiCallbackButton extends GuiActionButton {
		private final ActionCallback callback;

		public GuiCallbackButton(String buttonText, ActionCallback callback) {
			super(buttonText);
			this.callback = callback;
		}

		@Override
		public void actionPerformed() {
			callback.actionPerformed();
		}
	}
}
