package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.SPACER;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingDirection;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class Compass extends OverlayElement {
	private static final String[] DIRECTIONS = { "S", "E", "N", "W" };

	private SettingPosition position;
	private SettingChoose mode, requireItem;
	private SettingSlider directionScaling;
	private SettingBoolean showNotches;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH);
		directionScaling.set(0.5);
		showNotches.set(true);
		requireItem.setIndex(0);
		settings.setPriority(-3);
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

		settings.addChildren(
			position = new SettingPosition(DirectionOptions.TOP_BOTTOM, DirectionOptions.NORTH_SOUTH),
			mode = new SettingChoose("mode", "visual", "text"),
			new Legend("misc"),
			directionScaling = new SettingSlider("letterScale", 0, 1).setDisplayPercent(),
			showNotches = new SettingBoolean("showNotches").setValuePrefix(SettingBoolean.VISIBLE),
			requireItem = new SettingChoose("requireItem", "disabled", "inventory", "hand")
		);
	}

	private void drawBackground(Rect bounds) {
		GlUtil.drawRect(bounds, new Color(170, 0, 0, 0));
		GlUtil.drawRect(bounds.grow(-50, 0, -50, 0), new Color(85, 85, 85, 85));

		Direction alignment = position.getContentAlignment();

		Rect smallRect = bounds.grow(2);
		Rect largeNotch = new Rect(1, 7);

		Rect smallNotch = new Rect(1, 6);
		Rect largeRect = bounds.grow(0, 3, 0, 3);

		if(showNotches.get()) {
			for(int loc : notchX) {
				Rect notchTemp = smallNotch.anchor(smallRect, alignment);
				GlUtil.drawRect(notchTemp.translate(loc, 0), Color.WHITE);
				GlUtil.drawRect(notchTemp.translate(-loc, 0), Color.WHITE);
			}
		}

		GlUtil.drawRect(largeNotch.anchor(largeRect, alignment.withCol(0)), Color.RED);
		GlUtil.drawRect(largeNotch.anchor(largeRect, alignment.withCol(1)), Color.RED);
		GlUtil.drawRect(largeNotch.anchor(largeRect, alignment.withCol(2)), Color.RED);
	}

	private void drawDirections(Rect bounds) {
		float angle = (float)Math.toRadians(Minecraft.getMinecraft().player.rotationYaw);

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

			Color color = i == 0 ? Color.BLUE : i == 2 ? Color.RED : Color.WHITE;
			color = color.withAlpha((int)(((cos + 1) / 2) * 255));

			// Super low alphas can render opaque for some reason
			if(color.getAlpha() > 3) {
				GlUtil.drawString(DIRECTIONS[i], Point.zero(), bottom ? Direction.SOUTH : Direction.NORTH, color);
			}

			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldRender(OverlayContext context) {
		switch(requireItem.getIndex()) {
			case 1:
				return Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(Items.COMPASS));
			case 2:
				return Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.COMPASS
					|| Minecraft.getMinecraft().player.getHeldItemOffhand().getItem() == Items.COMPASS;
		}
		return true;
	}

	public String getText() {
		EnumFacing enumfacing = Minecraft.getMinecraft().player.getHorizontalFacing();

		String coord;
		Direction direction;

		switch(enumfacing) {
			case NORTH: coord = "-Z"; direction = Direction.NORTH; break;
			case SOUTH: coord = "+Z"; direction = Direction.SOUTH; break;
			case WEST: coord = "-X"; direction = Direction.WEST; break;
			case EAST: coord = "+X"; direction = Direction.EAST; break;
			default: return "?";
		}
		return I18n.format("betterHud.hud.facing", SettingDirection.localizeDirection(direction), coord);
	}

	@Override
	public Rect render(OverlayContext context) {
		Rect bounds;

		if(mode.getIndex() == 0) {
			bounds = position.applyTo(new Rect(180, 12));

			Minecraft.getMinecraft().mcProfiler.startSection("background");
			drawBackground(bounds);
			Minecraft.getMinecraft().mcProfiler.endStartSection("text");
			drawDirections(bounds);
			Minecraft.getMinecraft().mcProfiler.endSection();
		} else {
			String text = getText();
			bounds = position.applyTo(new Rect(GlUtil.getStringSize(text)));

			Minecraft.getMinecraft().mcProfiler.startSection("text");
			GlUtil.drawString(text, bounds.getPosition(), Direction.NORTH_WEST, Color.WHITE);
			Minecraft.getMinecraft().mcProfiler.endSection();
		}

		return bounds;
	}
}
