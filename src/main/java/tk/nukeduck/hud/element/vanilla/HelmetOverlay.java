package tk.nukeduck.hud.element.vanilla;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.util.Bounds;

public class HelmetOverlay extends OverrideElement {
	private static final ResourceLocation PUMPKIN_BLUR_TEX_PATH = new ResourceLocation("textures/misc/pumpkinblur.png");

	public HelmetOverlay() {
		super("helmetOverlay");
	}

	@Override
	protected ElementType getType() {
		return ElementType.HELMET;
	}

	@Override
	protected Bounds render(Event event) {
		ItemStack stack = MC.player.inventory.armorItemInSlot(3);

		if(MC.gameSettings.thirdPersonView == 0 && !stack.isEmpty()) {
			Item item = stack.getItem();

			if(item == Item.getItemFromBlock(Blocks.PUMPKIN)) {
				GlStateManager.disableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				MC.getTextureManager().bindTexture(PUMPKIN_BLUR_TEX_PATH);
				Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, MANAGER.getResolution().x, MANAGER.getResolution().y, MANAGER.getResolution().x, MANAGER.getResolution().y);
			} else {
				item.renderHelmetOverlay(stack, MC.player, MANAGER.getScaledResolution(), ((RenderGameOverlayEvent)event).getPartialTicks());
			}
		}
		return null;
	}
}
