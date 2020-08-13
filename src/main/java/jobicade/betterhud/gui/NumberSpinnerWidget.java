package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;

public class NumberSpinnerWidget extends Widget implements INestedGuiEventHandler {
    private final TextFieldWidget textField;
    private final Button upButton;
    private final Button downButton;

    private int value;

    public NumberSpinnerWidget(int x, int y, int width, int height, String message) {
        super(x, y, width, height, message);

        // TODO image buttons
        textField = new TextFieldWidget(MC.fontRenderer, x, y, width - 20, height, message);
        upButton = new Button(x + width - 20, y, 20, 10, message, b -> up());
        downButton = new Button(x + width - 20, y + 10, 20, 10, message, b -> down());

        children = Arrays.asList(textField, upButton, downButton);
    }

    private void up() {
        ++value;
        setMessage(String.valueOf(value));
    }

    private void down() {
        --value;
        setMessage(String.valueOf(value));
    }

    private final List<Widget> children;

    @Override
    public List<? extends IGuiEventListener> children() {
        return children;
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean p_setDragging_1_) {
    }

    @Override
    public IGuiEventListener getFocused() {
        return null;
    }

    @Override
    public void setFocused(IGuiEventListener p_setFocused_1_) {
    }
}
