package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
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

	private final SettingChoose mode = new SettingChoose("mode", "visual", "text");
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
		super("compass", new SettingPosition(Direction.Options.TOP_BOTTOM, Direction.Options.NORTH_SOUTH));

		settings.add(mode);
		settings.add(new Legend("misc"));
		settings.add(directionScaling);
		settings.add(showNotches);
		settings.add(requireItem);
	}

	private void drawBackground(Bounds bounds) {
		GlUtil.drawRect(bounds, Colors.fromARGB(170, 0, 0, 0));
		GlUtil.drawRect(bounds.grow(-50, 0, -50, 0), Colors.fromARGB(85, 85, 85, 85));

		Direction alignment = position.getContentAlignment();

		Bounds smallBounds = bounds.grow(2);
		Bounds largeNotch = new Bounds(1, 7);

		Bounds smallNotch = new Bounds(1, 6);
		Bounds largeBounds = bounds.grow(0, 3, 0, 3);

		if(showNotches.get()) {
			for(int loc : notchX) {
				Bounds notchTemp = smallNotch.anchor(smallBounds, alignment);
				GlUtil.drawRect(notchTemp.translate(loc, 0), Colors.WHITE);
				GlUtil.drawRect(notchTemp.translate(-loc, 0), Colors.WHITE);
			}
		}

		GlUtil.drawRect(largeNotch.anchor(largeBounds, alignment.withColumn(0)), Colors.RED);
		GlUtil.drawRect(largeNotch.anchor(largeBounds, alignment.withColumn(1)), Colors.RED);
		GlUtil.drawRect(largeNotch.anchor(largeBounds, alignment.withColumn(2)), Colors.RED);
	}

	private void drawDirections(Bounds bounds) {
		float angle = (float)Math.toRadians(MC.player.rotationYaw);

		float radius = bounds.getWidth() / 2 + SPACER;
		boolean bottom = position.getContentAlignment() == Direction.SOUTH;

		Point origin = bounds.grow(-2).getAnchor(position.getContentAlignment());

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

	public String getText() {
		EnumFacing enumfacing = MC.player.getHorizontalFacing();

		String coord;
		Direction direction;

		switch(enumfacing) {
			case NORTH: coord = "-Z"; direction = Direction.NORTH; break;
			case SOUTH: coord = "+Z"; direction = Direction.SOUTH; break;
			case WEST: coord = "-X"; direction = Direction.WEST; break;
			case EAST: coord = "+X"; direction = Direction.EAST; break;
			default: return "?";
		}
		return I18n.format("betterHud.hud.facing", direction.getLocalizedName(), coord);
	}

	@Override
	public Bounds render(Event event) {
		GlUtil.enableBlendTranslucent();
		Bounds bounds;

		if(mode.getIndex() == 0) {
			bounds = position.applyTo(new Bounds(180, 12));

			MC.mcProfiler.startSection("background");
			drawBackground(bounds);
			MC.mcProfiler.endStartSection("text");
			drawDirections(bounds);
			MC.mcProfiler.endSection();
		} else {
			String text = getText();
			bounds = position.applyTo(new Bounds(GlUtil.getStringSize(text)));

			MC.mcProfiler.startSection("text");
			GlUtil.drawString(text, bounds.getPosition(), Direction.NORTH_WEST, Colors.WHITE);
			MC.mcProfiler.endSection();
		}

		return bounds;
	}
}
