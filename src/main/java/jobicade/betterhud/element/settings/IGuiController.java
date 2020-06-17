package jobicade.betterhud.element.settings;

import jobicade.betterhud.geom.Point;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;

public interface IGuiController {
    /**
     * @param topAnchor The point just above the setting GUI in the center.
     * @return The added height of the GUI.
     */
    int populateGui(Point topAnchor, Populator populator);

    /**
     * Callback for buttons added by this controller.
     */
    void actionPerformed(GuiButton button);

    /**
     * Callback for key pressed in textboxes added by this controller.
     * @return {@code true} to consume the keypress.
     */
    boolean textboxKeyTyped(char typedChar, int keyCode);

    /**
     * Called when any button in the GUI is pressed or a key is typed.
     */
    void otherUpdate();

    /**
     * Renders decorations to the GUI after buttons.
     */
    void drawGui();

    public interface Populator {
        void addButton(GuiButton button);
        void addLabel(GuiLabel label);
        void addTextField(GuiTextField textField);
    }
}
