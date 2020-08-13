package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.SettingElement;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.HudElements;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class GuiElementChooser extends GuiElements {
    private final Screen parent;
    private final HudElement<?> element;

    private final SettingElement setting;

    public GuiElementChooser(Screen parent, HudElement<?> element, SettingElement setting) {
        super(new StringTextComponent(""));
        this.parent = parent;
        this.element = element;
        this.setting = setting;
    }

    @Override
    public void onClose() {
        setting.set(null);
        MC.displayGuiScreen(parent);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        MC.displayGuiScreen(parent);
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        HudElement<?> selected = getHoveredElement(mouseX, mouseY, (HudElement<?> element) -> {
            return element == this.element;
        });
        setting.set(selected);

        for(HudElement<?> element : HudElements.get().getRegistered()) {
            Rect bounds = element.getLastBounds();

            if(!bounds.isEmpty()) {
                drawRect(bounds, element == selected);
            }
        }
    }
}
