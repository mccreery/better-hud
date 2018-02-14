package tk.nukeduck.hud.element.entityinfo;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.entity.EntityLivingBase;
import tk.nukeduck.hud.element.ElementStub;

public abstract class EntityInfo extends ElementStub {
	protected EntityInfo(String name) {
		super(name);
	}

	/** Calls {@link #render(EntityLivingBase, float)}
	 * if the element should be rendered */
	public void tryRender(EntityLivingBase entity, float partialTicks) {
		if(isEnabled() && shouldRender(entity)) {
			MC.mcProfiler.startSection(name);
			render(entity, partialTicks);
			MC.mcProfiler.endSection();
		}
	}

	public boolean shouldRender(EntityLivingBase entity) {
		return true;
	}

	/** Renders this element to the current billboard */
	public abstract void render(EntityLivingBase entity, float partialTicks);
}
