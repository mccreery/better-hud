package jobicade.betterhud.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
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
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.function.Function;

import static net.minecraft.inventory.container.PlayerContainer.*;

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

    /**
     * @see net.minecraft.inventory.container.PlayerContainer#TEXTURE_EMPTY_SLOTS
     */
    private static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[] {
        EMPTY_ARMOR_SLOT_BOOTS,
        EMPTY_ARMOR_SLOT_LEGGINGS,
        EMPTY_ARMOR_SLOT_CHESTPLATE,
        EMPTY_ARMOR_SLOT_HELMET
    };

    @Override
    public Rect render(Event event) {
        Grid<Boxed> grid = new Grid<>(new Point(1, 4)).setStretch(true);

        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS);
        MatrixStack matrixStack = ((RenderGameOverlayEvent)event).getMatrixStack();

        for(int i = 0; i < 4; i++) {
            ItemStack stack = Minecraft.getInstance().player.inventory.getArmor(3-i);
            TextureAtlasSprite empty = atlas.apply(TEXTURE_EMPTY_SLOTS[3-i]);

            grid.setCell(new Point(0, i), new SlotDisplay(matrixStack, stack, empty));
        }

        Rect bounds = position.applyTo(new Rect(grid.getPreferredSize()));
        grid.setBounds(bounds).render();
        return bounds;
    }

    private class SlotDisplay extends DefaultBoxed {
        private final MatrixStack matrixStack;
        private final ItemStack stack;
        private final TextureAtlasSprite empty;

        public SlotDisplay(MatrixStack matrixStack, ItemStack stack, TextureAtlasSprite empty) {
            this.matrixStack = matrixStack;
            this.stack = stack;
            this.empty = empty;
        }

        private Label getLabel() {
            return new Label(matrixStack, getText(stack));
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
                Minecraft.getInstance().getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);
                Minecraft.getInstance().gui.blit(matrixStack, item.getX(), item.getY(), 0, item.getWidth(), item.getHeight(), empty);
                Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
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
