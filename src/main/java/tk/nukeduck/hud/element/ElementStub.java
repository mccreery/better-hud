package tk.nukeduck.hud.element;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.util.Bounds;

public abstract class ElementStub extends HudElement {
	protected ElementStub(String name) {
		super(name);
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event) {
		return null;
	}

	@Override
	public boolean shouldRender() {
		return false;
	}
}
