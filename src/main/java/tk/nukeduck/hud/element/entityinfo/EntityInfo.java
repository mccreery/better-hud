package tk.nukeduck.hud.element.entityinfo;

import static org.lwjgl.opengl.GL11.glTranslatef;

import net.minecraft.entity.EntityLivingBase;
import tk.nukeduck.hud.element.ElementStub;
import tk.nukeduck.hud.element.settings.SettingSlider;

public abstract class EntityInfo extends ElementStub {
	protected final SettingSlider distance = new SettingSlider("distance", 5, 200).setUnlocalizedValue("betterHud.strings.distanceShort");

	protected EntityInfo(String name) {
		super(name);
		settings.add(distance);
	}

	public double getDistance() {
		return distance.get();
	}

	public abstract void renderInfo(EntityLivingBase entity, float partialTicks);

	public static void zIncrease() {
		glTranslatef(0.0F, 0.0F, -0.001F);
	}

	@Override
	public void loadDefaults() {
		distance.set(100.0);
	}
}
