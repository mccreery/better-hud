package tk.nukeduck.hud.element.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementBiome extends ExtraGuiElementText {
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		pos.value = Position.TOP_CENTER;
	}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		if(posMode.index == 0 && pos.value == Position.TOP_CENTER) {
			int stringWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(biome);
			return new Bounds((resolution.getScaledWidth() - stringWidth) / 2, BetterHud.proxy.elements.compass.enabled ? 40 : 17, stringWidth, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
		}
		return super.getBounds(resolution);
	}
	
	@Override
	public String getName() {
		return "biome";
	}
	
	public ExtraGuiElementBiome() {
		super();
		pos.possibleLocations = Position.combine(Position.TOP_LEFT, Position.TOP_CENTER, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT);
		this.registerUpdates(UpdateSpeed.FASTER);
	}
	
	private String biome = "";
	
	public void update(Minecraft mc) {
		if(mc.world == null || mc.player == null) return;
		
		BlockPos pos = new BlockPos((int) mc.player.posX, 0, (int) mc.player.posZ);
		biome = I18n.format("betterHud.strings.biome", mc.world.getBiomeForCoordsBody(pos).getBiomeName());
	}
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		if(posMode.index == 0 && pos.value == Position.TOP_CENTER) {
			int y = BetterHud.proxy.elements.compass.enabled ? 40 : 17;
			mc.ingameGUI.drawCenteredString(mc.fontRenderer, biome, resolution.getScaledWidth() / 2, y, this.getColor());
		} else {
			super.render(mc, resolution, stringManager, layoutManager);
		}
	}

	@Override
	public boolean shouldProfile() {
		return posMode.index == 0 && pos.value == Position.TOP_CENTER;
	}

	@Override
	protected String[] getText(Minecraft mc) {
		return new String[] {this.biome};
	}
}
