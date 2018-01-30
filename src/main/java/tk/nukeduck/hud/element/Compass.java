package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPercentage;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;

public class Compass extends HudElement {
	private final SettingPosition position = new SettingPosition("position");
	private final SettingSlider directionScaling = new SettingPercentage("letterScale", 0.01);
	private final SettingBoolean showNotches = new SettingBoolean("showNotches").setUnlocalizedValue(SettingBoolean.VISIBLE);
	//private final SettingSlider scale = new SettingPercentage("scale", 25, 200, 0.01);

	@Override
	public void loadDefaults() {
		this.settings.set(true);
		position.set(Direction.NORTH_WEST);
		directionScaling.set(50.0);
		showNotches.set(true);
		//scale.set(100.0);
	}

	public static final double degreesPerRadian = 180.0 / Math.PI;
	private int[] notchX = new int[9];

	public Compass() {
		super("compass");

		int x = 0;
		for(double i = 0.1; i < 0.9; i += 0.1) {
			notchX[x] = (int) (Math.asin(i) / Math.PI * 180);
			x++;
		}

		settings.add(position);
		settings.add(new Legend("misc"));
		settings.add(directionScaling);
		//settings.add(scale);
		settings.add(showNotches);
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event) {
		double offsetQuarter = Math.toRadians(90);
		double transform = Math.toRadians(MC.player.rotationYaw);

		short nOpacity = (short) Math.abs(Math.sin(transform / 2) * 255);
		short wOpacity = (short) Math.abs(Math.sin((transform + offsetQuarter) / 2) * 255);
		short sOpacity = (short) Math.abs(Math.sin((transform + offsetQuarter * 2) / 2) * 255);
		short eOpacity = (short) Math.abs(Math.sin((transform - offsetQuarter) / 2) * 255);

		int nX = (int) (Math.sin(transform + offsetQuarter * 2) * 100);
		int eX = (int) (Math.sin(transform + offsetQuarter) * 100);
		int sX = (int) (Math.sin(transform) * 100);
		int wX = (int) (Math.sin(transform - offsetQuarter) * 100);

		Bounds bounds = position.applyTo(new Bounds(180, 12));

		// TODO scale
		/*GL11.glPushMatrix();
		GL11.glTranslatef(x + 90, y, 0);
		GL11.glScaled(scale.value / 100, scale.value / 100, 1);
		GL11.glTranslatef(-x - 90, -y, 0);*/

		drawRect(bounds, Colors.fromARGB(170,  0,  0,  0));
		Gui.drawRect(bounds.x() + 50, bounds.y(), bounds.x() + 130, bounds.y() + 12, Colors.fromARGB( 85, 85, 85, 85));

		GlUtil.enableBlendTranslucent();

		MC.mcProfiler.startSection("text");

		int maxScale = 4;
		float factor = 100 / maxScale;
		
		// TODO repeated code
		if(nOpacity > 10) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(bounds.x() + 90 - nX, bounds.y() + 2, 0);
			
			float size = (float) nOpacity / 128F;
			float finalSize = Math.max(0, (float) (directionScaling.get() / factor) * (size - 1) + 1);
			GlStateManager.scale(finalSize, finalSize, 1);
			
			MC.ingameGUI.drawCenteredString(MC.fontRenderer, "N", 0, 0, Colors.fromARGB(nOpacity, 255, 0, 0));

			GlStateManager.popMatrix();
		}
		if(eOpacity > 10) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(bounds.x() + 90 - eX, bounds.y() + 2, 0.0F);

			float size = (float) eOpacity / 128F;
			float finalSize = Math.max(0, (float) (directionScaling.get() / factor) * (size - 1) + 1);
			GlStateManager.scale(finalSize, finalSize, 1.0F);

			MC.ingameGUI.drawCenteredString(MC.fontRenderer, "E", 0, 0, Colors.fromARGB(eOpacity, 255, 255, 255));
			GlStateManager.popMatrix();
		}
		if(sOpacity > 10) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(bounds.x() + 90 - sX, bounds.y() + 2, 0.0F);
			
			float size = (float) sOpacity / 128F;
			float finalSize = Math.max(0, (float) (directionScaling.get() / factor) * (size - 1) + 1);
			GlStateManager.scale(finalSize, finalSize, 1.0F);
			
			MC.ingameGUI.drawCenteredString(MC.fontRenderer, "S", 0, 0, Colors.fromARGB(sOpacity, 0, 0, 255));
			GlStateManager.popMatrix();
		}
		if(wOpacity > 10) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(bounds.x() + 90 - wX, bounds.y() + 2, 0.0F);

			float size = (float) wOpacity / 128F;
			float finalSize = Math.max(0, (float) (directionScaling.get() / factor) * (size - 1) + 1);
			GlStateManager.scale(finalSize, finalSize, 1.0F);

			MC.ingameGUI.drawCenteredString(MC.fontRenderer, "W", 0, 0, Colors.fromARGB(wOpacity, 255, 255, 255));
			GlStateManager.popMatrix();
		}
		MC.mcProfiler.endSection();
		
		MC.mcProfiler.startSection("notches");

		int largeNotch = Colors.WHITE;
		if(showNotches.get()) {
			largeNotch = Colors.RED;

			for(int loc : notchX) {
				Gui.drawRect(bounds.x() + loc - 1, bounds.y() - 2, bounds.x() + loc, bounds.y()+4, Colors.WHITE);
				Gui.drawRect(bounds.x() - loc + 180, bounds.y() - 2, bounds.x()-loc+181, bounds.y()+4, Colors.WHITE);
			}
		}

		Gui.drawRect(bounds.x()+89,  bounds.y()-3, bounds.x()+90, bounds.y()+4, largeNotch);
		Gui.drawRect(bounds.x(),     bounds.y()-3, bounds.x()+1, bounds.y()+4, largeNotch);
		Gui.drawRect(bounds.x()+179, bounds.y()-3, bounds.x()+180, bounds.y()+4, largeNotch);

		MC.mcProfiler.endSection();
		//GL11.glPopMatrix();

		return bounds;
	}
}
