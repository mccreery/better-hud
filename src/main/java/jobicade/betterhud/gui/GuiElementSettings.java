package jobicade.betterhud.gui;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static jobicade.betterhud.BetterHud.SPACER;

public class GuiElementSettings extends GuiMenuScreen {
    private static final int REPEAT_SPEED       = 20; // Rate of speed-up to 20/s
    private static final int REPEAT_SPEED_FAST = 10; // Rate of speed-up beyond 20/s

    public HudElement element;
    private ArrayList<TextFieldWidget> textboxList = new ArrayList<TextFieldWidget>();
    public HashMap<AbstractGui, Setting<?>> callbacks = new HashMap<AbstractGui, Setting<?>>();

    private Rect viewport;

    private final GuiActionButton done = new GuiActionButton(I18n.get("gui.done"));
    private GuiScrollbar scrollbar;

    private int repeatTimer = 0;

    public GuiElementSettings(HudElement element, Screen prev) {
        this.element = element;
        done.setCallback(b -> Minecraft.getInstance().setScreen(prev));
    }

    @Override
    public void func_73866_w_() {
        setTitle(I18n.get("betterHud.menu.settings", this.element.getLocalizedName()));
        field_146292_n.clear();
        textboxList.clear();
        field_146293_o.clear();

        Keyboard.enableRepeatEvents(true);
        done.setBounds(new Rect(200, 20).align(getOrigin(), Direction.NORTH));

        List<AbstractGui> parts = new ArrayList<AbstractGui>();
        int contentHeight = element.settings.getGuiParts(parts, callbacks, new Point(field_146294_l / 2, SPACER)).getY();

        for(AbstractGui gui : parts) {
            if(gui instanceof Button) {
                field_146292_n.add((Button)gui);
            } else if(gui instanceof GuiLabel) {
                field_146293_o.add((GuiLabel)gui);
            } else if(gui instanceof TextFieldWidget) {
                textboxList.add((TextFieldWidget)gui);
            }
        }

        viewport = new Rect(field_146294_l / 2 - 200, field_146295_m / 16 + 40 + SPACER, 400, 0).withBottom(field_146295_m - 20);
        scrollbar = new GuiScrollbar(viewport, contentHeight);

        for(Setting<?> setting : callbacks.values()) {
            setting.updateGuiParts(callbacks.values());
        }
    }

    @Override
    public void func_146281_b() {
        Keyboard.enableRepeatEvents(false);
        BetterHud.getConfig().save();
    }

    @Override
    protected void func_146284_a(Button button) {
        if(callbacks.containsKey(button)) {
            callbacks.get(button).actionPerformed(this, button);

            // Notify the rest of the elements that a button has been pressed
            for(Setting<?> setting : callbacks.values()) {
                setting.updateGuiParts(callbacks.values());
            }
        } else {
            super.func_146284_a(button);
        }
    }

    /** @see Screen#handleMouseInput() */
    @Override
    public void func_73876_c() {
        for(TextFieldWidget field : this.textboxList) {
            field.tick();
        }

        if(field_146290_a instanceof GuiActionButton && ((GuiActionButton)field_146290_a).getRepeat()) {
            // Slowly build up speed until 1/tick after REPEAT_SPEED ticks
            if(++repeatTimer % Math.max(1, Math.round(REPEAT_SPEED / repeatTimer)) == 0) {
                // When above REPEAT_SPEED, repeat multiple times per tick
                int c = Math.max(1, (repeatTimer - REPEAT_SPEED) / REPEAT_SPEED_FAST);

                for(int i = 0; i < c; i++) {
                    func_146284_a(field_146290_a);
                }
            }
        } else {
            repeatTimer = 0;
        }
    }

    @Override
    protected void func_73869_a(char typedChar, int keyCode) throws IOException {
        super.func_73869_a(typedChar, keyCode);

        for(TextFieldWidget field : this.textboxList) {
            field.func_146201_a(typedChar, keyCode);

            if(callbacks.containsKey(field)) {
                callbacks.get(field).updateGuiParts(callbacks.values());
            }
        }
    }

