package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemArmor;
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
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;

public class ArmorBars extends HudElement {
	private final SettingChoose bars = new SettingChoose("barType", new String[] {"hidden", "smallBars", "largeBars"});
	private final SettingBoolean showName = new SettingBoolean("showName");
	private final SettingBoolean showDurability = new SettingBoolean("showDurability", Direction.WEST);
	private final SettingChoose durabilityMode = new SettingChoose("durabilityMode", Direction.EAST, "values", "percent") {
		@Override
		public boolean enabled() {
			return showDurability.get();
		}
	};

	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS);
	private final SettingWarnings warnings = new SettingWarnings("damageWarning");

	String[] textCache = new String[4];
	int[] widthCache = new int[4];

	@Override
	public void loadDefaults() {
		settings.set(true);
		bars.setIndex(2);
		showName.set(true);
		showDurability.set(true);
		durabilityMode.setIndex(0);
		position.set(Direction.NORTH_WEST);

		warnings.set(new Integer[] {45, 25, 10});
		warnings.setActive(true);
	}

	public ArmorBars() {
		super("armorBars");

		this.settings.add(position);
		this.settings.add(new Legend("misc"));
		this.settings.add(showDurability);
		this.settings.add(durabilityMode);
		this.settings.add(showName);
		this.settings.add(bars);
		bars.comments.add("hidden, smallBars, largeBars");

		settings.add(warnings);
	}

	public String generateText(ItemStack item) {
		ArrayList<String> parts = new ArrayList<String>();
		if(this.showName.get()) parts.add(item.getDisplayName());
		int maxDamage = item.getMaxDamage();
		float value = (float) (maxDamage - item.getItemDamage()) / (float) maxDamage;

		if(this.showDurability.get()) {
			if(durabilityMode.getIndex() == 1) {
				parts.add(I18n.format("betterHud.strings.percent", FormatUtil.formatToPlaces(value * 100.0, 1)));
			} else {
				parts.add(I18n.format("betterHud.strings.outOf", String.valueOf(maxDamage - item.getItemDamage()), String.valueOf(maxDamage)));
			}
		}

		String text = FormatUtil.separate(I18n.format("betterHud.strings.splitter"), parts.toArray(new String[parts.size()]));
		int count = warnings.getWarning(value);

		if(count > 0) {
			text += " " + I18n.format("betterHud.strings.damaged." + count);
		}
		return text;
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		Direction itemAnchor = position.getAnchor().in(Direction.RIGHT) ? Direction.NORTH_EAST : Direction.NORTH_WEST;

		for(int i = 0; i < 4; i++) {
			ItemStack stack = MC.player.inventory.armorItemInSlot(i);

			if(!stack.isEmpty() && (showName.get() || showDurability.get())) {
				this.textCache[i] = generateText(stack);
			}
		}
		Point size = getLinesSize(textCache);
		size.y = 70;

		// Make sure large bar fits
		if(bars.getIndex() == 2 && size.x < 85) {
			size.x = 85;
		}

		Bounds margin = position.getAnchor().align(new Bounds(-18, 0, 18, 0));
		PaddedBounds bounds = position.applyTo(new PaddedBounds(new Bounds(size), Bounds.EMPTY, margin), manager);

		Bounds item = itemAnchor.anchor(new Bounds(16, 16), bounds);

		MC.mcProfiler.startSection("items");
		RenderHelper.enableGUIStandardItemLighting();
		for(int i = 3; i >= 0; i--) {
			ItemStack stack = MC.player.inventory.armorItemInSlot(i);

			if(stack != null) {
				MC.getRenderItem().renderItemAndEffectIntoGUI(stack, item.x(), item.y());
			} else {
				TextureAtlasSprite empty = MC.getTextureMapBlocks().getAtlasSprite(ItemArmor.EMPTY_SLOT_NAMES[i]);

				GlStateManager.disableLighting();
				MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				MC.ingameGUI.drawTexturedModalRect(item.x(), item.y(), empty, 16, 16);
				GlStateManager.enableLighting();
			}

			item.y(item.y() + 18);
		}
		RenderHelper.disableStandardItemLighting();
		MC.mcProfiler.endSection();

		Bounds content = bounds.contentBounds();

		if(this.showName.get() || this.showDurability.get()) {
			MC.mcProfiler.startSection("text");

			for(int i = 0; i < 4; i++) {
				if(textCache[i] != null) {
					Bounds line = new Bounds();
					itemAnchor.anchor(line, content);
					line.y(line.y() + 18 * i + (bars.getIndex() == 2 ? 2 : 4));

					MC.ingameGUI.drawString(MC.fontRenderer, this.textCache[i], bounds.x(), bounds.y(), Colors.WHITE);
				}
			}
			MC.mcProfiler.endSection();
		}

		if(this.bars.getIndex() != 0) {
			MC.mcProfiler.startSection("bars");
			for(int i = 0; i < 4; i++) {
				ItemStack stack = MC.player.inventory.armorItemInSlot(i);
				if(stack == null) continue;

				if(this.bars.getIndex() == 2) { // Large bars
					/*int x = this.alignment.value == Direction.WEST ? this.bounds.getX() + 21 : this.bounds.getX2() - 85;
					int y = this.bounds.getY() + i * 18 + 12 + (this.showName.value || this.showDurability.value ? 0 : -4);
					RenderUtil.drawDamageBar(x, y, 64, 2, armor[i], false); TODO */
				} else { // Small bars
					Bounds bar = itemAnchor.anchor(new Bounds(2, 16), content.pad(2)); // Align bar outside content box
					HudElement.drawDamageBar(bar, stack, true);
				}
			}
			MC.mcProfiler.endSection();
		}
		return bounds;
	}
}
