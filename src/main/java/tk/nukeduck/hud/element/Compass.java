package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class Compass extends HudElement {
	private SettingMode posMode;
	private SettingAnchoredPosition pos;
	private SettingAnchor anchor;
	private SettingSlider directionScaling;
	private SettingBoolean showNotches;
	private SettingSlider scale;
	
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
	
	public static final double degreesPerRadian = 180.0 / Math.PI;
	
	public static final int nColor  = Colors.fromRGB(255, 0, 0);
	public static final int ewColor = Colors.fromRGB(255, 255, 255);
	public static final int sColor  = Colors.fromRGB(0, 0, 255);
	
	private int[] notchX = new int[9];
	
	public Compass() {
		super("compass");
		//modes = new String[] {"simple", "fancy", "fancier"};
		//defaultMode = 2;
		
		int x = 0;
		for(double i = 0.1; i < 0.9; i += 0.1) {
			notchX[x] = (int) (Math.asin(i) / Math.PI * 180);
			x++;
		}
		
		//this.settings.add(fanciness = new ElementSettingMode("fanciness", new String[] {"simple", "fancy", "fancier"}));
		this.settings.add(new Divider("position"));
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.anchor = new SettingAnchor("anchor");
		this.settings.add(pos = new SettingAnchoredPosition("position", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new Divider("misc"));
		this.settings.add(directionScaling = new SettingSlider("scaledDirections", 0, 100) {
			@Override
			public String getSliderText() {
				return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format("betterHud.strings.percent", String.valueOf((int) this.value)));
			}
		});
		directionScaling.accuracy = 1;
		this.settings.add(scale = new SettingSlider("scale", 25, 200) {
			@Override
			public String getSliderText() {
				return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format("betterHud.strings.percent", String.valueOf((int) this.value)));
			}
		});
		directionScaling.accuracy = 1;
		this.settings.add(showNotches = new SettingBoolean("showNotches"));
	}
	
	//short nOpacity = 0, wOpacity = 0, sOpacity = 0, eOpacity = 0;
	//int nX = 0, wX = 0, sX = 0, eX = 0;
	
	public void update() {}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		int x = posMode.index == 0 ? resolution.getScaledWidth() / 2 - 90 : pos.x;
		int y = posMode.index == 0 ? 18 : pos.y;
		return new Bounds((int) Math.round(x - (90 * (scale.value - 100.0) / 100.0)), y, (int) Math.round(180 * scale.value / 100.0), (int) Math.round(12 * scale.value / 100.0));
	}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		//int fanciness = this.fanciness.index;
		
		double offsetQuarter = Math.toRadians(90);
		double transform = Math.toRadians(MC.player.rotationYaw);
		
		short nOpacity = (short) Math.abs(Math.sin(transform / 2) * 255);
		short wOpacity = (short) Math.abs(Math.sin((transform + offsetQuarter) / 2) * 255);
		short sOpacity = (short) Math.abs(Math.sin((transform + offsetQuarter * 2) / 2) * 255);
		short eOpacity = (short) Math.abs(Math.sin((transform - offsetQuarter) / 2) * 255);
		
		int nX = (int) (Math.sin(transform + offsetQuarter * 2) * 100);
		int eX = (int) (Math.sin(transform + offsetQuarter) * 100);
		int sX = (int) (Math.sin(transform) * 100);
		int wX = (int) (Math.sin(transform - offsetQuarter) * 100);
		
		if(posMode.index == 1) {
			this.pos.update(event.getResolution(), this.getBounds(event.getResolution()));
		}
		int x = posMode.index == 0 ? event.getResolution().getScaledWidth() / 2 - 90 : pos.x;
		int y = posMode.index == 0 ? 18 : pos.y;
		
		///
		GL11.glPushMatrix();
		///
		GL11.glTranslatef(x + 90, y, 0);
		GL11.glScaled(scale.value / 100, scale.value / 100, 1);
		GL11.glTranslatef(-x - 90, -y, 0);
		
		RenderUtil.renderQuad(Tessellator.getInstance(), x,      y, 180, 12, Colors.fromARGB(170, 0, 0, 0));
		RenderUtil.renderQuad(Tessellator.getInstance(), x + 50, y, 80,  12, Colors.fromARGB(85, 85, 85, 85));
		
		glEnable(GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		MC.mcProfiler.startSection("text");
		
		int maxScale = 4;
		float factor = 100 / maxScale;
		
		if(nOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(x + 90 - nX, y + 2, 0.0F);
				
				float size = (float) nOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);
				
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, "N", 0, 0, Colors.fromARGB(nOpacity, 255, 0, 0));
			}
			glPopMatrix();
		}
		if(eOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(x + 90 - eX, y + 2, 0.0F);

				float size = (float) eOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);
				
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, "E", 0, 0, Colors.fromARGB(eOpacity, 255, 255, 255));
			}
			glPopMatrix();
		}
		if(sOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(x + 90 - sX, y + 2, 0.0F);
				
				float size = (float) sOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);
				
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, "S", 0, 0, Colors.fromARGB(sOpacity, 0, 0, 255));
			}
			glPopMatrix();
		}
		if(wOpacity > 10) {
			glPushMatrix(); {
				glTranslatef(x + 90 - wX, y + 2, 0.0F);
				
				float size = (float) wOpacity / 128F;
				float finalSize = Math.max(0, (float) (directionScaling.value / factor) * (size - 1) + 1);
				glScalef(finalSize, finalSize, 1.0F);
				
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, "W", 0, 0, Colors.fromARGB(wOpacity, 255, 255, 255)); // 16777216 = 256^3
			}
			glPopMatrix();
		}
		MC.mcProfiler.endSection();
		
		MC.mcProfiler.startSection("notches");
		
		int largeNotch = Colors.WHITE;
		if(showNotches.value) {
			largeNotch = Colors.RED;
			
			for(int loc : notchX) {
				RenderUtil.renderQuad(x + loc - 1, y - 2, 1, 6, Colors.WHITE);
				RenderUtil.renderQuad(x - loc + 180, y - 2, 1, 6, Colors.WHITE);
			}
		}
		
		RenderUtil.renderQuad(x+89,  y-3, 1, 7, largeNotch);
		RenderUtil.renderQuad(x,     y-3, 1, 7, largeNotch);
		RenderUtil.renderQuad(x+179, y-3, 1, 7, largeNotch);
		
		MC.mcProfiler.endSection();
		
		///
		GL11.glPopMatrix();
		///
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}
