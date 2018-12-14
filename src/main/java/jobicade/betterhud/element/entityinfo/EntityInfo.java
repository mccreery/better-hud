package jobicade.betterhud.element.entityinfo;

import static jobicade.betterhud.BetterHud.pointedEntity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Rect;

public abstract class EntityInfo extends HudElement {
	public static final List<EntityInfo> ENTITY_INFO = new ArrayList<EntityInfo>();

	protected EntityInfo(String name) {
		super(name);
		ENTITY_INFO.add(this);
	}

	@Override
	public boolean shouldRender(Event event) {
		return event instanceof RenderWorldLastEvent && shouldRender(pointedEntity);
	}

	@Override
	public Rect render(Event event) {
		render(pointedEntity);
		return null;
	}

	public boolean shouldRender(EntityLivingBase entity) {
		return true;
	}

	/** Renders this element to the current billboard */
	public abstract void render(EntityLivingBase entity);
}
