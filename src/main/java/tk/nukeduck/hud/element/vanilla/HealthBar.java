package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import tk.nukeduck.hud.util.bars.StatBar;
import tk.nukeduck.hud.util.bars.StatBarHealth;

public class HealthBar extends Bar {
	public HealthBar() {
		super("health");
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(3);
		side.setIndex(0);
	}

	@Override
	protected ElementType getType() {
		return ElementType.HEALTH;
	}

	@Override
	public StatBar getBar() {
		return new StatBarHealth((EntityLivingBase)MC.getRenderViewEntity());
	}
}
