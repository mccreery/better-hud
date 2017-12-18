package tk.nukeduck.hud.element.text;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glDisable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingText;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;

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
		
		int h = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
		String health = (h / 2) + "." + "05".charAt(h % 2);
		int f = mc.thePlayer.getFoodStats().getFoodLevel();
		String food = (f / 2) + "." + "05".charAt(f % 2);
		
		int center = resolution.getScaledWidth() / 2;
		int textY = resolution.getScaledHeight() - 35;
		int healthWidth = mc.fontRendererObj.getStringWidth(health);
		
		mc.ingameGUI.drawString(mc.fontRendererObj, food, center + 95, textY, RenderUtil.colorRGB(255, 255, 255));
		mc.ingameGUI.drawString(mc.fontRendererObj, health, center - 95 - healthWidth, textY, RenderUtil.colorRGB(255, 255, 255));
		super.render(mc, resolution, stringManager, layoutManager);
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}

	@Override
	protected String[] getText(Minecraft mc) {
		if(!this.saturation.value) return new String[] {};
		return new String[] {FormatUtil.translatePre("strings.saturation", String.valueOf(Math.round(mc.thePlayer.getFoodStats().getSaturationLevel() * 10.0) / 10.0))};
	}
}