package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnable;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.element.settings.SettingPositionHorizontal;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class PotionBar extends HudElement {
	private SettingMode posMode;
	private SettingPosition pos;
	private SettingAnchoredPosition pos2;
	private SettingAnchor anchor;
	public SettingBoolean disableDefault;
	
	public static ResourceLocation inventory;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.value = Position.MIDDLE_RIGHT;
		anchor.value = Position.TOP_LEFT.getFlag();
		pos2.x = 5;
		pos2.y = 5;
	}
	
	public PotionBar() {
		super("potionBar");
		//modes = new String[] {"left", "center", "right"};
		//defaultMode = 1;
		inventory = new ResourceLocation("textures/gui/container/inventory.png");
		this.settings.add(new Legend("potionsUseless"));
		this.settings.add(disableDefault = new SettingBoolean("disableDefault"));
		this.settings.add(new Divider("position"));
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new SettingPositionHorizontal("position", Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_CENTER, Position.MIDDLE_RIGHT)) {
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
	}
	
	public void update() {}
	
	private Bounds bounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return bounds;
	}
	
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		boolean right = posMode.index == 0 && pos.value == Position.MIDDLE_RIGHT;
		boolean left = pos.value == Position.MIDDLE_LEFT;
		
		int amount = MC.player.getActivePotionEffects().size();
		
		glEnable(GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// Render potion icons
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(inventory);
		//int it = 0;
		
		int x = right ? event.getResolution().getScaledWidth() - amount * 16 - 5 : left ? 5 : event.getResolution().getScaledWidth() / 2 + 5;
		int y = right ? layoutManager.get(Position.TOP_RIGHT) + MC.fontRenderer.FONT_HEIGHT + 2 : left ? layoutManager.get(Position.TOP_LEFT) + MC.fontRenderer.FONT_HEIGHT + 2 : event.getResolution().getScaledHeight() / 2 - 23;
		int xOffset = 0;
		
		if(posMode.index == 1) {
			Bounds b = new Bounds(0, 0, amount * 16 + 2, 18);
			this.pos2.update(event.getResolution(), b);
			x = pos2.x;
			y = pos2.y;
		}
		
		for(Iterator<PotionEffect> i = MC.player.getActivePotionEffects().iterator(); i.hasNext(); xOffset += 16) {
			PotionEffect pe = i.next();
			
			int iIndex = pe.getPotion().getStatusIconIndex();
			glColor4f(1.0F, 1.0F, 1.0F, ((float) pe.getDuration() / 600F));
			
			MC.ingameGUI.drawTexturedModalRect(x + xOffset, y, 18 * (iIndex % 8), 198 + ((iIndex >> 3) * 18), 18, 18); // >> 3 = / 8
		}
		// Render potion potencies
		xOffset = 4;
		for(Iterator<PotionEffect> i = MC.player.getActivePotionEffects().iterator(); i.hasNext(); xOffset += 16) {
			MC.ingameGUI.drawString(MC.fontRenderer, I18n.format("potion.potency." + i.next().getAmplifier()).replace("potion.potency.", ""), x + xOffset, y - MC.fontRenderer.FONT_HEIGHT - 2, Colors.WHITE);
		}
		
		this.bounds = new Bounds(x, y, amount * 16 + 2, 18);
		if(amount == 0) bounds.setWidth(0);
		
		if(right) {
			//rightHeight = amount > 0 ? 20 + fr.FONT_HEIGHT : -5;
			//leftHeight = -5;
			if(amount > 0) layoutManager.add(20 + MC.fontRenderer.FONT_HEIGHT, Position.TOP_RIGHT);
		} else if(left) {
			//leftHeight = amount > 0 ? 20 + fr.FONT_HEIGHT : -5;
			//rightHeight = -5;
			if(amount > 0) layoutManager.add(20 + MC.fontRenderer.FONT_HEIGHT, Position.TOP_LEFT);
		}
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}