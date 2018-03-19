package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;

public class JumpBar extends HudElement {
	public JumpBar() {
		super("jumpBar");
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && MC.player.isRidingHorse();
	}

	@Override
	protected Bounds render(Event event) {
		MC.getTextureManager().bindTexture(ICONS);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();

		float charge = MC.player.getHorseJumpPower();
		Bounds bounds = MANAGER.position(Direction.SOUTH, new Bounds(182, 5), false, 1);
		int filled = (int)(charge * bounds.width());

		GlUtil.drawTexturedModalRect(bounds.position, new Bounds(0, 84, bounds.width(), bounds.height()));

		if(filled > 0) {
			GlUtil.drawTexturedModalRect(bounds.position, new Bounds(0, 89, filled, bounds.height()));
		}

		GlStateManager.enableBlend();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		return bounds;
	}
}
