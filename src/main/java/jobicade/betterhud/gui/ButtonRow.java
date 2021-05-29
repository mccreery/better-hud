package jobicade.betterhud.gui;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

import java.util.Arrays;
import java.util.List;

class ButtonRow {
    private final HudElement element;
    private final GuiActionButton toggle;
    private final GuiActionButton options;

    private Rect bounds;

    public ButtonRow(Screen callback, HudElement element) {
        this.element = element;

        toggle = new GuiActionButton("").setCallback(b -> {
            element.toggle();
            HudElement.SORTER.markDirty(SortType.ENABLED);

            Screen screen = Minecraft.getInstance().screen;
            if(screen != null) {
                screen.init(Minecraft.getInstance(), screen.width, screen.height);
            }
        });
        options = new GuiTexturedButton(new Rect(40, 0, 20, 20)).setCallback(b ->
            Minecraft.getInstance().setScreen(new GuiElementSettings(element, callback)));
    }

    public ButtonRow setBounds(Rect bounds) {
        this.bounds = bounds;
        toggle.setBounds(bounds.withWidth(bounds.getWidth() - 20).anchor(bounds, Direction.NORTH_WEST));
        options.setBounds(bounds.withWidth(20).anchor(bounds, Direction.NORTH_EAST));
        return this;
    }

    public Rect getBounds() {
        return bounds;
    }

    public List<Button> getButtons() {
        return Arrays.asList(toggle, options);
    }

    public ButtonRow update() {
        boolean supported = element.isSupportedByServer();

        toggle.active = supported;
        toggle.glowing = element.get();
        toggle.updateText(element.getUnlocalizedName(), "options", element.get());
        toggle.setTooltip(toggle.active ? null : I18n.get("betterHud.menu.unsupported"));

        options.active = supported && element.get() && !element.settings.isEmpty();

        return this;
    }
}
