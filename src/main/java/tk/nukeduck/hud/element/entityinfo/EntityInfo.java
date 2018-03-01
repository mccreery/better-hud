package tk.nukeduck.hud.element.entityinfo;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import tk.nukeduck.hud.element.ElementStub;

public abstract class EntityInfo extends ElementStub {
	public static final List<EntityInfo> ENTITY_INFO = new ArrayList<EntityInfo>();

	protected EntityInfo(String name) {
		super(name);
		ENTITY_INFO.add(this);
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
