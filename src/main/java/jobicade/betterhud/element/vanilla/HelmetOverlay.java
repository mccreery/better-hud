package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class HelmetOverlay extends OverlayElement {
    private static final ResourceLocation PUMPKIN_BLUR_TEX_PATH = new ResourceLocation("textures/misc/pumpkinblur.png");

    public HelmetOverlay() {
        super("helmetOverlay");
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return ForgeIngameGui.renderHelmet
            && !OverlayHook.pre(context.getEvent(), ElementType.HELMET)
            && MC.gameSettings.thirdPersonView == 0
            && !MC.player.inventory.armorItemInSlot(3).isEmpty();
    }

    @Override
    public Rect render(OverlayContext context) {
        ItemStack stack = MC.player.inventory.armorItemInSlot(3);

        if (MC.gameSettings.thirdPersonView == 0 && !stack.isEmpty()) {
            if (stack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
                MC.getTextureManager().bindTexture(PUMPKIN_BLUR_TEX_PATH);
                GlUtil.drawRect(MANAGER.getScreen(), new Rect(256, 256), Color.RED);
                MC.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
            } else {
                stack.getItem().renderHelmetOverlay(stack, MC.player, MC.getMainWindow().getScaledWidth(), MC.getMainWindow().getScaledHeight(), context.getPartialTicks());
            }
        }

        OverlayHook.post(context.getEvent(), ElementType.HELMET);
        return MANAGER.getScreen();
    }
}
