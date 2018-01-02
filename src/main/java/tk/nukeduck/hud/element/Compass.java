package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;

public class Compass extends HudElement {
	private final SettingPosition position = new SettingPosition("position");
	private final SettingSlider directionScaling = new SettingSlider("scaledDirections", 0, 100, 1).setUnlocalizedValue("betterHud.strings.percent");
	private final SettingBoolean showNotches = new SettingBoolean("showNotches");
	private final SettingSlider scale = new SettingSlider("scale", 25, 200, 1).setUnlocalizedValue("betterHud.strings.percent");

	@Override
	public void loadDefaults() {
		this.settings.set(true);
		position.load(Direction.NORTH_WEST);
		directionScaling.value = 50.0;
		showNotches.set(true);
		scale.value = 100;
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
		settings.add(scale);
		settings.add(showNotches);
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
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

		Bounds bounds = position.applyTo(new Bounds(180, 12), manager);
		
		// TODO scale
		/*GL11.glPushMatrix();
		GL11.glTranslatef(x + 90, y, 0);
		GL11.glScaled(scale.value / 100, scale.value / 100, 1);
		GL11.glTranslatef(-x - 90, -y, 0);*/

		drawRect(bounds, Colors.fromARGB(170,  0,  0,  0));
		Gui.drawRect(bounds.x() + 50, bounds.y(), bounds.x() + 130, bounds.y() + 12, Colors.fromARGB( 85, 85, 85, 85));

		glEnable(GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		MC.mcProfiler.startSection("text");

		int maxScale = 4;
		float factor = 100 / maxScale;
		
		// TODO repeated code
		if(nOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(bounds.x() + 90 - nX, bounds.y() + 2, 0.0F);
				
				float size = (float) nOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);
				
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, "N", 0, 0, Colors.fromARGB(nOpacity, 255, 0, 0));
			}
			glPopMatrix();
		}
		if(eOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(bounds.x() + 90 - eX, bounds.y() + 2, 0.0F);

				float size = (float) eOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);
				
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, "E", 0, 0, Colors.fromARGB(eOpacity, 255, 255, 255));
			}
			glPopMatrix();
		}
		if(sOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(bounds.x() + 90 - sX, bounds.y() + 2, 0.0F);
				
				float size = (float) sOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);
				
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, "S", 0, 0, Colors.fromARGB(sOpacity, 0, 0, 255));
			}
			glPopMatrix();
		}
		if(wOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(bounds.x() + 90 - wX, bounds.y() + 2, 0.0F);

				float size = (float) wOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);

				MC.ingameGUI.drawCenteredString(MC.fontRenderer, "W", 0, 0, Colors.fromARGB(wOpacity, 255, 255, 255));
			}
			glPopMatrix();
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
