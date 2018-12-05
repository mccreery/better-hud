package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.mode.GlMode;
import tk.nukeduck.hud.util.mode.TextureMode;

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
