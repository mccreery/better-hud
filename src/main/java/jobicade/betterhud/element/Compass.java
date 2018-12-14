package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.List;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingDirection;
import jobicade.betterhud.element.settings.SettingPercentage;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Compass extends HudElement {
	private static final String[] DIRECTIONS = { "S", "E", "N", "W" };

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
		super("compass", new SettingPosition(DirectionOptions.TOP_BOTTOM, DirectionOptions.NORTH_SOUTH));
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(mode = new SettingChoose("mode", "visual", "text"));
		settings.add(new Legend("misc"));
		settings.add(directionScaling = new SettingPercentage("letterScale"));
		settings.add(showNotches = new SettingBoolean("showNotches").setValuePrefix(SettingBoolean.VISIBLE));
		settings.add(requireItem = new SettingChoose("requireItem", "disabled", "inventory", "hand"));
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

			Color color = i == 0 ? Color.BLUE : i == 2 ? Color.RED : Color.WHITE;
			color = color.withAlpha((int)(((cos + 1) / 2) * 255));

			// Super low alphas can render opaque for some reason
			if(color.getAlpha() > 3) {
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, DIRECTIONS[i], 0, bottom ? -MC.fontRenderer.FONT_HEIGHT : 0, color.getPacked());
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
		return I18n.format("betterHud.hud.facing", SettingDirection.localizeDirection(direction), coord);
	}

	@Override
	public Rect render(Event event) {
		GlUtil.enableBlendTranslucent();
		Rect bounds;

		if(mode.getIndex() == 0) {
			bounds = position.applyTo(new Rect(180, 12));

			MC.mcProfiler.startSection("background");
			drawBackground(bounds);
			MC.mcProfiler.endStartSection("text");
			drawDirections(bounds);
			MC.mcProfiler.endSection();
		} else {
			String text = getText();
			bounds = position.applyTo(new Rect(GlUtil.getStringSize(text)));

			MC.mcProfiler.startSection("text");
			GlUtil.drawString(text, bounds.getPosition(), Direction.NORTH_WEST, Color.WHITE);
			MC.mcProfiler.endSection();
		}

		return bounds;
	}
}
