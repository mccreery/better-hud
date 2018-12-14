package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.util.SortField;

class SortButton extends GuiActionButton {
	private static final ResourceLocation ARROW_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");
	private static final Rect UP_TEXTURE   = new Rect(114, 5, 11, 7);
	private static final Rect DOWN_TEXTURE = new Rect(82, 20, 11, 7);

	private final GuiHudMenu callback;
	SortField<HudElement> sortValue;

	SortButton(GuiHudMenu callback, SortField<HudElement> sortValue) {
		super(I18n.format(sortValue.getUnlocalizedName()));

		this.callback = callback;
		this.sortValue = sortValue;

		setCallback(b -> callback.changeSort(sortValue));
	}

	private boolean isTargeted() {
		return callback.getSortCriteria() == sortValue;
	}

	@Override
	protected int getHoverState(boolean mouseOver) {
		return isTargeted() ? 2 : super.getHoverState(mouseOver);
	}

	@Override
	protected void drawButton(Rect bounds, Point mousePosition, float partialTicks) {
		super.drawButton(bounds, mousePosition, partialTicks);

		if(isTargeted()) {
			Rect texture = callback.isDescending() ? DOWN_TEXTURE : UP_TEXTURE;
			Point position = new Rect(texture).anchor(bounds, Direction.EAST).getPosition().add(-2, 0);

			MC.getTextureManager().bindTexture(ARROW_TEXTURE);
			GlUtil.drawTexturedModalRect(position, texture);
			MC.getTextureManager().bindTexture(ICONS);
		}
	}
}
