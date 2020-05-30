package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class HelmetOverlay extends OverrideElement {
	private static final ResourceLocation PUMPKIN_BLUR_TEX_PATH = new ResourceLocation("textures/misc/pumpkinblur.png");

	public HelmetOverlay() {
		super("helmetOverlay");
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(Integer.MIN_VALUE);
	}

	@Override
	protected ElementType getType() {
		return ElementType.HELMET;
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !Minecraft.getMinecraft().player.inventory.armorItemInSlot(3).isEmpty();
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		ItemStack stack = Minecraft.getMinecraft().player.inventory.armorItemInSlot(3);
		Item item = stack.getItem();

		if(item == Item.getItemFromBlock(Blocks.PUMPKIN)) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(PUMPKIN_BLUR_TEX_PATH);
			GlUtil.drawRect(MANAGER.getScreen(), new Rect(256, 256), Color.RED);
			Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
		} else {
			item.renderHelmetOverlay(stack, Minecraft.getMinecraft().player, new ScaledResolution(Minecraft.getMinecraft()), context.getPartialTicks());
		}
		return MANAGER.getScreen();
	}
}
