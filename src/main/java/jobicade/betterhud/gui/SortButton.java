package jobicade.betterhud.gui;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.SortField;
import net.minecraft.client.Minecraft;
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
    protected int func_146114_a(boolean mouseOver) {
        return isTargeted() ? 2 : super.func_146114_a(mouseOver);
    }

    @Override
    protected void drawButton(Rect bounds, Point mousePosition, float partialTicks) {
        super.drawButton(bounds, mousePosition, partialTicks);

        if(isTargeted()) {
            Rect texture = callback.isDescending() ? DOWN_TEXTURE : UP_TEXTURE;
            Rect arrow = new Rect(texture).anchor(bounds, Direction.EAST).translate(-2, 0);

            Minecraft.getInstance().getTextureManager().bind(ARROW_TEXTURE);
            GlUtil.drawRect(arrow, texture);
            Minecraft.getInstance().getTextureManager().bind(field_110324_m);
        }
    }
}
