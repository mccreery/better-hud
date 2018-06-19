package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import net.minecraft.client.gui.Gui;
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
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class Compass extends HudElement {
	private static final String[] DIRECTIONS = {"S", "E", "N", "W"};

	private final SettingPosition position = new SettingPosition("position", Direction.TOP | Direction.CORNERS, Direction.getFlags(Direction.NORTH, Direction.SOUTH));
	private final SettingSlider directionScaling = new SettingPercentage("letterScale", 0.01);
	private final SettingBoolean showNotches = new SettingBoolean("showNotches").setUnlocalizedValue(SettingBoolean.VISIBLE);
	private final SettingChoose requireItem = new SettingChoose("requireItem", "disabled", "inventory", "hand");

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.set(Direction.NORTH);
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

		if(showNotches.get()) {
			int baseX = bounds.getX();

			int x;
			int y = bounds.getY() - 2;

			for(int loc : notchX) {
				x = baseX + loc;
				Gui.drawRect(x, y, x + 1, y + 6, Colors.WHITE);
				x = baseX + 180 - loc;
				Gui.drawRect(x - 1, y, x, y + 6, Colors.WHITE);
			}
		}

		Bounds notches = bounds.withPadding(0, 3, 0, 0);
		Bounds largeNotch = new Bounds(1, 7);

		GlUtil.drawRect(Direction.NORTH_WEST.anchor(largeNotch, notches), Colors.RED);
		GlUtil.drawRect(Direction.NORTH.anchor(largeNotch, notches), Colors.RED);
		GlUtil.drawRect(Direction.NORTH_EAST.anchor(largeNotch, notches), Colors.RED);
	}

	private void drawDirections(Bounds bounds) {
		GlStateManager.enableBlend();
		float angle = (float)Math.toRadians(MC.player.rotationYaw);

		Point origin = Direction.NORTH.getAnchor(bounds).add(0, 2);

		float radius = bounds.getWidth() / 2 + SPACER;

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
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, DIRECTIONS[i], 0, 0, color);
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

	// TODO implement alignment

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
