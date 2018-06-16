package tk.nukeduck.hud.util;

public interface Renderable {
	public boolean shouldRender();
	public void render(float partialTicks);
}
