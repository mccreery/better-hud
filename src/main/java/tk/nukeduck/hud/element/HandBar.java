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
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.RenderUtil;

public class HandBar extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS | Direction.SOUTH.flag());

	private final SettingBoolean showName;
	private final SettingBoolean showDurability;
	private final SettingChoose durabilityMode;
	private final SettingBoolean showItem;
	private final SettingBoolean offHand;
	private final SettingBoolean showBars;

	private final SettingBoolean enableWarnings;
	private final SettingSlider[] damageWarnings;

	@Override
	public void loadDefaults() {
		this.setEnabled(true);
		position.load(Direction.SOUTH);

		showName.set(true);
		showDurability.set(true);
		durabilityMode.index = 0;
		showItem.set(true);
		showBars.set(true);
		offHand.set(false);
		enableWarnings.set(true);
		damageWarnings[0].value = 45.0;
		damageWarnings[1].value = 25.0;
		damageWarnings[2].value = 10.0;
	}

	public HandBar() {
		super("handBar");

		settings.add(position);
		settings.add(new Legend("misc"));
		settings.add(showItem = new SettingBoolean("showItem"));
		settings.add(showBars = new SettingBoolean("showBars"));
		settings.add(showDurability = new SettingBoolean("showDurability", Direction.WEST));
		settings.add(durabilityMode = new SettingChoose("durabilityMode", Direction.EAST, new String[] {"values", "percent"}));
		settings.add(showName = new SettingBoolean("showName"));
		settings.add(offHand = new SettingBoolean("offHand"));

		this.settings.add(new Legend("damageWarning"));
		this.settings.add(this.enableWarnings = new SettingBoolean("damageWarning"));
		damageWarnings = new SettingSlider[3];
		Direction[] positions = new Direction[] {Direction.WEST, Direction.CENTER, Direction.EAST};
		for(int i = 0; i < 3; i++) {
			this.settings.add(damageWarnings[i] = new SettingSlider("damaged." + String.valueOf(i), 1, 100, 1, positions[i]) {
				@Override
				public String getSliderText() {
					return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format("betterHud.strings.percent", String.valueOf((int) this.value)));
				}
			});
		}
	}

	private String generateText(ItemStack stack) {
		int maxDamage = stack.getMaxDamage();
		float value = (float) (maxDamage - stack.getItemDamage()) / (float) maxDamage;

		String dur, text;
		if(durabilityMode.getValue().equals("percent")) {
			dur = I18n.format("betterHud.strings.percent", FormatUtil.ONE_PLACE.format(value * 100.0));
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

		if(enableWarnings.get()) {
			int count = -1;
			for(int a = 0; a < this.damageWarnings.length; a++) {
				if(value * 100.0f <= damageWarnings[a].value) {
					count = a;
				}
			}
			text += count == -1 ? "" : " " + I18n.format("betterHud.strings.damaged." + count);
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
