package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.events.OverlayContext;
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
import net.minecraftforge.common.MinecraftForge;

public class HelmetOverlay extends OverlayElement {
    private static final ResourceLocation PUMPKIN_BLUR_TEX_PATH = new ResourceLocation("textures/misc/pumpkinblur.png");

    public HelmetOverlay() {
        super("helmetOverlay");
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return Minecraft.getMinecraft().gameSettings.thirdPersonView == 0
            && !Minecraft.getMinecraft().player.inventory.armorItemInSlot(3).isEmpty()
            && !MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(context.getEvent(), ElementType.HELMET));
    }

    @Override
    public Rect render(OverlayContext context) {
        ItemStack stack = Minecraft.getMinecraft().player.inventory.armorItemInSlot(3);
        Item item = stack.getItem();

        if(item == Item.getItemFromBlock(Blocks.PUMPKIN)) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(PUMPKIN_BLUR_TEX_PATH);
            GlUtil.drawRect(MANAGER.getScreen(), new Rect(256, 256), Color.RED);
            Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
        } else {
            item.renderHelmetOverlay(stack, Minecraft.getMinecraft().player, new ScaledResolution(Minecraft.getMinecraft()), context.getPartialTicks());
        }

        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(context.getEvent(), ElementType.HELMET));
        return MANAGER.getScreen();
    }
}
