package tk.nukeduck.hud.util.mode;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.util.ResourceLocation;

public class TextureMode extends GlMode {
	private final ResourceLocation texture;

	public TextureMode(ResourceLocation texture) {
		this.texture = texture;
	}

	@Override
	public void begin() {
		MC.getTextureManager().bindTexture(texture);
	}

	@Override
	public void end() {
		MC.getTextureManager().bindTexture(ICONS);
	}
}
