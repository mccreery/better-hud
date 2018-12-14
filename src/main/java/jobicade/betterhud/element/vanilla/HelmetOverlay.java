package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.util.Bounds;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.mode.GlMode;
import jobicade.betterhud.util.mode.TextureMode;

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
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && MC.gameSettings.thirdPersonView == 0 && !MC.player.inventory.armorItemInSlot(3).isEmpty();
	}

	@Override
	protected Bounds render(Event event) {
		ItemStack stack = MC.player.inventory.armorItemInSlot(3);
		Item item = stack.getItem();

		if(item == Item.getItemFromBlock(Blocks.PUMPKIN)) {
			GlMode.push(new TextureMode(PUMPKIN_BLUR_TEX_PATH));
			GlUtil.drawTexturedModalRect(MANAGER.getScreen(), new Bounds(256, 256));
			GlMode.pop();
		} else {
			item.renderHelmetOverlay(stack, MC.player, MANAGER.getScaledResolution(), getPartialTicks(event));
		}
		return MANAGER.getScreen();
	}
}
