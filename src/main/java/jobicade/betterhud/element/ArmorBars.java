package jobicade.betterhud.element;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.render.Boxed;
import jobicade.betterhud.render.DefaultBoxed;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.geom.Point;

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

    @Override
    public boolean shouldRender(Event event) {
        if(!super.shouldRender(event)) return false;
        if(alwaysVisible.get()) return true;

        for(int i = 0; i < 4; i++) {
            if(!Minecraft.getInstance().player.inventory.getArmor(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Rect render(Event event) {
        Grid<Boxed> grid = new Grid<>(new Point(1, 4)).setStretch(true);

        for(int i = 0; i < 4; i++) {
            ItemStack stack = Minecraft.getInstance().player.inventory.getArmor(3-i);
            TextureAtlasSprite empty = Minecraft.getInstance().func_147117_R().getTexture(ItemArmor.field_94603_a[3-i]);

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
                Minecraft.getInstance().getTextureManager().bind(TextureMap.LOCATION_BLOCKS);
                Minecraft.getInstance().gui.func_175175_a(item.getX(), item.getY(), empty, item.getWidth(), item.getHeight());
                Minecraft.getInstance().getTextureManager().bind(Gui.field_110324_m);
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
