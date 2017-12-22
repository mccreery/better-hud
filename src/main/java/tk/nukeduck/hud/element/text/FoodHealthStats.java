package tk.nukeduck.hud.element.text;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glDisable;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class FoodHealthStats extends TextElement {
	private SettingBoolean saturation;
	
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		saturation.value = true;
		pos.value = Position.BOTTOM_RIGHT;
	}
	
	public FoodHealthStats() {
		super("foodHealthStats");
		this.settings.add(0, new Divider("position"));
		this.settings.add(new Divider("misc"));
		this.settings.add(saturation = new SettingBoolean("saturation"));
		this.settings.add(0, new Legend("fhNotice"));
	}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		glDisable(GL_LIGHTING);
		
		String health = String.valueOf(((int)MC.player.getHealth()) / 2.0f);
		String food = String.valueOf(MC.player.getFoodStats().getFoodLevel() / 2.0F);
		
		int center = event.getResolution().getScaledWidth() / 2;
		int textY = event.getResolution().getScaledHeight() - 35;
		int healthWidth = MC.fontRenderer.getStringWidth(health);
		
		MC.ingameGUI.drawString(MC.fontRenderer, food, center + 95, textY, Colors.WHITE);
		MC.ingameGUI.drawString(MC.fontRenderer, health, center - 95 - healthWidth, textY, Colors.WHITE);
		super.render(event, stringManager, layoutManager);
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}

	@Override
	protected String[] getText() {
		if(!this.saturation.value) return new String[] {};
		return new String[] {I18n.format("betterHud.strings.saturation",
			Math.round(MC.player.getFoodStats().getSaturationLevel() * 10) / 10.0f)};
	}
}