    @Override
    public void func_146274_d() throws IOException {
        super.func_146274_d();
        scrollbar.handleMouseInput();
    }

    @Override
    protected void func_73864_a(int mouseX, int mouseY, int button) throws IOException {
        if(mouseY >= viewport.getTop() && mouseY < viewport.getBottom()) {
            super.func_73864_a(mouseX, mouseY + getMouseOffset(), button);

            for(TextFieldWidget field : this.textboxList) {
                field.func_146192_a(mouseX, mouseY + getMouseOffset(), button);
            }
        }

        // Done button isn't in buttonList, have to handle it manually
        if(done.func_146116_c(this.field_146297_k, mouseX, mouseY)) {
            ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, done, field_146292_n);
            if(MinecraftForge.EVENT_BUS.post(event)) return;

            Button eventResult = event.getButton();
            field_146290_a = eventResult;
            eventResult.func_146113_a(this.field_146297_k.getSoundManager());
            func_146284_a(eventResult);

            if(this.equals(Minecraft.getInstance().screen)) {
                MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, done, field_146292_n));
            }
        }
        scrollbar.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void func_146273_a(int mouseX, int mouseY, int button, long heldTime) {
        super.func_146273_a(mouseX, mouseY + getMouseOffset(), button, heldTime);
        scrollbar.mouseClickMove(mouseX, mouseY, button, heldTime);
    }

    @Override
    public void func_146286_b(int mouseX, int mouseY, int button) {
        super.func_146286_b(mouseX, mouseY + getMouseOffset(), button);
        scrollbar.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        func_146276_q_();
        drawTitle();

        ScaledResolution resolution = new ScaledResolution(Minecraft.getInstance());
        done.func_191745_a(Minecraft.getInstance(), mouseX, mouseY, partialTicks);

        matrixStack.pushPose();
        GlUtil.beginScissor(viewport, resolution);
        GL11.glTranslatef(0, -getMouseOffset(), 0);

        int viewportMouseY = mouseY + getMouseOffset();

        for(Button button : field_146292_n) button.func_191745_a(field_146297_k, mouseX, viewportMouseY, partialTicks);
        for(GuiLabel label : field_146293_o) label.func_146159_a(field_146297_k, mouseX, viewportMouseY);

        for(TextFieldWidget field : this.textboxList) {
            field.func_146194_f();
        }
        element.settings.draw();

        GlUtil.endScissor();
        matrixStack.popPose();

        scrollbar.drawScrollbar(mouseX, mouseY);
        drawResolution(10, 10, 100);
    }

    /** Add to {@code mouseY} to get the effective {@code mouseY} taking into account scroll */
    @Deprecated private int getMouseOffset() {
        return scrollbar.getScroll() - viewport.getTop();
    }

    /** Draws a diagram of the size of the HUD */
    private void drawResolution(int x, int y, int width) {
        int height = width * this.field_146295_m / this.field_146294_l;

        // Precalculate width
        String widthDisplay = String.valueOf(this.field_146294_l);
        int stringWidth = field_146289_q.width(widthDisplay);

        // Horizontal
        int textX = x + (width - stringWidth) / 2;
        func_73730_a(x, textX - SPACER, y, Color.WHITE.getPacked());
        func_73730_a(x + (width + stringWidth) / 2 + SPACER, x + width, y, Color.WHITE.getPacked());
        field_146289_q.func_78276_b(widthDisplay, textX, y, Color.WHITE.getPacked());

        // Vertical
        int textY = y + (height - field_146289_q.lineHeight) / 2;
        func_73728_b(x, y, textY - SPACER, Color.WHITE.getPacked());
        func_73728_b(x, y + (height + field_146289_q.lineHeight) / 2 + SPACER, y + height, Color.WHITE.getPacked());
        field_146289_q.func_78276_b(String.valueOf(this.field_146295_m), x, textY, Color.WHITE.getPacked());
    }
}
