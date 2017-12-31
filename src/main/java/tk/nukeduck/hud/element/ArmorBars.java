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
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;

public class ArmorBars extends HudElement {
	private final SettingChoose bars;
	private final SettingBoolean showName;
	private final SettingBoolean showDurability;
	private final SettingChoose durabilityMode;

	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS);

	private SettingBoolean enableWarnings;
	private SettingSlider[] damageWarnings;

	String[] textCache = new String[4];
	int[] widthCache = new int[4];

	@Override
	public void loadDefaults() {
		this.setEnabled(true);
		bars.index = 2;
		showName.set(true);
		showDurability.set(true);
		durabilityMode.index = 0;
		position.load(Direction.NORTH_WEST);

		enableWarnings.set(true);
		damageWarnings[0].value = 45.0;
		damageWarnings[1].value = 25.0;
		damageWarnings[2].value = 10.0;
	}

	public ArmorBars() {
		super("armorBars");

		this.settings.add(position);
		this.settings.add(new Legend("misc"));
		this.settings.add(showDurability = new SettingBoolean("showDurability", Direction.WEST));
		this.settings.add(durabilityMode = new SettingChoose("durabilityMode", Direction.EAST, "values", "percent") {
			@Override
			public boolean enabled() {
				return showDurability.get();
			}
		});
		this.settings.add(showName = new SettingBoolean("showName"));
		this.settings.add(bars = new SettingChoose("barType", new String[] {"hidden", "smallBars", "largeBars"}));
		bars.comments.add("hidden, smallBars, largeBars");
		
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

	public String generateText(ItemStack item) {
		ArrayList<String> parts = new ArrayList<String>();
		if(this.showName.get()) parts.add(item.getDisplayName());
		int maxDamage = item.getMaxDamage();
		float value = (float) (maxDamage - item.getItemDamage()) / (float) maxDamage;
		if(this.showDurability.get()) {
			if(durabilityMode.getValue().equals("percent")) {
				parts.add(I18n.format("betterHud.strings.percent", FormatUtil.ONE_PLACE.format(value * 100.0)));
			} else {
				parts.add(I18n.format("betterHud.strings.outOf", String.valueOf(maxDamage - item.getItemDamage()), String.valueOf(maxDamage)));
			}
		}

		if(enableWarnings.get()) {
			int count = -1;
			for(int a = 0; a < this.damageWarnings.length; a++) {
				if(value * 100.0f <= damageWarnings[a].value) {
					count = a;
				}
			}
			String exclamation = count == -1 ? "" : I18n.format("betterHud.strings.damaged." + count);
			return FormatUtil.separate(I18n.format("betterHud.strings.splitter"), parts.toArray(new String[parts.size()])) + " " + exclamation;
		} else {
			return FormatUtil.separate(I18n.format("betterHud.strings.splitter"), parts.toArray(new String[parts.size()]));
		}
	}
	
	/*public Point getPosition(ScaledResolution resolution, LayoutManager manager) {
		if(position.getDirection() != null) {
			return manager.getPosition(position.getDirection(), new Point(resolution));
		} else {
			return position.getPosition();
		}
	}*/

	/*private Bounds generateBounds(ScaledResolution resolution, LayoutManager layoutManager, ItemStack[] armor) {
		Bounds b = new Bounds(0, 0, 16, 0);

		boolean anyArmor = false;
		for(int i = 0; i < 4; i++) {
			if(isStackValid(armor[i])) {
				anyArmor = true;
				
				if(this.showName.get() || this.showDurability.get()) {
					this.textCache[i] = generateText(armor[i]);
					widthCache[i] = 21 + MC.fontRenderer.getStringWidth(textCache[i]);
					if(widthCache[i] > b.width()) b.width(widthCache[i]);
				}
			}
		}
		if(!anyArmor) return b;
		
		if(this.bars.index == 2) {
			b.width(Math.max(b.width(), 85));
		} else if(this.bars.index == 1) {
			b.width(Math.max(b.width(), 20));
		}
		
		b.height(70);
		b.position = getPosition(resolution, layoutManager);
		return b;
	}*/

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
		if(bars.index == 2 && size.x < 85) {
			size.x = 85;
		}

		Bounds margin = position.getAnchor().align(new Bounds(-18, 0, 18, 0));
		PaddedBounds bounds = position.applyTo(new PaddedBounds(new Bounds(size), Bounds.EMPTY, margin), manager);

		Bounds item = itemAnchor.anchor(new Bounds(16, 16), bounds);

		MC.mcProfiler.startSection("items");
		RenderHelper.enableGUIStandardItemLighting();
		for(int i = 0; i < 4; i++) {
			ItemStack stack = MC.player.inventory.armorItemInSlot(i);

			if(stack != null) {
				MC.getRenderItem().renderItemAndEffectIntoGUI(stack, item.x(), item.y());
			} else {
				TextureAtlasSprite empty = MC.getTextureMapBlocks().getAtlasSprite(ItemArmor.EMPTY_SLOT_NAMES[i]); // TODO check order

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
					line.y(line.y() + 18 * i + (bars.index == 2 ? 2 : 4));

					MC.ingameGUI.drawString(MC.fontRenderer, this.textCache[i], bounds.x(), bounds.y(), Colors.WHITE);
				}
			}
			MC.mcProfiler.endSection();
		}

		if(this.bars.index != 0) {
			MC.mcProfiler.startSection("bars");
			for(int i = 0; i < 4; i++) {
				ItemStack stack = MC.player.inventory.armorItemInSlot(i);
				if(stack == null) continue;

				if(this.bars.index == 2) { // Large bars
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
