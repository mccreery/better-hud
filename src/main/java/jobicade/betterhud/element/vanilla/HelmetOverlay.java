package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.GlMode;
import jobicade.betterhud.render.TextureMode;

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
	protected Rect render(Event event) {
		ItemStack stack = MC.player.inventory.armorItemInSlot(3);
		Item item = stack.getItem();

		if(item == Item.getItemFromBlock(Blocks.PUMPKIN)) {
			GlMode.push(new TextureMode(PUMPKIN_BLUR_TEX_PATH));
			GlUtil.drawRect(MANAGER.getScreen(), new Rect(256, 256), Color.RED);
			GlMode.pop();
		} else {
			item.renderHelmetOverlay(stack, MC.player, new ScaledResolution(MC), getPartialTicks(event));
		}
		return MANAGER.getScreen();
	}
}
