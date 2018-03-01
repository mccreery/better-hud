package tk.nukeduck.hud.element.entityinfo;

import static tk.nukeduck.hud.BetterHud.pointedEntity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Bounds;

public abstract class EntityInfo extends HudElement {
	public static final List<EntityInfo> ENTITY_INFO = new ArrayList<EntityInfo>();

	protected EntityInfo(String name) {
		super(name);
		ENTITY_INFO.add(this);
	}

	@Override
	public boolean shouldRender(RenderPhase phase) {
		return phase == RenderPhase.BILLBOARD && shouldRender(pointedEntity);
	}

	@Override
	public Bounds render(RenderPhase phase) {
		render(pointedEntity);
		return null;
	}

	public boolean shouldRender(EntityLivingBase entity) {
		return true;
	}

	/** Renders this element to the current billboard */
	public abstract void render(EntityLivingBase entity);
}
