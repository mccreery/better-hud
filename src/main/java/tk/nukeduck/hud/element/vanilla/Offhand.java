package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;

public class Offhand extends OverrideElement {
	private final SettingPosition position = new SettingPosition("position", Options.BAR, Options.NONE);

	public Offhand() {
		super("offhand");
		settings.add(position);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.setPreset(Direction.SOUTH);
	}

	@Override
	protected ElementType getType() {
		return null;
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

		GlStateManager.enableBlend();
		GlStateManager.enableDepth();

		MC.getTextureManager().bindTexture(BetterHud.WIDGETS);
		GlUtil.drawTexturedModalRect(bounds, texture);

		RenderHelper.enableGUIStandardItemLighting();
		GlUtil.renderHotbarItem(bounds.translate(3, 3), offhandStack, getPartialTicks(event));
		RenderHelper.disableStandardItemLighting();

		return bounds;
	}
}
