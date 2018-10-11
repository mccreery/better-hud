package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.mode.GlMode;
import tk.nukeduck.hud.util.mode.TextureMode;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.StringGroup;

public class ArmorBars extends EquipmentDisplay {
	private final SettingChoose barType = new SettingChoose("bars", "visible.off", "smallBars", "largeBars");
	private final SettingBoolean alwaysVisible = new SettingBoolean("alwaysVisible");

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		barType.setIndex(2);
		position.setPreset(Direction.NORTH_WEST);
		alwaysVisible.set(false);
	}

	public ArmorBars() {
		super("armorBars", new SettingPosition(Options.CORNERS, Options.WEST_EAST));

		settings.add(barType);
		settings.add(alwaysVisible);
	}

	private boolean showBars() {
		return barType.getIndex() != 0;
	}
	private boolean largeBars() {
		return barType.getIndex() == 2;
	}

	private static void drawEmptySlot(Point position, int slot) {
		GlMode.push(new TextureMode(TextureMap.LOCATION_BLOCKS_TEXTURE));
		TextureAtlasSprite empty = MC.getTextureMapBlocks().getAtlasSprite(ItemArmor.EMPTY_SLOT_NAMES[slot]);
		MC.ingameGUI.drawTexturedModalRect(position.getX(), position.getY(), empty, 16, 16);
		GlMode.pop();
	}

	@Override
	public boolean shouldRender(Event event) {
		if(!super.shouldRender(event)) return false;
		if(alwaysVisible.get()) return true;

		for(int i = 0; i < 4; i++) {
			if(!MC.player.inventory.armorItemInSlot(i).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Bounds render(Event event) {
		String[] text = null;
		Point size;

		if(hasText()) {
			text = new String[4];
			StringGroup group = new StringGroup(text);

			for(int i = 0; i < 4; i++) {
				text[i] = getText(MC.player.inventory.armorItemInSlot(i));
			}
			size = group.getSize().withY(16);
		} else {
			size = new Point(0, 16);
		}

		// Make sure large bar fits
		if(largeBars() && size.getX() < 80) {
			size = size.withX(80);
		}

		Direction alignment = position.getContentAlignment();

		Bounds bounds = position.applyTo(new Bounds(size.getX() + 20, 70));
		Bounds padding = alignment == Direction.EAST ? Bounds.createPadding(0, 0, 20, 0) : Bounds.createPadding(20, 0, 0, 0);

		Bounds row = new Bounds(size).grow(padding);
		row = row.anchor(bounds, Direction.NORTH);

		for(int i = 3; i >= 0; i--, row = row.withY(row.getY() + 18)) {
			ItemStack stack = MC.player.inventory.armorItemInSlot(i);
			Bounds item = new Bounds(16, 16).anchor(row, alignment);

			if(stack == null || stack.isEmpty()) {
				drawEmptySlot(item.getPosition(), i);
			} else {
				GlUtil.renderSingleItem(stack, item.getPosition());
				Bounds content = row.grow(padding.scale(-1));

				if(hasText() && text[i] != null) {
					MC.mcProfiler.startSection("text");

					Bounds textBounds = new Bounds(GlUtil.getStringSize(text[i])).anchor(content, alignment);
					if(stack.isItemStackDamageable() && largeBars()) {
						textBounds = textBounds.withY(textBounds.getY() - 1);
					}

					MC.ingameGUI.drawString(MC.fontRenderer, text[i], textBounds.getX(), textBounds.getY(), Colors.WHITE);
					MC.mcProfiler.endSection();
				}

				if(stack.isItemStackDamageable() && showBars()) {
					MC.mcProfiler.startSection("bars");

					if(largeBars()) {
						Bounds bar = new Bounds(content.getWidth(), 2).anchor(content, Direction.SOUTH);
						GlUtil.drawDamageBar(bar, stack, false);
					} else {
						Bounds bar = new Bounds(2, item.getHeight()).anchor(item.grow(2, 0, 2, 0), alignment.mirrorColumn());
						GlUtil.drawDamageBar(bar, stack, true);
					}

					MC.mcProfiler.endSection();
				}
			}
		}
		return bounds;
	}
}
