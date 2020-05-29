package jobicade.betterhud.element;

import jobicade.betterhud.element.settings.SettingPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public abstract class OverlayElement extends HudElement<RenderGameOverlayEvent> {
    protected OverlayElement(String name) {
        super(name);
    }

    protected OverlayElement(String name, SettingPosition position) {
        super(name, position);
    }
}
