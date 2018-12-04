package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;
import static tk.nukeduck.hud.BetterHud.WIDGETS;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.mode.GlMode;
import tk.nukeduck.hud.util.mode.TextureMode;

public class Offhand extends HudElement {
	public Offhand() {
		super("offhand", new SettingPosition("position", Options.BAR, Options.NONE));
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
	protected Bounds render(Event event) {
		ItemStack offhandStack = MC.player.getHeldItemOffhand();
		EnumHandSide offhandSide = MC.player.getPrimaryHand().opposite();
		Direction offhand = offhandSide == EnumHandSide.RIGHT ? Direction.EAST : Direction.WEST;

		Bounds bounds = new Bounds(22, 22);
		Bounds texture = new Bounds(24, 23, 22, 22);

		if(position.isDirection(Direction.SOUTH)) {
			bounds = bounds.align(HudElement.HOTBAR.getLastBounds().grow(SPACER).getAnchor(offhand), offhand.mirrorColumn());
		} else {
			bounds = position.applyTo(bounds);
		}

		GlMode.push(new TextureMode(WIDGETS));
		GlUtil.drawTexturedModalRect(bounds, texture);
		GlMode.pop();

		RenderHelper.enableGUIStandardItemLighting();
		GlUtil.renderHotbarItem(bounds.translate(3, 3), offhandStack, getPartialTicks(event));
		RenderHelper.disableStandardItemLighting();

		return bounds;
	}
}
