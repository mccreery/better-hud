package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.SortField;

class SortButton extends GuiActionButton {
	private static final ResourceLocation ARROW_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");
	private static final Bounds UP_TEXTURE   = new Bounds(114, 5, 11, 7);
	private static final Bounds DOWN_TEXTURE = new Bounds(82, 20, 11, 7);

	private final GuiHudMenu callback;
	SortField<HudElement> sortValue;

	SortButton(GuiHudMenu callback, SortField<HudElement> sortValue) {
		super(I18n.format(sortValue.getUnlocalizedName()));

		this.callback = callback;
		this.sortValue = sortValue;
	}

	@Override
	public void actionPerformed() {
		callback.changeSort(sortValue);
	}

	private boolean isTargeted() {
		return callback.getSortCriteria() == sortValue;
	}

	@Override
	protected int getHoverState(boolean mouseOver) {
		return isTargeted() ? 2 : super.getHoverState(mouseOver);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		super.drawButton(mc, mouseX, mouseY, partialTicks);

		if(isTargeted()) {
			Bounds texture = callback.isDescending() ? DOWN_TEXTURE : UP_TEXTURE;
			Point position = new Bounds(texture).anchor(getBounds(), Direction.EAST).getPosition().add(-2, 0);

			MC.getTextureManager().bindTexture(ARROW_TEXTURE);
			GlUtil.drawTexturedModalRect(position, texture);
		}
	}
}
