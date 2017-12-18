package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementCompass extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingAbsolutePositionAnchored pos;
	private ElementSettingAnchor anchor;
	private ElementSettingSlider directionScaling;
	private ElementSettingBoolean showNotches;
	private ElementSettingSlider scale;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		anchor.value = Position.TOP_LEFT.getFlag();
		pos.x = 5;
		pos.y = 5;
		directionScaling.value = 50.0;
		showNotches.value = true;
		scale.value = 100.0;
	}
	
	@Override
	public String getName() {
		return "compass";
	}
	
	public static final double degreesPerRadian = 180.0 / Math.PI;
	
	public static int nColor = RenderUtil.colorRGB(255, 0, 0);
	public static int ewColor = RenderUtil.colorRGB(255, 255, 255);
	public static int sColor = RenderUtil.colorRGB(0, 0, 255);
	
	private int[] notchX = new int[9];
	
	public ExtraGuiElementCompass() {
		//modes = new String[] {"simple", "fancy", "fancier"};
		//defaultMode = 2;
		
		int x = 0;
		for(double i = 0.1; i < 0.9; i += 0.1) {
			notchX[x] = (int) (Math.asin(i) / Math.PI * 180);
			x++;
		}
		
		//this.settings.add(fanciness = new ElementSettingMode("fanciness", new String[] {"simple", "fancy", "fancier"}));
		this.settings.add(new ElementSettingDivider("position"));
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.anchor = new ElementSettingAnchor("anchor");
		this.settings.add(pos = new ElementSettingAbsolutePositionAnchored("position", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(directionScaling = new ElementSettingSlider("scaledDirections", 0, 100) {
			@Override
			public String getSliderText() {
				return FormatUtil.translatePre("menu.settingButton", this.getLocalizedName(), FormatUtil.translatePre("strings.percent", String.valueOf((int) this.value)));
			}
		});
		directionScaling.accuracy = 1;
		this.settings.add(scale = new ElementSettingSlider("scale", 25, 200) {
			@Override
			public String getSliderText() {
				return FormatUtil.translatePre("menu.settingButton", this.getLocalizedName(), FormatUtil.translatePre("strings.percent", String.valueOf((int) this.value)));
			}
		});
		directionScaling.accuracy = 1;
		this.settings.add(showNotches = new ElementSettingBoolean("showNotches"));
	}
	
	//short nOpacity = 0, wOpacity = 0, sOpacity = 0, eOpacity = 0;
	//int nX = 0, wX = 0, sX = 0, eX = 0;
	
	public void update(Minecraft mc) {}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		int x = posMode.index == 0 ? resolution.getScaledWidth() / 2 - 90 : pos.x;
		int y = posMode.index == 0 ? 18 : pos.y;
		return new Bounds((int) Math.round(x - (90 * (scale.value - 100.0) / 100.0)), y, (int) Math.round(180 * scale.value / 100.0), (int) Math.round(12 * scale.value / 100.0));
	}
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		//int fanciness = this.fanciness.index;
		
		double offsetQuarter = Math.toRadians(90);
		double transform = Math.toRadians(mc.thePlayer.rotationYaw);
		
		short nOpacity = (short) Math.abs(Math.sin(transform / 2) * 255);
		short wOpacity = (short) Math.abs(Math.sin((transform + offsetQuarter) / 2) * 255);
		short sOpacity = (short) Math.abs(Math.sin((transform + offsetQuarter * 2) / 2) * 255);
		short eOpacity = (short) Math.abs(Math.sin((transform - offsetQuarter) / 2) * 255);
		
		int nX = (int) (Math.sin(transform + offsetQuarter * 2) * 100);
		int eX = (int) (Math.sin(transform + offsetQuarter) * 100);
		int sX = (int) (Math.sin(transform) * 100);
		int wX = (int) (Math.sin(transform - offsetQuarter) * 100);
		
		if(posMode.index == 1) {
			this.pos.update(resolution, this.getBounds(resolution));
		}
		int x = posMode.index == 0 ? resolution.getScaledWidth() / 2 - 90 : pos.x;
		int y = posMode.index == 0 ? 18 : pos.y;
		
		///
		GL11.glPushMatrix();
		///
		GL11.glTranslatef(x + 90, y, 0);
		GL11.glScaled(scale.value / 100, scale.value / 100, 1);
		GL11.glTranslatef(-x - 90, -y, 0);
		
		RenderUtil.drawRect(x, y, x + 180, y + 12, RenderUtil.colorARGB(170, 0, 0, 0));
		RenderUtil.drawRect(x + 50, y, x + 130, y + 12, RenderUtil.colorARGB(85, 85, 85, 85));
		
		glEnable(GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		mc.mcProfiler.startSection("text");
		
		int maxScale = 4;
		float factor = 100 / maxScale;
		
		if(nOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(x + 90 - nX, y + 2, 0.0F);
				
				float size = (float) nOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);
				
				mc.ingameGUI.drawCenteredString(mc.fontRendererObj, "N", 0, 0, RenderUtil.colorARGB(nOpacity, 255, 0, 0));
			}
			glPopMatrix();
		}
		if(eOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(x + 90 - eX, y + 2, 0.0F);

				float size = (float) eOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);
				
				mc.ingameGUI.drawCenteredString(mc.fontRendererObj, "E", 0, 0, RenderUtil.colorARGB(eOpacity, 255, 255, 255));
			}
			glPopMatrix();
		}
		if(sOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(x + 90 - sX, y + 2, 0.0F);
				
				float size = (float) sOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);
				
				mc.ingameGUI.drawCenteredString(mc.fontRendererObj, "S", 0, 0, RenderUtil.colorARGB(sOpacity, 0, 0, 255));
			}
			glPopMatrix();
		}
		if(wOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(x + 90 - wX, y + 2, 0.0F);
				
				float size = (float) wOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);
				
				mc.ingameGUI.drawCenteredString(mc.fontRendererObj, "W", 0, 0, RenderUtil.colorARGB(wOpacity, 255, 255, 255)); // 16777216 = 256^3
			}
			glPopMatrix();
		}
		mc.mcProfiler.endSection();
		
		mc.mcProfiler.startSection("notches");
		
		int largeNotch = RenderUtil.colorRGB(255, 255, 255);
		
		if(showNotches.value) {
			largeNotch = RenderUtil.colorRGB(255, 0, 0);
			
			for(int loc : notchX) {
				RenderUtil.drawRect(x + loc - 1, y - 2, x + loc, y + 4, RenderUtil.colorRGB(255, 255, 255));
				RenderUtil.drawRect(x - loc + 180, y - 2, x - loc + 181, y + 4, RenderUtil.colorRGB(255, 255, 255));
			}
		}
		
		RenderUtil.drawRect(x + 89, y - 3, x + 90, y + 4, largeNotch);
		RenderUtil.drawRect(x, y - 3, x + 1, y + 4, largeNotch);
		RenderUtil.drawRect(x + 180, y - 3, x + 179, y + 4, largeNotch);
		
		mc.mcProfiler.endSection();
		
		///
		GL11.glPopMatrix();
		///
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}
