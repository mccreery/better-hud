package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.util.render.Color;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.GlUtil;

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
		boolean isTool = stack.isItemStackDamageable();
		if(stack == null || !showNonTools.get() && !isTool) return;

		String text = getText(stack);

		int width = 0;
		if(showItem.get()) width += 21;

		if(text != null) {
			width += MC.fontRenderer.getStringWidth(text);
		}

		if(showItem.get()) {
			MC.mcProfiler.startSection("items");
			GlUtil.renderSingleItem(stack, x + 90 - width / 2, y);
			MC.mcProfiler.endSection();
		}

		if(text != null) {
			MC.mcProfiler.startSection("text");
			MC.ingameGUI.drawString(MC.fontRenderer, text, x + 90 - width / 2 + (showItem.get() ? 21 : 0), y + 4, Color.WHITE.getPacked());
			MC.mcProfiler.endSection();
		}

		if(isTool && showBars.get()) {
			MC.mcProfiler.startSection("bars");
			GlUtil.drawDamageBar(new Rect(x, y + 16, 180, 2), stack, false);
			MC.mcProfiler.endSection();
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
