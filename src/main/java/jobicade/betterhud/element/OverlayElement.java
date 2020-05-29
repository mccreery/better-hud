package jobicade.betterhud.element;

import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.IForgeRegistryEntryImpl;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public abstract class OverlayElement extends HudElement<RenderGameOverlayEvent>
        implements IForgeRegistryEntryImpl<OverlayElement> {
    protected OverlayElement(String name) {
        super(name);
    }

    protected OverlayElement(String name, SettingPosition position) {
        super(name, position);
    }

    // IForgeRegistryEntryImpl required implementation
    private ResourceLocation registryName;

    @Override
    public void setRegistryNameInner(ResourceLocation name) {
        this.registryName = name;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
