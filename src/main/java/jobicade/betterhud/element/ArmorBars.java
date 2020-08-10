package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.render.Boxed;
import jobicade.betterhud.render.DefaultBoxed;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ArmorBars extends EquipmentDisplay {
    private SettingPosition position;
    private SettingChoose barType;
    private SettingBoolean alwaysVisible;

    public ArmorBars() {
        super("armorBars");

        settings.addChildren(
            position = new SettingPosition(DirectionOptions.CORNERS, DirectionOptions.WEST_EAST),
            barType = new SettingChoose("bars", "visible.off", "smallBars", "largeBars"),
            alwaysVisible = new SettingBoolean("alwaysVisible")
        );
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        if(alwaysVisible.get()) return true;

        for(int i = 0; i < 4; i++) {
            if(!MC.player.inventory.armorItemInSlot(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Rect render(OverlayContext context) {
        Grid<Boxed> grid = new Grid<>(new Point(1, 4)).setStretch(true);

        for(int i = 0; i < 4; i++) {
            ItemStack stack = MC.player.inventory.armorItemInSlot(3-i);
            TextureAtlasSprite empty = MC.getTextureMapBlocks().getAtlasSprite(ItemArmor.EMPTY_SLOT_NAMES[3-i]);

            grid.setCell(new Point(0, i), new SlotDisplay(stack, empty));
        }

        Rect bounds = position.applyTo(new Rect(grid.getPreferredSize()));
        grid.setBounds(bounds).render();
        return bounds;
    }

    private class SlotDisplay extends DefaultBoxed {
        private final ItemStack stack;
        private final TextureAtlasSprite empty;

        public SlotDisplay(ItemStack stack, TextureAtlasSprite empty) {
            this.stack = stack;
            this.empty = empty;
        }

        private Label getLabel() {
            return new Label(getText(stack));
        }

        @Override
        public Size getPreferredSize() {
            int textBarWidth = getLabel().getPreferredSize().getWidth();

            if(barType.getIndex() == 2 && showDurability(stack)) {
                textBarWidth = Math.max(textBarWidth, 64);
            }
            return new Size(textBarWidth > 0 ? 20 + textBarWidth : 16, 16);
        }

        @Override
        public void render() {
            Direction contentAlignment = position.getContentAlignment();
            Rect textBarArea = bounds.withWidth(bounds.getWidth() - 20)
                .anchor(bounds, contentAlignment.mirrorCol());

            Rect item = new Rect(16, 16).anchor(bounds, contentAlignment);
            if(stack.isEmpty()) {
                MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                MC.ingameGUI.drawTexturedModalRect(item.getX(), item.getY(), empty, item.getWidth(), item.getHeight());
                MC.getTextureManager().bindTexture(Gui.ICONS);
            } else {
                GlUtil.renderSingleItem(stack, item.getPosition());
            }

            Label label = getLabel();
            label.setBounds(new Rect(label.getPreferredSize()).anchor(textBarArea, contentAlignment)).render();

            int barTypeIndex = barType.getIndex();
            if(barTypeIndex != 0 && showDurability(stack)) {
                Rect bar;

                if(barTypeIndex == 2) {
                    Direction barAlignment = label.getText() != null ? Direction.SOUTH : Direction.CENTER;
                    bar = textBarArea.withHeight(2).anchor(textBarArea, barAlignment);
                } else {
                    bar = item.grow(-2, -13, -1, -1);
                }
                GlUtil.drawDamageBar(bar, stack, false);
            }
        }
    }
}
