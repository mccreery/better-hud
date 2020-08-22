package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MC;

import com.mojang.blaze3d.systems.RenderSystem;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

public class Chat extends OverlayElement {
    private final SettingPosition position;

    public Chat() {
        super("chat");

        position = new SettingPosition("position");
        position.setDirectionOptions(DirectionOptions.CORNERS);
        position.setContentOptions(DirectionOptions.NONE);
        addSetting(position);
    }

    private Rect bounds;

    @Override
    public boolean shouldRender(OverlayContext context) {
        final int fontHeight = MC.fontRenderer.FONT_HEIGHT;
        bounds = position.applyTo(new Rect(getChatSize(getChatGui())));

        RenderGameOverlayEvent.Chat chatEvent = new RenderGameOverlayEvent.Chat(
            context.getEvent(),
            bounds.getX(),
            bounds.getBottom() - fontHeight
        );

        boolean canceled = MinecraftForge.EVENT_BUS.post(chatEvent);
        bounds = new Rect(
            chatEvent.getPosX(),
            chatEvent.getPosY() + fontHeight - bounds.getHeight(),
            bounds.getWidth(),
            bounds.getHeight()
        );
        return !canceled;
    }

    @Override
    public Rect render(OverlayContext context) {
        Minecraft mc = MC;

        RenderSystem.pushMatrix();
        RenderSystem.translatef(bounds.getX(), bounds.getBottom() - mc.fontRenderer.FONT_HEIGHT, 0);

        getChatGui().render(mc.ingameGUI.getTicks());

        RenderSystem.popMatrix();

        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(context.getEvent(), ElementType.CHAT));
        return bounds;
    }

    private Size getChatSize(NewChatGui guiChat) {
        return new Size(guiChat.getChatWidth() + 6, guiChat.getChatHeight());
    }

    private NewChatGui getChatGui() {
        return MC.ingameGUI.getChatGUI();
    }
}
