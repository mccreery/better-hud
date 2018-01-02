package tk.nukeduck.hud.element.entityinfo;

import static org.lwjgl.opengl.GL11.glTranslatef;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;

public abstract class EntityInfo extends HudElement {
	protected final SettingSlider distance = new SettingSlider("distance", 5, 200).setUnlocalizedValue("betterHud.strings.distanceShort");

	public double getDistance() {
		return distance.value;
	}

	@Override
	public void loadDefaults() {
		distance.value = 100;
	}

	protected EntityInfo(String name) {
		super(name);
		settings.add(distance);
	}

	public abstract void renderInfo(EntityLivingBase entity, float partialTicks);

	// Entity info isn't technically part of the HUD
	@Override public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {return null;}
	@Override public boolean shouldRender() {return false;}

	public static void zIncrease() {
		glTranslatef(0.0F, 0.0F, -0.001F);
	}
}
