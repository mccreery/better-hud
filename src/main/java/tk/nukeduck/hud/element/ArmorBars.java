package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;

public class ArmorBars extends EquipmentDisplay {
	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS);
	private final SettingChoose barType = new SettingChoose("barType", new String[] {"hidden", "smallBars", "largeBars"});

	@Override
	public void loadDefaults() {
		settings.set(true);
		barType.setIndex(2);
		position.set(Direction.NORTH_WEST);

		super.loadDefaults();
	}

	public ArmorBars() {
		super("armorBars");

		settings.add(position);
		settings.add(barType);
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		Direction itemAnchor = position.getAnchor().in(Direction.RIGHT) ? Direction.NORTH_EAST : Direction.NORTH_WEST;
		String[] text = null;
		Point size;

		if(hasText()) {
			text = new String[4];

			for(int i = 0; i < 4; i++) {
				text[i] = getText(MC.player.inventory.armorItemInSlot(i));
			}
			size = getLinesSize(text);
			size.y = 70;
		} else {
			size = new Point(18, 70);
		}

		// Make sure large bar fits
		if(barType.getIndex() == 2 && size.x < 85) {
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

		if(hasText()) {
			MC.mcProfiler.startSection("text");

			for(int i = 0; i < 4; i++) {
				if(text[i] != null) {
					Bounds line = new Bounds();
					itemAnchor.anchor(line, content);
					line.y(line.y() + 18 * i + (barType.getIndex() == 2 ? 2 : 4));

					MC.ingameGUI.drawString(MC.fontRenderer, text[i], bounds.x(), bounds.y(), Colors.WHITE);
				}
			}
			MC.mcProfiler.endSection();
		}

		if(this.barType.getIndex() != 0) {
			MC.mcProfiler.startSection("bars");
			for(int i = 0; i < 4; i++) {
				ItemStack stack = MC.player.inventory.armorItemInSlot(i);
				if(stack == null) continue;

				if(this.barType.getIndex() == 2) { // Large bars
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
