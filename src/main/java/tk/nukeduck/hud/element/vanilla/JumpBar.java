package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;

public class JumpBar extends OverrideElement {
	public JumpBar() {
		super("jumpBar", new SettingPosition("position", Options.BAR, Options.NORTH_SOUTH));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(2);
	}

	@Override
	public boolean shouldRender(Event event) {
		return MC.player.isRidingHorse() && super.shouldRender(event);
	}

	@Override
	protected Bounds render(Event event) {
		MC.getTextureManager().bindTexture(ICONS);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();

		Bounds bounds = new Bounds(182, 5);
		if(!position.isCustom() && position.getDirection() == Direction.SOUTH) {
			bounds = MANAGER.position(Direction.SOUTH, bounds, false, 1);
		} else {
			bounds = position.applyTo(bounds);
		}

		float charge = MC.player.getHorseJumpPower();
		int filled = (int)(charge * bounds.getWidth());

		GlUtil.drawTexturedModalRect(bounds.getPosition(), new Bounds(0, 84, bounds.getWidth(), bounds.getHeight()));

		if(filled > 0) {
			GlUtil.drawTexturedModalRect(bounds.getPosition(), new Bounds(0, 89, filled, bounds.getHeight()));
		}

		GlStateManager.enableBlend();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		return bounds;
	}

	@Override
	protected ElementType getType() {
		return ElementType.JUMPBAR;
	}
}
