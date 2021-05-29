package jobicade.betterhud.gui;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.geom.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

public class GuiElementChooser extends GuiElements {
    private final Screen parent;
    private final HudElement element;

    private final Setting<HudElement> setting;

    public GuiElementChooser(Screen parent, HudElement element, Setting<HudElement> setting) {
        this.parent = parent;
        this.element = element;
        this.setting = setting;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 1) {
            setting.set(null);
            Minecraft.getInstance().setScreen(parent);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Minecraft.getInstance().setScreen(parent);
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        HudElement selected = getHoveredElement((int)mouseX, (int)mouseY, (HudElement element) -> {
            return element == this.element;
        });
        setting.set(selected);

        for(HudElement element : HudElement.ELEMENTS) {
            Rect bounds = element.getLastBounds();

            if(!bounds.isEmpty()) {
                drawRect(bounds, element == selected);
            }
        }
    }
}
