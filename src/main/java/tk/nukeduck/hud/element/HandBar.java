package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingWarnings;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.RenderUtil;

public class HandBar extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS | Direction.SOUTH.flag());

	private final SettingBoolean showName = new SettingBoolean("showName");
	private final SettingBoolean showDurability = new SettingBoolean("showDurability", Direction.WEST);
	private final SettingChoose durabilityMode = new SettingChoose("durabilityMode", Direction.EAST, "values", "percent");
	private final SettingBoolean showItem = new SettingBoolean("showItem");
	private final SettingBoolean offHand = new SettingBoolean("offHand");
	private final SettingBoolean showBars = new SettingBoolean("showBars");
	private final SettingWarnings warnings = new SettingWarnings("damageWarning");

	@Override
	public void loadDefaults() {
		this.settings.set(true);
		position.set(Direction.SOUTH);

		showName.set(true);
		showDurability.set(true);
		durabilityMode.setIndex(0);
		showItem.set(true);
		showBars.set(true);
		offHand.set(false);
		warnings.set(new Integer[] {45, 25, 10});
		warnings.setActive(true);
	}

	public HandBar() {
		super("handBar");

		settings.add(position);
		settings.add(new Legend("misc"));
		settings.add(showItem);
		settings.add(showBars);
		settings.add(showDurability);
		settings.add(durabilityMode);
		settings.add(showName);
		settings.add(offHand);
		settings.add(warnings);
	}

	private String generateText(ItemStack stack) {
		int maxDamage = stack.getMaxDamage();
		float value = (float) (maxDamage - stack.getItemDamage()) / (float) maxDamage;

		String dur, text;
		if(durabilityMode.getIndex() == 1) {
			dur = I18n.format("betterHud.strings.percent", FormatUtil.formatToPlaces(value * 100.0, 1));
		} else {
			dur = I18n.format("betterHud.strings.outOf", String.valueOf(maxDamage - stack.getItemDamage()), String.valueOf(maxDamage));
		}

		if(showName.get() && showDurability.get()) {
			text = FormatUtil.separate(I18n.format("betterHud.strings.splitter"), stack.getDisplayName(), dur);
		} else if(showName.get()) {
			text =  stack.getDisplayName();
		} else if(showDurability.get()) {
			text = dur;
		} else {
			text = "";
		}

		int count = warnings.getWarning(value);

		if(count > 0) {
			text += " " + I18n.format("betterHud.strings.damaged." + count);
		}
		return text;
	}
	
	public void renderBar(ItemStack stack, int x, int y) {
		//byte green = (byte) (255 * value);
		//byte red = (byte) (256 - green);

		String text = generateText(stack);

		int totalWidth = MC.fontRenderer.getStringWidth(text);
		if(showItem.get()) totalWidth += 21;

		if(showItem.get()) {
			MC.mcProfiler.startSection("items");
			MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderUtil.renderItem(MC.getRenderItem(), MC.fontRenderer, MC.getTextureManager(), stack, x + 90 - totalWidth / 2, y);
			MC.mcProfiler.endSection();
		}

		MC.mcProfiler.startSection("text");
		MC.ingameGUI.drawString(MC.fontRenderer, text, x + 90 - totalWidth / 2 + (showItem.get() ? 21 : 0), y + 4, Colors.WHITE);
		MC.mcProfiler.endSection();

		if(showBars.get()) {
			MC.mcProfiler.startSection("bars");
			HudElement.drawDamageBar(new Bounds(x, y + 16, 180, 2), stack, false);
			MC.mcProfiler.endSection();
		}
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		Bounds bounds = new Bounds(180, offHand.get() ? 41 : 18);

		if(position.getDirection() == Direction.SOUTH) {
			bounds.position = new Point(manager.getResolution().x / 2, manager.getResolution().y - 64);
			Direction.SOUTH.align(bounds);
		} else {
			position.applyTo(bounds, manager);
		}

		ItemStack stack = MC.player.getHeldItemMainhand();

		if(stack != null && stack.getMaxDamage() > 0) {
			renderBar(stack, bounds.x(), bounds.bottom() - 18);
		}

		if(offHand.get()) {
			stack = MC.player.getHeldItemOffhand();

			if(stack != null && stack.getMaxDamage() > 0) {
				renderBar(stack, bounds.x(), bounds.y());
			}
		}
		return bounds;
	}
}
