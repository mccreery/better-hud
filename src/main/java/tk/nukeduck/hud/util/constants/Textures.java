package tk.nukeduck.hud.util.constants;

import net.minecraft.util.ResourceLocation;

public class Textures {
	public static ResourceLocation iconsHud, particles;

	public static void init() {
		iconsHud = new ResourceLocation("hud", "textures/gui/icons_hud.png");
		particles = new ResourceLocation("textures/particle/particles.png");
	}
}
