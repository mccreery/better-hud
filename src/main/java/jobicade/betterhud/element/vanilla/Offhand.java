package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;
import static jobicade.betterhud.BetterHud.WIDGETS;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.eventbus.api.Event;

public class Offhand extends HudElement {
	public Offhand() {
		super("offhand", new SettingPosition("position", DirectionOptions.BAR, DirectionOptions.NONE));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.setPreset(Direction.SOUTH);
	}

	@Override
	public boolean shouldRender(Event event) {
		return !MC.player.getHeldItemOffhand().isEmpty() && super.shouldRender(event);
	}

	@Override
	protected Rect render(Event event) {
		ItemStack offhandStack = MC.player.getHeldItemOffhand();
		EnumHandSide offhandSide = MC.player.getPrimaryHand().opposite();
		Direction offhand = offhandSide == EnumHandSide.RIGHT ? Direction.EAST : Direction.WEST;

		Rect bounds = new Rect(22, 22);
		Rect texture = new Rect(24, 23, 22, 22);

		if(position.isDirection(Direction.SOUTH)) {
			bounds = bounds.align(HudElement.HOTBAR.getLastBounds().grow(SPACER).getAnchor(offhand), offhand.mirrorCol());
		} else {
			bounds = position.applyTo(bounds);
		}

		MC.getTextureManager().bindTexture(WIDGETS);
		GlUtil.drawRect(bounds, texture);

		GlUtil.renderHotbarItem(bounds.translate(3, 3), offhandStack, getPartialTicks(event));
		return bounds;
	}
}
