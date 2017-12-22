package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.Ticker;

public class BiomeName extends TextElement {
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
		return Bounds.EMPTY;
	}
	
	public BiomeName() {
		super("biome");
		pos.possibleLocations = Position.combine(Position.TOP_LEFT, Position.TOP_CENTER, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT);
		Ticker.FASTER.register(this);
	}
	
	private String biome = "";
	
	public void update() {
		if(MC.world == null || MC.player == null) return;
		
		BlockPos pos = new BlockPos((int) MC.player.posX, 0, (int) MC.player.posZ);
		biome = I18n.format("betterHud.strings.biome", MC.world.getBiomeForCoordsBody(pos).getBiomeName());
	}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		if(posMode.index == 0 && pos.value == Position.TOP_CENTER) {
			int y = BetterHud.proxy.elements.compass.enabled ? 40 : 17;
			MC.ingameGUI.drawCenteredString(MC.fontRenderer, biome, event.getResolution().getScaledWidth() / 2, y, this.getColor());
		} else {
			super.render(event, stringManager, layoutManager);
		}
	}

	@Override
	public boolean shouldProfile() {
		return posMode.index == 0 && pos.value == Position.TOP_CENTER;
	}

	@Override
	protected String[] getText() {
		return new String[] {this.biome};
	}
}
