package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPercentage;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPositionAligned;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class Compass extends HudElement {
	private static final String[] DIRECTIONS = {"S", "E", "N", "W"};

	private final SettingPosition position = new SettingPositionAligned("position", Direction.TOP | Direction.BOTTOM, Direction.flags(Direction.NORTH, Direction.SOUTH));
	private final SettingSlider directionScaling = new SettingPercentage("letterScale", 0.01);
	private final SettingBoolean showNotches = new SettingBoolean("showNotches").setUnlocalizedValue(SettingBoolean.VISIBLE);

	@Override
	public void loadDefaults() {
		this.settings.set(true);
		position.set(Direction.NORTH_WEST);
		directionScaling.set(50.0);
		showNotches.set(true);
	}

	private static final int[] notchX = new int[9];

	static {
		int x = 0;

		for(double i = 0.1; i <= 0.9; i += 0.1, x++) {
			notchX[x] = (int) (Math.asin(i) / Math.PI * 180);
		}
	}

	public Compass() {
		super("compass");

		settings.add(position);
		settings.add(new Legend("misc"));
		settings.add(directionScaling);
		settings.add(showNotches);
	}

	private void drawBackground(Bounds bounds) {
		GlUtil.drawRect(bounds, Colors.fromARGB(170, 0, 0, 0));
		GlUtil.drawRect(bounds.inset(50, 0, 50, 0), Colors.fromARGB(85, 85, 85, 85));

		if(showNotches.get()) {
			Bounds notch = new Bounds(0, bounds.y() - 2, 1, 6);

			for(int loc : notchX) {
				notch.x(bounds.x() + loc - 1);
				GlUtil.drawRect(notch, Colors.WHITE);
				notch.x(bounds.x() - loc + 180);
				GlUtil.drawRect(notch, Colors.WHITE);
			}
		}

		Bounds notches = bounds.pad(0, 3, 0, 0);
		Bounds largeNotch = new Bounds(1, 7);

		GlUtil.drawRect(Direction.NORTH_WEST.anchor(largeNotch, notches), Colors.RED);
		GlUtil.drawRect(Direction.NORTH     .anchor(largeNotch, notches), Colors.RED);
		GlUtil.drawRect(Direction.NORTH_EAST.anchor(largeNotch, notches), Colors.RED);
	}

	private void drawDirections(Bounds bounds) {
		float angle = (float)Math.toRadians(MC.player.rotationYaw);

		Point origin = Direction.NORTH.getAnchor(bounds);
		origin.y += 2;

		float radius = bounds.width() / 2 + SPACER;

		for(int i = 0; i < 4; i++, angle += Math.PI / 2) {
			double cos = Math.cos(angle);

			Point letter = origin.add(-(int)(Math.sin(angle) * radius), 0);

			double scale = cos + 1;
			scale *= directionScaling.get() * 2;

			GlStateManager.pushMatrix();

			GlStateManager.translate(letter.x, letter.y, 0);
			GlUtil.scale((float)scale);

			int color = i == 0 ? Colors.BLUE : i == 2 ? Colors.RED : Colors.WHITE;
			color = Colors.setAlpha(color, (int)(((cos + 1) / 2) * 255));

			// Super low alphas can render opaque for some reason
			if(Colors.alpha(color) > 3) {
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, DIRECTIONS[i], 0, 0, color);
			}

			GlStateManager.popMatrix();
		}
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event) {
		GlUtil.enableBlendTranslucent();
		Bounds bounds = position.applyTo(new Bounds(180, 12));

		MC.mcProfiler.startSection("background");
		drawBackground(bounds);
		MC.mcProfiler.endStartSection("text");
		drawDirections(bounds);
		MC.mcProfiler.endSection();

		return bounds;
	}
}
