package tk.nukeduck.hud.element.entityinfo;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import tk.nukeduck.hud.element.ElementStub;
import tk.nukeduck.hud.element.settings.SettingSlider;

public abstract class EntityInfo extends ElementStub {
	protected final SettingSlider distance = new SettingSlider("distance", 5, 200).setUnlocalizedValue("betterHud.hud.meters");

	protected EntityInfo(String name) {
		super(name);
		settings.add(distance);
	}

	public double getDistance() {
		return distance.get();
	}

	public abstract void render(EntityLivingBase entity, float partialTicks);

	@Deprecated public static void zIncrease() {
		GlStateManager.translate(0, 0, -.001f);
	}

	@Override
	public void loadDefaults() {
		distance.set(100.0);
	}
}
