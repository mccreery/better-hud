package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingPositionHorizontal;
import tk.nukeduck.hud.element.settings.ElementSettingText;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementArrowCount extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingPosition pos;
	private ElementSettingAbsolutePositionAnchored pos2;
	private ElementSettingAnchor anchor;
	private ElementSettingMode mode;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.value = Position.MIDDLE_RIGHT;
		anchor.value = Position.TOP_LEFT.getFlag();
		pos2.x = 5;
		pos2.y = 5;
		mode.index = 0;
	}
	
	@Override
	public String getName() {
		return "arrowCount";
	}
	
	public ExtraGuiElementArrowCount() {
		this.settings.add(new ElementSettingDivider("position"));
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new ElementSettingPositionHorizontal("position", Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_RIGHT)) {
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
		this.settings.add(new ElementSettingText("arrowCountNotice"));
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(mode = new ElementSettingMode("mode", new String[] {"side", "overlay"}));
	}
	
	public void update(Minecraft mc) {}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		//System.out.println(resolution.getScaledWidth());
		if(mode.getValue().equals("side")) {
			if(posMode.index == 0) {
				int x = pos.value == Position.MIDDLE_LEFT ? resolution.getScaledWidth() / 2 - 111 : resolution.getScaledWidth() / 2 + 95;
				int y = resolution.getScaledHeight() - 18;
				return new Bounds(x, y, 16, 16);
			} else {
				return new Bounds(pos2.x, pos2.y, 16, 16);
			}
		} else {
			return new Bounds(resolution.getScaledWidth() / 2 - 90, resolution.getScaledHeight() - 20, 180, 20);
		}
	}
	
	int x = 0, y = 0;
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		int arrows = 0;
		for(ItemStack[] inventory : new ItemStack[][] {mc.thePlayer.inventory.mainInventory, mc.thePlayer.inventory.offHandInventory}) {
			for(ItemStack i : inventory) {
				if(i != null && i.getItem().equals(Items.ARROW)) {
					arrows += i.stackSize;
				}
			}
		} 

		if(mode.getValue().equals("side")) {
			if(mc.thePlayer.getHeldItemMainhand() != null && mc.thePlayer.getHeldItemMainhand().getItem().equals(Items.BOW)) {
				mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				if(posMode.index == 0) {
					if(pos.value == Position.MIDDLE_LEFT) {
						RenderUtil.renderItem(mc.getRenderItem(), mc.fontRendererObj, mc.getTextureManager(), arrow, resolution.getScaledWidth() / 2 - 111, resolution.getScaledHeight() - 18);
						glPushMatrix();
						glTranslatef(0.0f, 0.0f, 151.0f);
						mc.ingameGUI.drawString(mc.fontRendererObj, arrows + "", resolution.getScaledWidth() / 2 - 111, resolution.getScaledHeight() - 18, RenderUtil.colorRGB(255, 255, 255));
						glPopMatrix();
					} else if(pos.value == Position.MIDDLE_RIGHT) {
						RenderUtil.renderItem(mc.getRenderItem(), mc.fontRendererObj, mc.getTextureManager(), arrow, resolution.getScaledWidth() / 2 + 95, resolution.getScaledHeight() - 18);
						glPushMatrix();
						glTranslatef(0.0f, 0.0f, 151.0f);
						mc.ingameGUI.drawString(mc.fontRendererObj, arrows + "", resolution.getScaledWidth() / 2 + 95, resolution.getScaledHeight() - 18, RenderUtil.colorRGB(255, 255, 255));
						glPopMatrix();
					}
				} else {
					pos2.update(resolution, this.getBounds(resolution));

					RenderUtil.renderItem(mc.getRenderItem(), mc.fontRendererObj, mc.getTextureManager(), arrow, pos2.x, pos2.y);
					glPushMatrix();
					glTranslatef(0.0f, 0.0f, 151.0f);
					mc.ingameGUI.drawString(mc.fontRendererObj, arrows + "", pos2.x, pos2.y, RenderUtil.colorRGB(255, 255, 255));
					glPopMatrix();
				}
			}
		} else {
			for(int i = 0; i < 9; i++) {
				ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
				if(stack != null && stack.getItem() instanceof ItemBow) {
					glPushMatrix();
					glTranslatef(0.0f, 0.0f, 151.0f);
					mc.ingameGUI.drawString(mc.fontRendererObj, arrows + "", resolution.getScaledWidth() / 2 - 71 + (i * 20) - mc.fontRendererObj.getStringWidth(arrows + ""), resolution.getScaledHeight() - 1 - mc.fontRendererObj.FONT_HEIGHT, RenderUtil.colorRGB(255, 255, 255));
					glPopMatrix();
				}
			}
		}
	}
	
	private static final ItemStack arrow = new ItemStack(Items.ARROW, 1);

	@Override
	public boolean shouldProfile() {
		return true;
	}
}