package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.WIDGETS;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class Hotbar extends OverrideElement {
	public Hotbar() {
		super("hotbar");
	}

	@Override
	protected ElementType getType() {
		return ElementType.HOTBAR;
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && MC.getRenderViewEntity() instanceof EntityPlayer;
	}

	@Override
	protected Bounds render(Event event) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		MC.getTextureManager().bindTexture(WIDGETS);

		Bounds barTexture = new Bounds(182, 22);
		Bounds selected = new Bounds(0, 22, 24, 24);

		Bounds bar = MANAGER.position(Direction.SOUTH, new Bounds(barTexture), true, 2);

		EntityPlayer player = (EntityPlayer)MC.getRenderViewEntity();

		GlUtil.enableBlendTranslucent();

		GlUtil.drawTexturedModalRect(bar.position, barTexture);
		GlUtil.drawTexturedModalRect(bar.position.add(-1 + player.inventory.currentItem * 20, -1), selected);

		GlUtil.enableBlendTranslucent();
		GlStateManager.enableRescaleNormal();
		RenderHelper.enableGUIStandardItemLighting();

		for (int i = 0; i < 9; i++) {
			renderHotbarItem(bar.position.add(3 + i*20, 3), ((RenderGameOverlayEvent)event).getPartialTicks(), player, player.inventory.mainInventory.get(i));
		}

		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		return bar;
	}

	protected void renderHotbarItem(Point position, float partialTicks, EntityPlayer player, ItemStack stack) {
		if(!stack.isEmpty()) {
			float animationTicks = (float)stack.getAnimationsToGo() - partialTicks;

			if(animationTicks > 0.0F) {
				float factor = 1 + animationTicks / 5;

				GlStateManager.pushMatrix();
				GlStateManager.translate(position.x + 8, position.y + 12, 0);
				GlStateManager.scale(1 / factor, (factor + 1) / 2, 1);
				GlStateManager.translate(-(position.x + 8), -(position.y + 12), 0.0F);
			}

			MC.getRenderItem().renderItemAndEffectIntoGUI(player, stack, position.x, position.y);

			if(animationTicks > 0.0F) {
				GlStateManager.popMatrix();
			}

			MC.getRenderItem().renderItemOverlays(MC.fontRenderer, stack, position.x, position.y);
		}
	}
}
