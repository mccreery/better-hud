package jobicade.betterhud.element.settings.gui;

import java.util.function.BooleanSupplier;

import jobicade.betterhud.geom.Point;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;

public abstract class SettingGui {
    private BooleanSupplier enableCheck;
    public void setEnableCheck(BooleanSupplier enableCheck) {
        this.enableCheck = enableCheck;
    }

    public boolean isEnabled() {
        return enableCheck.getAsBoolean();
    }

    private boolean hidden;
    public void setHidden() {
        hidden = true;
    }

    public boolean isHidden() {
        return hidden;
    }

    /**
     * @param topAnchor The point just above the setting GUI in the center.
     * @return The added height of the GUI.
     */
    public abstract int populateGui(Point topAnchor, Populator populator);

    /**
     * Callback for buttons added by this controller.
     */
    public abstract void actionPerformed(GuiButton button);

    /**
     * Callback for key pressed in textboxes added by this controller.
     * @return {@code true} to consume the keypress.
     */
    public abstract boolean textboxKeyTyped(char typedChar, int keyCode);

    /**
     * Called when any button in the GUI is pressed or a key is typed.
     */
    public abstract void otherUpdate();

    public abstract void drawDecorations();

    public interface Populator {
        void addButton(GuiButton button);
        void addLabel(GuiLabel label);
        void addTextField(GuiTextField textField);
    }
}
