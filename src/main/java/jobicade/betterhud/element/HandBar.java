package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;

import java.util.List;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class HandBar extends EquipmentDisplay {
	private SettingBoolean showItem, offHand, showBars, showNonTools;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.SOUTH);
		showItem.set(true);
		showBars.set(true);
		offHand.set(false);
		settings.priority.set(100);
	}

	public HandBar() {
		super("handBar", new SettingPosition(DirectionOptions.BAR, DirectionOptions.NORTH_SOUTH));
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(new Legend("misc"));
		settings.add(showItem = new SettingBoolean("showItem").setValuePrefix(SettingBoolean.VISIBLE));
		settings.add(showBars = new SettingBoolean("bars"));
		settings.add(offHand = new SettingBoolean("offhand"));
		settings.add(showNonTools = new SettingBoolean("showNonTools").setValuePrefix("betterHud.value.nonTools"));
	}

	public void renderBar(ItemStack stack, int x, int y) {
		boolean isTool = stack.isDamageable();
		if(stack == null || !showNonTools.get() && !isTool) return;

		String text = getText(stack);

		int width = 0;
		if(showItem.get()) width += 21;

		if(text != null) {
			width += MC.fontRenderer.getStringWidth(text);
		}

		if(showItem.get()) {
			MC.profiler.startSection("items");
			GlUtil.renderSingleItem(stack, x + 90 - width / 2, y);
			MC.profiler.endSection();
		}

		if(text != null) {
			MC.profiler.startSection("text");
			GlUtil.drawString(text, new Point(x + 90 - width / 2 + (showItem.get() ? 21 : 0), y + 4), Direction.NORTH_WEST, Color.WHITE);
			MC.profiler.endSection();
		}

		if(isTool && showBars.get()) {
			MC.profiler.startSection("bars");
			GlUtil.drawDamageBar(new Rect(x, y + 16, 180, 2), stack, false);
			MC.profiler.endSection();
		}
	}

	@Override
	public Rect render(Event event) {
		Rect bounds = position.applyTo(new Rect(180, offHand.get() ? 41 : 18));
		renderBar(MC.player.getHeldItemMainhand(), bounds.getX(), bounds.getBottom() - 18);

		if(offHand.get()) {
			renderBar(MC.player.getHeldItemOffhand(), bounds.getX(), bounds.getY());
		}
		return bounds;
	}
}
