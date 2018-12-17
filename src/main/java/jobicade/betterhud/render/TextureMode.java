package jobicade.betterhud.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class TextureMode extends GlMode {
	private final ResourceLocation texture;

	public TextureMode(ResourceLocation texture) {
		this.texture = texture;
	}

	@Override
	public void begin() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
	}

	@Override
	public void end() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
	}
}
