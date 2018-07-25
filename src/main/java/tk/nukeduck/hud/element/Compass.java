package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPercentage;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class Compass extends HudElement {
	private static final String[] DIRECTIONS = {"S", "E", "N", "W"};

	private final SettingPosition position = new SettingPosition("position", Options.TOP_BOTTOM, Options.NORTH_SOUTH);
	private final SettingSlider directionScaling = new SettingPercentage("letterScale");
	private final SettingBoolean showNotches = new SettingBoolean("showNotches").setUnlocalizedValue(SettingBoolean.VISIBLE);
	private final SettingChoose requireItem = new SettingChoose("requireItem", "disabled", "inventory", "hand");

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH);
		directionScaling.set(0.5);
		showNotches.set(true);
		requireItem.setIndex(0);
		settings.priority.set(-3);
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
		settings.add(requireItem);
	}

	private void drawBackground(Bounds bounds) {
		GlUtil.drawRect(bounds, Colors.fromARGB(170, 0, 0, 0));
		GlUtil.drawRect(bounds.withInset(50, 0, 50, 0), Colors.fromARGB(85, 85, 85, 85));

		Direction alignment = position.getContentAlignment();

		Bounds smallBounds = bounds.withPadding(2);
		Bounds largeNotch = new Bounds(1, 7);

		Bounds smallNotch = new Bounds(1, 6);
		Bounds largeBounds = bounds.withPadding(3);

		if(showNotches.get()) {
			for(int loc : notchX) {
				GlUtil.drawRect(smallNotch.anchoredTo(smallBounds, alignment).addPosition(loc, 0), Colors.WHITE);
				GlUtil.drawRect(smallNotch.anchoredTo(smallBounds, alignment).subPosition(loc, 0), Colors.WHITE);
			}
		}

		GlUtil.drawRect(largeNotch.anchoredTo(largeBounds, alignment.withColumn(0)), Colors.RED);
		GlUtil.drawRect(largeNotch.anchoredTo(largeBounds, alignment.withColumn(1)), Colors.RED);
		GlUtil.drawRect(largeNotch.anchoredTo(largeBounds, alignment.withColumn(2)), Colors.RED);
	}

	private void drawDirections(Bounds bounds) {
		GlStateManager.enableBlend();
		float angle = (float)Math.toRadians(MC.player.rotationYaw);

		float radius = bounds.getWidth() / 2 + SPACER;
		boolean bottom = position.getContentAlignment() == Direction.SOUTH;

		Point origin = bounds.withInset(2).getAnchor(position.getContentAlignment());

		for(int i = 0; i < 4; i++, angle += Math.PI / 2) {
			double cos = Math.cos(angle);

			Point letter = origin.add(-(int)(Math.sin(angle) * radius), 0);
			double scale = 1 + directionScaling.get() * cos * 2;

			GlStateManager.pushMatrix();

			GlStateManager.translate(letter.getX(), letter.getY(), 0);
			GlUtil.scale((float)scale);

			int color = i == 0 ? Colors.BLUE : i == 2 ? Colors.RED : Colors.WHITE;
			color = Colors.setAlpha(color, (int)(((cos + 1) / 2) * 255));

			// Super low alphas can render opaque for some reason
			if(Colors.alpha(color) > 3) {
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, DIRECTIONS[i], 0, bottom ? -MC.fontRenderer.FONT_HEIGHT : 0, color);
			}

			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldRender(Event event) {
		if(!super.shouldRender(event)) return false;

		switch(requireItem.getIndex()) {
			case 1:
				return MC.player.inventory.hasItemStack(new ItemStack(Items.COMPASS));
			case 2:
				return MC.player.getHeldItemMainhand().getItem() == Items.COMPASS
					|| MC.player.getHeldItemOffhand().getItem() == Items.COMPASS;
		}
		return true;
	}

	@Override
	public Bounds render(Event event) {
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
