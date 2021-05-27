package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Hotbar extends OverrideElement {
    public Hotbar() {
        super("hotbar", new SettingPosition(DirectionOptions.TOP_BOTTOM, DirectionOptions.NONE));
        position.setEdge(true).setPostSpacer(2);
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        position.setPreset(Direction.SOUTH);
    }

    @Override
    protected ElementType getType() {
        return ElementType.HOTBAR;
    }

    @Override
    public boolean shouldRender(Event event) {
        return !GuiIngameForge.renderHotbar && super.shouldRender(event);
    }

    @Override
    protected Rect render(Event event) {
        Rect barTexture = new Rect(182, 22);
        Rect bounds = position.applyTo(new Rect(barTexture));

        Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.WIDGETS);
        GlUtil.drawRect(bounds, barTexture);

        Rect slot = bounds.grow(-3).withWidth(16);

        float partialTicks = getPartialTicks(event);
        for(int i = 0; i < 9; i++, slot = slot.translate(Direction.EAST.scale(20))) {
            if(i == Minecraft.getMinecraft().player.inventory.currentItem) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.WIDGETS);
                GlUtil.drawRect(slot.grow(4), new Rect(0, 22, 24, 24));
            }

            GlUtil.renderHotbarItem(slot, Minecraft.getMinecraft().player.inventory.mainInventory.get(i), partialTicks);
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
        return bounds;
    }
}
