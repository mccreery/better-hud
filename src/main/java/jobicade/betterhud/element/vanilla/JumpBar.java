package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class JumpBar extends OverlayElement {
    private SettingPosition position;

    public JumpBar() {
        super("jumpBar");

        settings.addChild(position = new SettingPosition("position", DirectionOptions.BAR, DirectionOptions.NORTH_SOUTH));
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return GuiIngameForge.renderJumpBar
            && !OverlayHook.pre(context.getEvent(), ElementType.JUMPBAR);
    }

    @Override
    public Rect render(OverlayContext context) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);

        Rect bounds = new Rect(182, 5);
        if(!position.isCustom() && position.getDirection() == Direction.SOUTH) {
            bounds = MANAGER.position(Direction.SOUTH, bounds, false, 1);
        } else {
            bounds = position.applyTo(bounds);
        }

        float charge = Minecraft.getMinecraft().player.getHorseJumpPower();
        int filled = (int)(charge * bounds.getWidth());

        GlUtil.drawRect(bounds, bounds.move(0, 84));

        if(filled > 0) {
            GlUtil.drawRect(bounds.withWidth(filled), new Rect(0, 89, filled, bounds.getHeight()));
        }

        OverlayHook.post(context.getEvent(), ElementType.JUMPBAR);
        return bounds;
    }
}
