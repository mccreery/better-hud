package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;

import java.util.List;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.util.Colors;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.util.mode.GlMode;
import jobicade.betterhud.util.mode.TextureMode;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.geom.Point;
import jobicade.betterhud.util.StringGroup;

public class ArmorBars extends EquipmentDisplay {
	private SettingChoose barType;
	private SettingBoolean alwaysVisible;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		barType.setIndex(2);
		position.setPreset(Direction.NORTH_WEST);
		alwaysVisible.set(false);
	}

	public ArmorBars() {
		super("armorBars", new SettingPosition(DirectionOptions.CORNERS, DirectionOptions.WEST_EAST));
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(barType = new SettingChoose("bars", "visible.off", "smallBars", "largeBars"));
		settings.add(alwaysVisible = new SettingBoolean("alwaysVisible"));
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
	public Rect render(Event event) {
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

		Rect bounds = position.applyTo(new Rect(size.getX() + 20, 70));
		Rect padding = alignment == Direction.EAST ? Rect.createPadding(0, 0, 20, 0) : Rect.createPadding(20, 0, 0, 0);

		Rect row = new Rect(size).grow(padding);
		row = row.anchor(bounds, Direction.NORTH);

		for(int i = 3; i >= 0; i--, row = row.withY(row.getY() + 18)) {
			ItemStack stack = MC.player.inventory.armorItemInSlot(i);
			Rect item = new Rect(16, 16).anchor(row, alignment);

			if(stack == null || stack.isEmpty()) {
				drawEmptySlot(item.getPosition(), i);
			} else {
				GlUtil.renderSingleItem(stack, item.getPosition());
				Rect content = row.grow(padding.invert());

				if(hasText() && text[i] != null) {
					MC.mcProfiler.startSection("text");

					Rect textRect = new Rect(GlUtil.getStringSize(text[i])).anchor(content, alignment);
					if(stack.isItemStackDamageable() && largeBars()) {
						textRect = textRect.withY(textRect.getY() - 1);
					}

					MC.ingameGUI.drawString(MC.fontRenderer, text[i], textRect.getX(), textRect.getY(), Colors.WHITE);
					MC.mcProfiler.endSection();
				}

				if(stack.isItemStackDamageable() && showBars()) {
					MC.mcProfiler.startSection("bars");

					if(largeBars()) {
						Rect bar = new Rect(content.getWidth(), 2).anchor(content, Direction.SOUTH);
						GlUtil.drawDamageBar(bar, stack, false);
					} else {
						Rect bar = new Rect(2, item.getHeight()).anchor(item.grow(2, 0, 2, 0), alignment.mirrorCol());
						GlUtil.drawDamageBar(bar, stack, true);
					}

					MC.mcProfiler.endSection();
				}
			}
		}
		return bounds;
	}
}
