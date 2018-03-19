package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.util.Bounds;

public class RidingHealth extends Bar {
	public RidingHealth() {
		super("mountHealth", new Bounds(52, 9, 9, 9), new Bounds(97, 9, 9, 9), new Bounds(88, 9, 9, 9));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(5);
		side.setIndex(1);
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && MC.player.getRidingEntity() instanceof EntityLivingBase;
	}

	@Override
	protected int getCurrent() {
		return (int)((EntityLivingBase)MC.player.getRidingEntity()).getHealth();
	}

	@Override
	protected int getMaximum() {
		return (int)((EntityLivingBase)MC.player.getRidingEntity()).getMaxHealth();
	}

	@Override
	protected ElementType getType() {
		return ElementType.HEALTHMOUNT;
	}
}
