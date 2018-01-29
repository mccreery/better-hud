package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPositionAligned;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;

public class ArmorBars extends EquipmentDisplay {
	private final SettingPositionAligned position = new SettingPositionAligned("position", Direction.CORNERS, Direction.flags(Direction.WEST, Direction.EAST));
	private final SettingChoose barType = new SettingChoose("bars", "visible.off", "smallBars", "largeBars");

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

	private boolean showBars() {
		return barType.getIndex() != 0;
	}
	private boolean largeBars() {
		return barType.getIndex() == 2;
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event) {
		String[] text = null;
		Point size;

		if(hasText()) {
			text = new String[4];

			for(int i = 0; i < 4; i++) {
				text[i] = getText(MC.player.inventory.armorItemInSlot(i));
			}
			size = getLinesSize(text);
			size.y = 16;
		} else {
			size = new Point(0, 16);
		}

		// Make sure large bar fits
		if(largeBars() && size.x < 80) {
			size.x = 80;
		}

		Direction alignment;
		if(position.getAlignment().in(Direction.RIGHT)) {
			alignment = Direction.EAST;
		} else {
			alignment = Direction.WEST;
		}

		Bounds bounds = position.applyTo(new Bounds(size.x + 20, 70));
		Bounds padding = alignment == Direction.EAST ? Bounds.getPadding(0, 0, 20, 0) : Bounds.getPadding(20, 0, 0, 0);
		PaddedBounds row = Direction.NORTH.anchor(new PaddedBounds(new Bounds(size), padding, Bounds.EMPTY), bounds);

		for(int i = 3; i >= 0; i--, row.y(row.y() + 18)) {
			ItemStack stack = MC.player.inventory.armorItemInSlot(i);
			Bounds item = alignment.anchor(new Bounds(18, 16), row);

			if(stack == null || stack.isEmpty()) {
				TextureAtlasSprite empty = MC.getTextureMapBlocks().getAtlasSprite(ItemArmor.EMPTY_SLOT_NAMES[i]);
				MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				MC.ingameGUI.drawTexturedModalRect(item.x(), item.y(), empty, 16, 16);

				continue;
			} else {
				RenderHelper.enableGUIStandardItemLighting();
				MC.getRenderItem().renderItemAndEffectIntoGUI(stack, item.x(), item.y());
				RenderHelper.disableStandardItemLighting();
			}

			Bounds content = row.contentBounds();

			if(hasText() && text[i] != null) {
				MC.mcProfiler.startSection("text");

				Bounds textBounds = alignment.anchor(new Bounds(getLinesSize(text[i])), content);
				if(largeBars()) textBounds.y(textBounds.y() - 1);

				MC.ingameGUI.drawString(MC.fontRenderer, text[i], textBounds.x(), textBounds.y(), Colors.WHITE);
				MC.mcProfiler.endSection();
			}

			if(showBars()) {
				MC.mcProfiler.startSection("bars");

				if(largeBars()) {
					Bounds bar = Direction.SOUTH.anchor(new Bounds(content.width(), 2), content);
					drawDamageBar(bar, stack, false);
				} else {
					Bounds bar = alignment.mirrorX().anchor(new Bounds(2, item.height()), item);
					drawDamageBar(bar, stack, true);
				}

				MC.mcProfiler.endSection();
			}
		}
		return bounds;
	}
}
