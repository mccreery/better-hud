package jobicade.betterhud.events;

import static jobicade.betterhud.BetterHud.MC;

import com.mojang.blaze3d.systems.RenderSystem;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.entityinfo.BillboardElement;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.registry.BillboardElements;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.GlSnapshot;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.RayTraceUtil;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = BetterHud.MODID)
public class BillboardHook {
    private static SnapshotTracker tracker;

    @SubscribeEvent
    public static void worldRender(RenderWorldLastEvent event) {
        MC.getProfiler().startSection(BetterHud.MODID);

        if(BetterHud.isModEnabled()) {
            Entity entity = RayTraceUtil.getMouseOver(MC.getRenderViewEntity(), HudElements.GLOBAL.getBillboardDistance(), event.getPartialTicks());

            if(entity instanceof LivingEntity) {
                renderMobInfo(new BillboardContext(event, (LivingEntity)entity));
            }
        }
        MC.getProfiler().endSection();
    }

    /**
    * Renders mob info elements to the screen.
    */
    private static void renderMobInfo(BillboardContext event) {
        BetterHud.MANAGER.reset(Point.zero());

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        Color.WHITE.apply();
        MC.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);

        RenderSystem.pushMatrix();
        GlUtil.setupBillboard(event.getPointedEntity(), event.getPartialTicks(), HudElements.GLOBAL.getBillboardScale());

        GlSnapshot pre = null;
        if (HudElements.GLOBAL.isDebugMode()) {
            if (tracker == null) {
                tracker = new SnapshotTracker(BetterHud.getLogger());
            }
            pre = new GlSnapshot();
        }

        for (BillboardElement element : BillboardElements.get().getEnabled()) {
            if (element.getServerDependency().containsVersion(BetterHud.getServerVersion())
                    && element.shouldRender(event)) {
                MC.getProfiler().startSection(element.getName());
                element.render(event);
                MC.getProfiler().endSection();
            }
        }

        if (HudElements.GLOBAL.isDebugMode()) {
            tracker.step(pre, new GlSnapshot());
        }

        RenderSystem.popMatrix();

        MC.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
