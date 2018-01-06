package tk.nukeduck.hud.element;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;

public abstract class ElementStub extends HudElement {
	protected ElementStub(String name) {
		super(name);
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		return null;
	}

	@Override
	public boolean shouldRender() {
		return false;
	}
}
