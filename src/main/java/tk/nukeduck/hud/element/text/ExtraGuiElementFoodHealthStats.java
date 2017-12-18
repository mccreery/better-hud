package tk.nukeduck.hud.element.text;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glDisable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingText;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class ExtraGuiElementFoodHealthStats extends ExtraGuiElementText {
	private ElementSettingBoolean saturation;
	
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		saturation.value = true;
		pos.value = Position.BOTTOM_RIGHT;
	}
	
	@Override
	public String getName() {
		return "foodHealthStats";
	}
	
	public ExtraGuiElementFoodHealthStats() {
		super();
		this.settings.add(0, new ElementSettingDivider("position"));
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(saturation = new ElementSettingBoolean("saturation"));
		this.settings.add(0, new ElementSettingText("fhNotice"));
	}
	
	public void update(Minecraft mc) {}
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		glDisable(GL_LIGHTING);
		
		String health = String.valueOf(((int)mc.player.getHealth()) / 2.0f);
		String food = String.valueOf(mc.player.getFoodStats().getFoodLevel() / 2.0F);
		
		int center = resolution.getScaledWidth() / 2;
		int textY = resolution.getScaledHeight() - 35;
		int healthWidth = mc.fontRenderer.getStringWidth(health);
		
		mc.ingameGUI.drawString(mc.fontRenderer, food, center + 95, textY, Colors.WHITE);
		mc.ingameGUI.drawString(mc.fontRenderer, health, center - 95 - healthWidth, textY, Colors.WHITE);
		super.render(mc, resolution, stringManager, layoutManager);
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}

	@Override
	protected String[] getText(Minecraft mc) {
		if(!this.saturation.value) return new String[] {};
		return new String[] {I18n.format("betterHud.strings.saturation",
			Math.round(mc.player.getFoodStats().getSaturationLevel() * 10) / 10.0f)};
	}
}
