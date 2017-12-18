package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingPositionHorizontal;
import tk.nukeduck.hud.element.settings.ElementSettingText;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class ExtraGuiElementPotionBar extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingPosition pos;
	private ElementSettingAbsolutePositionAnchored pos2;
	private ElementSettingAnchor anchor;
	public ElementSettingBoolean disableDefault;
	
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
	
	@Override
	public String getName() {
		return "potionBar";
	}
	
	public ExtraGuiElementPotionBar() {
		//modes = new String[] {"left", "center", "right"};
		//defaultMode = 1;
		inventory = new ResourceLocation("textures/gui/container/inventory.png");
		this.settings.add(new ElementSettingText("potionsUseless"));
		this.settings.add(disableDefault = new ElementSettingBoolean("disableDefault"));
		this.settings.add(new ElementSettingDivider("position"));
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new ElementSettingPositionHorizontal("position", Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_CENTER, Position.MIDDLE_RIGHT)) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.anchor = new ElementSettingAnchor("anchor");
		this.settings.add(pos2 = new ElementSettingAbsolutePositionAnchored("position2", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
	}
	
	public void update(Minecraft mc) {}
	
	private Bounds bounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
	}
	
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		boolean right = posMode.index == 0 && pos.value == Position.MIDDLE_RIGHT;
		boolean left = pos.value == Position.MIDDLE_LEFT;
		
		int amount = mc.player.getActivePotionEffects().size();
		
		glEnable(GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// Render potion icons
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(inventory);
		//int it = 0;
		
		int x = right ? resolution.getScaledWidth() - amount * 16 - 5 : left ? 5 : resolution.getScaledWidth() / 2 + 5;
		int y = right ? layoutManager.get(Position.TOP_RIGHT) + mc.fontRenderer.FONT_HEIGHT + 2 : left ? layoutManager.get(Position.TOP_LEFT) + mc.fontRenderer.FONT_HEIGHT + 2 : resolution.getScaledHeight() / 2 - 23;
		int xOffset = 0;
		
		if(posMode.index == 1) {
			Bounds b = new Bounds(0, 0, amount * 16 + 2, 18);
			this.pos2.update(resolution, b);
			x = pos2.x;
			y = pos2.y;
		}
		
		for(Iterator<PotionEffect> i = mc.player.getActivePotionEffects().iterator(); i.hasNext(); xOffset += 16) {
			PotionEffect pe = i.next();
			
			int iIndex = pe.getPotion().getStatusIconIndex();
			glColor4f(1.0F, 1.0F, 1.0F, ((float) pe.getDuration() / 600F));
			
			mc.ingameGUI.drawTexturedModalRect(x + xOffset, y, 18 * (iIndex % 8), 198 + ((iIndex >> 3) * 18), 18, 18); // >> 3 = / 8
		}
		// Render potion potencies
		xOffset = 4;
		for(Iterator<PotionEffect> i = mc.player.getActivePotionEffects().iterator(); i.hasNext(); xOffset += 16) {
			mc.ingameGUI.drawString(mc.fontRenderer, I18n.format("potion.potency." + i.next().getAmplifier()).replace("potion.potency.", ""), x + xOffset, y - mc.fontRenderer.FONT_HEIGHT - 2, Colors.WHITE);
		}
		
		this.bounds = new Bounds(x, y, amount * 16 + 2, 18);
		if(amount == 0) bounds.setWidth(0);
		
		if(right) {
			//rightHeight = amount > 0 ? 20 + fr.FONT_HEIGHT : -5;
			//leftHeight = -5;
			if(amount > 0) layoutManager.add(20 + mc.fontRenderer.FONT_HEIGHT, Position.TOP_RIGHT);
		} else if(left) {
			//leftHeight = amount > 0 ? 20 + fr.FONT_HEIGHT : -5;
			//rightHeight = -5;
			if(amount > 0) layoutManager.add(20 + mc.fontRenderer.FONT_HEIGHT, Position.TOP_LEFT);
		}
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}