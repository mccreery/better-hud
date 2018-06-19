package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;

public class HandBar extends EquipmentDisplay {
	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS | Direction.SOUTH.getFlag(), Direction.TOP | Direction.BOTTOM);
	private final SettingBoolean showItem = new SettingBoolean("showItem").setUnlocalizedValue(SettingBoolean.VISIBLE);
	private final SettingBoolean offHand = new SettingBoolean("offhand");
	private final SettingBoolean showBars = new SettingBoolean("bars");

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.set(Direction.SOUTH);
		showItem.set(true);
		showBars.set(true);
		offHand.set(false);
		settings.priority.set(100);
	}

	public HandBar() {
		super("handBar");

		settings.add(position);
		settings.add(new Legend("misc"));
		settings.add(showItem);
		settings.add(showBars);
		settings.add(offHand);
	}

	public void renderBar(ItemStack stack, int x, int y) {
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
			MC.ingameGUI.drawString(MC.fontRenderer, text, x + 90 - width / 2 + (showItem.get() ? 21 : 0), y + 4, Colors.WHITE);
			MC.mcProfiler.endSection();
		}

		if(showBars.get()) {
			MC.mcProfiler.startSection("bars");
			GlUtil.drawDamageBar(new Bounds(x, y + 16, 180, 2), stack, false);
			MC.mcProfiler.endSection();
		}
	}

	@Override
	public Bounds render(Event event) {
		Bounds bounds = position.applyTo(new Bounds(180, offHand.get() ? 41 : 18));
		ItemStack stack = MC.player.getHeldItemMainhand();

		if(stack != null && stack.getMaxDamage() > 0) {
			renderBar(stack, bounds.getX(), bounds.getBottom() - 18);
		}

		if(offHand.get()) {
			stack = MC.player.getHeldItemOffhand();

			if(stack != null && stack.getMaxDamage() > 0) {
				renderBar(stack, bounds.getX(), bounds.getY());
			}
		}
		return bounds;
	}
}
