package jobicade.betterhud.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.SortField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

class SortButton extends GuiActionButton {
    private static final ResourceLocation ARROW_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");
    private static final Rect UP_TEXTURE   = new Rect(114, 5, 11, 7);
    private static final Rect DOWN_TEXTURE = new Rect(82, 20, 11, 7);

    private final GuiHudMenu callback;
    SortField<HudElement> sortValue;

    SortButton(GuiHudMenu callback, SortField<HudElement> sortValue) {
        super(I18n.get(sortValue.getUnlocalizedName()));

        this.callback = callback;
        this.sortValue = sortValue;

        setCallback(b -> callback.changeSort(sortValue));
    }

    private boolean isTargeted() {
        return callback.getSortCriteria() == sortValue;
    }

    @Override
    protected int getYImage(boolean mouseOver) {
        return isTargeted() ? 2 : super.getYImage(mouseOver);
    }

    @Override
    protected void drawButton(MatrixStack matrixStack, Rect bounds, Point mousePosition, float partialTicks) {
        super.drawButton(matrixStack, bounds, mousePosition, partialTicks);

        if(isTargeted()) {
            Rect texture = callback.isDescending() ? DOWN_TEXTURE : UP_TEXTURE;
            Rect arrow = new Rect(texture).anchor(bounds, Direction.EAST).translate(-2, 0);

            Minecraft.getInstance().getTextureManager().bind(ARROW_TEXTURE);
            GlUtil.drawRect(arrow, texture);
            Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
        }
    }
}
