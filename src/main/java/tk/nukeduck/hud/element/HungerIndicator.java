package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Textures;

public class HungerIndicator extends HudElement {
	private SettingMode posMode;
	private SettingPosition pos;
	private SettingAnchoredPosition pos2;
	private SettingAnchor anchor;
	private SettingSlider maxLimit;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.value = Position.BOTTOM_CENTER;
		anchor.value = Position.TOP_LEFT.getFlag();
		pos2.x = 5;
		pos2.y = 5;
		maxLimit.value = 9.5;
	}
	
	public HungerIndicator() {
		super("foodIndicator");
		this.settings.add(new Divider("position"));
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new SettingPosition("position", Position.combine(Position.MIDDLE_CENTER, Position.BOTTOM_CENTER)) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.anchor = new SettingAnchor("anchor");
		this.settings.add(pos2 = new SettingAnchoredPosition("position2", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new Divider("misc"));
		this.settings.add(maxLimit = new SettingSlider("maxLimit", 0, 10));
		maxLimit.accuracy = 0.5;
	}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		int x, y;
		if(posMode.index == 1) {
			x = pos2.x;
			y = pos2.y;
		} else if(pos.value == Position.MIDDLE_CENTER) {
			x = resolution.getScaledWidth() / 2 + 5;
			y = resolution.getScaledHeight() / 2 + 5;
			//RenderUtil.renderItemAlpha(ri, fr, mc.getTextureManager(), beef, width / 2 + 5, height / 2 + 5, );
		} else {
			x = resolution.getScaledWidth() / 2 + 75;
			y = resolution.getScaledHeight() - 56;
			//RenderUtil.renderItemAlpha(ri, fr, mc.getTextureManager(), beef, width / 2 + 75, height - 56, Math.sin(System.currentTimeMillis() % ((mc.thePlayer.getFoodStats().getFoodLevel() + 1) * 100) / 1050.0 * Math.PI));
		}
		return new Bounds(x, y, 16, 16);
	}
	
	public void update() {}
	
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		double foodLevel = MC.player.getFoodStats().getFoodLevel();
		double foodMax = this.maxLimit.value * 2.0;
		if(foodLevel <= foodMax) {
			double speed = (foodMax - foodLevel) / foodMax * 50.0 + 2.0;
			
			double alpha = Math.sin(System.currentTimeMillis() / 3000.0 * speed) + 1.0 / 2.0;
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4d(1.0, 1.0, 1.0, alpha);
			MC.getTextureManager().bindTexture(Textures.iconsHud);
			int x = 0, y = 0;
			
			if(posMode.index == 1) {
				this.pos2.update(event.getResolution(), this.getBounds(event.getResolution()));
				x = pos2.x;
				y = pos2.y;
			} else if(pos.value == Position.MIDDLE_CENTER) {
				x = event.getResolution().getScaledWidth() / 2 + 5;
				y = event.getResolution().getScaledHeight() / 2 + 5;
				//RenderUtil.renderItemAlpha(ri, fr, mc.getTextureManager(), beef, width / 2 + 5, height / 2 + 5, );
			} else {
				x = event.getResolution().getScaledWidth() / 2 + 75;
				y = event.getResolution().getScaledHeight() - 56;
				//RenderUtil.renderItemAlpha(ri, fr, mc.getTextureManager(), beef, width / 2 + 75, height - 56, Math.sin(System.currentTimeMillis() % ((mc.thePlayer.getFoodStats().getFoodLevel() + 1) * 100) / 1050.0 * Math.PI));
			}
			MC.ingameGUI.drawTexturedModalRect(x, y, 0, 64, 16, 16);
		}
	}
	
	//private static final ItemStack beef = new ItemStack(Items.COOKED_BEEF);

	@Override
	public boolean shouldProfile() {
		return true;
	}
}