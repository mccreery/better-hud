package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.element.settings.SettingPositionHorizontal;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class ArrowCount extends HudElement {
	private SettingMode posMode;
	private SettingPosition pos;
	private SettingAnchoredPosition pos2;
	private SettingAnchor anchor;
	private SettingMode mode;
	
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
	
	public ArrowCount() {
		super("arrowCount");
		this.settings.add(new Divider("position"));
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new SettingPositionHorizontal("position", Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_RIGHT)) {
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
		this.settings.add(new Legend("arrowCountNotice"));
		this.settings.add(new Divider("misc"));
		this.settings.add(mode = new SettingMode("mode", new String[] {"side", "overlay"}));
	}
	
	public void update() {}
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
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
	
	private boolean isHoldingBow(EntityPlayer player) {
		return (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == Items.BOW)
			|| (player.getHeldItemOffhand()  != null && player.getHeldItemOffhand().getItem()  == Items.BOW);
	}
	
	private int arrowCount(EntityPlayer player) {
		int count = 0;

		for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);

			if(stack.getItem() instanceof ItemArrow) {
				count += stack.getCount();
			}
		}
		return count;
	}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		if(mode.getValue().equals("side") && !isHoldingBow(MC.player)) return;

		String arrowsDisplay = String.valueOf(arrowCount(MC.player));

		// TODO very ugly
		if(mode.getValue().equals("side")) {
			MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			if(posMode.index == 0) {
				if(pos.value == Position.MIDDLE_LEFT) {
					RenderUtil.renderItem(MC.getRenderItem(), MC.fontRenderer, MC.getTextureManager(), arrow, event.getResolution().getScaledWidth() / 2 - 111, event.getResolution().getScaledHeight() - 18);
					glPushMatrix();
					glTranslatef(0.0f, 0.0f, 151.0f);
					MC.ingameGUI.drawString(MC.fontRenderer, arrowsDisplay, event.getResolution().getScaledWidth() / 2 - 111, event.getResolution().getScaledHeight() - 18, Colors.WHITE);
					glPopMatrix();
				} else if(pos.value == Position.MIDDLE_RIGHT) {
					RenderUtil.renderItem(MC.getRenderItem(), MC.fontRenderer, MC.getTextureManager(), arrow, event.getResolution().getScaledWidth() / 2 + 95, event.getResolution().getScaledHeight() - 18);
					glPushMatrix();
					glTranslatef(0.0f, 0.0f, 151.0f);
					MC.ingameGUI.drawString(MC.fontRenderer, arrowsDisplay, event.getResolution().getScaledWidth() / 2 + 95, event.getResolution().getScaledHeight() - 18, Colors.WHITE);
					glPopMatrix();
				}
			} else {
				pos2.update(event.getResolution(), getBounds(event.getResolution()));

				RenderUtil.renderItem(MC.getRenderItem(), MC.fontRenderer, MC.getTextureManager(), arrow, pos2.x, pos2.y);
				glPushMatrix();
				glTranslatef(0.0f, 0.0f, 151.0f);
				MC.ingameGUI.drawString(MC.fontRenderer, arrowsDisplay, pos2.x, pos2.y, Colors.WHITE);
				glPopMatrix();
			}
		} else {
			int center = event.getResolution().getScaledWidth() / 2;
			int y = event.getResolution().getScaledHeight() - 1;

			for(int i = 0; i < 9; i++) {
				ItemStack stack = MC.player.inventory.getStackInSlot(i);
				if(stack != null && stack.getItem() == Items.BOW) {
					drawHotbarText(arrowsDisplay, center - 71 + (i * 20), y);
				}
			}
			ItemStack stack = MC.player.inventory.getStackInSlot(40);
			if(stack != null && stack.getItem() == Items.BOW) {
				drawHotbarText(arrowsDisplay, center - 100, y);
			}
		}
	}
	
	private void drawHotbarText(String text, int x, int y) {
		glPushMatrix();
		glTranslatef(0.0f, 0.0f, 151.0f);
		MC.ingameGUI.drawString(MC.fontRenderer, text, x - MC.fontRenderer.getStringWidth(text), y - MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
		glPopMatrix();
	}
	
	private static final ItemStack arrow = new ItemStack(Items.ARROW, 1);

	@Override
	public boolean shouldProfile() {
		return true;
	}
}