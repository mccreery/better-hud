package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPositionAligned;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.StringGroup;

public class ArmorBars extends EquipmentDisplay {
	private final SettingPositionAligned position = new SettingPositionAligned("position", Direction.CORNERS, Direction.getFlags(Direction.WEST, Direction.EAST));
	private final SettingChoose barType = new SettingChoose("bars", "visible.off", "smallBars", "largeBars");
	private final SettingBoolean alwaysVisible = new SettingBoolean("alwaysVisible");

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		barType.setIndex(2);
		position.set(Direction.NORTH_WEST);
		alwaysVisible.set(false);
	}

	public ArmorBars() {
		super("armorBars");

		settings.add(barType);
		settings.add(alwaysVisible);
		settings.add(position);
	}

	private boolean showBars() {
		return barType.getIndex() != 0;
	}
	private boolean largeBars() {
		return barType.getIndex() == 2;
	}

	private static void drawEmptySlot(Point position, int slot) {
		GlUtil.enableBlendTranslucent();
		MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		TextureAtlasSprite empty = MC.getTextureMapBlocks().getAtlasSprite(ItemArmor.EMPTY_SLOT_NAMES[slot]);
		MC.ingameGUI.drawTexturedModalRect(position.x, position.y, empty, 16, 16);
	}

	@Override
	public boolean shouldRender(RenderPhase phase) {
		if(!super.shouldRender(phase)) return false;
		if(alwaysVisible.get()) return true;

		for(int i = 0; i < 4; i++) {
			if(!MC.player.inventory.armorItemInSlot(i).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Bounds render(RenderPhase phase) {
		String[] text = null;
		Point size;

		if(hasText()) {
			text = new String[4];
			StringGroup group = new StringGroup(text);

			for(int i = 0; i < 4; i++) {
				text[i] = getText(MC.player.inventory.armorItemInSlot(i));
			}
			size = group.getSize();
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
				drawEmptySlot(item.position, i);
			} else {
				GlUtil.renderSingleItem(stack, item.position);
				Bounds content = row.contentBounds();

				if(hasText() && text[i] != null) {
					MC.mcProfiler.startSection("text");

					Bounds textBounds = alignment.anchor(new Bounds(GlUtil.getStringSize(text[i])), content);
					if(largeBars()) textBounds.y(textBounds.y() - 1);

					MC.ingameGUI.drawString(MC.fontRenderer, text[i], textBounds.x(), textBounds.y(), Colors.WHITE);
					MC.mcProfiler.endSection();
				}

				if(showBars()) {
					MC.mcProfiler.startSection("bars");

					if(largeBars()) {
						Bounds bar = Direction.SOUTH.anchor(new Bounds(content.width(), 2), content);
						GlUtil.drawDamageBar(bar, stack, false);
					} else {
						Bounds bar = alignment.mirrorColumn().anchor(new Bounds(2, item.height()), item);
						GlUtil.drawDamageBar(bar, stack, true);
					}

					MC.mcProfiler.endSection();
				}
			}
		}
		return bounds;
	}
}
