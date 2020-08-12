package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.opengl.GL11;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.java.games.input.Keyboard;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

public class GuiElementSettings extends GuiMenuScreen {
    private static final int REPEAT_SPEED       = 20; // Rate of speed-up to 20/s
    private static final int REPEAT_SPEED_FAST = 10; // Rate of speed-up beyond 20/s

    public HudElement<?> element;
    private ArrayList<TextFieldWidget> textboxList = new ArrayList<>();
    public HashMap<AbstractGui, Setting> callbacks = new HashMap<>();

    private Rect viewport;

    private SuperButton done;
    private GuiScrollbar scrollbar;

    private int repeatTimer = 0;

    private final Screen previousScreen;

    public GuiElementSettings(HudElement<?> element, Screen previousScreen) {
        super(new TranslationTextComponent("betterHud.menu.settings", new TranslationTextComponent(element.getUnlocalizedName())));

        this.element = element;
        this.previousScreen = previousScreen;
    }

    @Override
    public void init() {
        buttons.clear();
        textboxList.clear();
        //labels.clear();

        done = new SuperButton(b -> MC.displayGuiScreen(previousScreen));
        done.setBounds(new Rect(200, 20).align(getOrigin(), Direction.NORTH));
        done.setMessage(I18n.format("gui.done"));

        Keyboard.enableRepeatEvents(true);

        List<AbstractGui> parts = new ArrayList<AbstractGui>();
        int contentHeight = element.settings.getGuiParts(parts, callbacks, new Point(width / 2, SPACER)).getY();

        for(AbstractGui gui : parts) {
            if(gui instanceof Button) {
                buttons.add((Button)gui);
            } else if(gui instanceof GuiLabel) {
                labelList.add((GuiLabel)gui);
            } else if(gui instanceof TextFieldWidget) {
                textboxList.add((TextFieldWidget)gui);
            }
        }

        viewport = new Rect(width / 2 - 200, height / 16 + 40 + SPACER, 400, 0).withBottom(height - 20);
        scrollbar = new GuiScrollbar(viewport, contentHeight);

        for(Setting setting : callbacks.values()) {
            setting.updateGuiParts(callbacks.values());
        }
    }

    @Override
    public void onClose() {
        Keyboard.enableRepeatEvents(false);
        BetterHud.getProxy().getConfig().saveSettings();
    }

    @Override
    protected void actionPerformed(Button button) {
        if(callbacks.containsKey(button)) {
            callbacks.get(button).actionPerformed(this, button);

            // Notify the rest of the elements that a button has been pressed
            for(Setting setting : callbacks.values()) {
                setting.updateGuiParts(callbacks.values());
            }
        } else {
            super.actionPerformed(button);
        }
    }

    /** @see GuiScreen#handleMouseInput() */
    @Override
    public void tick() {
        for(TextFieldWidget field : this.textboxList) {
            field.tick();
        }

        if(selectedButton instanceof GuiActionButton && ((GuiActionButton)selectedButton).getRepeat()) {
            // Slowly build up speed until 1/tick after REPEAT_SPEED ticks
            if(++repeatTimer % Math.max(1, Math.round(REPEAT_SPEED / repeatTimer)) == 0) {
                // When above REPEAT_SPEED, repeat multiple times per tick
                int c = Math.max(1, (repeatTimer - REPEAT_SPEED) / REPEAT_SPEED_FAST);

                for(int i = 0; i < c; i++) {
                    actionPerformed(selectedButton);
                }
            }
        } else {
            repeatTimer = 0;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        for(TextFieldWidget field : this.textboxList) {
            field.textboxKeyTyped(typedChar, keyCode);

            if(callbacks.containsKey(field)) {
                callbacks.get(field).updateGuiParts(callbacks.values());
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        scrollbar.handleMouseInput();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseY >= viewport.getTop() && mouseY < viewport.getBottom()) {
            super.mouseClicked(mouseX, mouseY + getMouseOffset(), button);

            for(GuiTextField field : this.textboxList) {
                field.mouseClicked(mouseX, mouseY + getMouseOffset(), button);
            }
        }

        // Done button isn't in buttonList, have to handle it manually
        if(done.mousePressed(this, mouseX, mouseY)) {
            ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, done, buttonList);
            if(MinecraftForge.EVENT_BUS.post(event)) return;

            GuiButton eventResult = event.getButton();
            selectedButton = eventResult;
            eventResult.playPressSound(this.mc.getSoundHandler());
            actionPerformed(eventResult);

            if(this.equals(MC.currentScreen)) {
                MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, done, buttonList));
            }
        }
        scrollbar.mouseClicked(mouseX, mouseY, button);
        return true; // TODO
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long heldTime) {
        super.mouseClickMove(mouseX, mouseY + getMouseOffset(), button, heldTime);
        scrollbar.mouseClickMove(mouseX, mouseY, button, heldTime);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY + getMouseOffset(), button);
        scrollbar.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        drawTitle();

        done.render(mouseX, mouseY, partialTicks);

        RenderSystem.pushMatrix();
        GlUtil.beginScissor(viewport);
        GL11.glTranslatef(0, -getMouseOffset(), 0);

        int viewportMouseY = mouseY + getMouseOffset();

        for(Button button : buttons) button.render(mouseX, viewportMouseY, partialTicks);
        for(GuiLabel label : labelList) label.drawLabel(mouseX, viewportMouseY);

        for(TextFieldWidget field : this.textboxList) {
            field.render(mouseX, mouseY, partialTicks);
        }
        element.settings.draw();

        GlUtil.endScissor();
        RenderSystem.popMatrix();

        scrollbar.drawScrollbar(mouseX, mouseY);
        drawResolution(10, 10, 100);
    }

    /** Add to {@code mouseY} to get the effective {@code mouseY} taking into account scroll */
    @Deprecated private int getMouseOffset() {
        return scrollbar.getScroll() - viewport.getTop();
    }

    /** Draws a diagram of the size of the HUD */
    private void drawResolution(int x, int y, int width) {
        int height = width * this.height / this.width;

        // Precalculate width
        String widthDisplay = String.valueOf(this.width);
        int stringWidth = font.getStringWidth(widthDisplay);

        // Horizontal
        int textX = x + (width - stringWidth) / 2;
        hLine(x, textX - SPACER, y, Color.WHITE.getPacked());
        hLine(x + (width + stringWidth) / 2 + SPACER, x + width, y, Color.WHITE.getPacked());
        font.drawString(widthDisplay, textX, y, Color.WHITE.getPacked());

        // Vertical
        int textY = y + (height - font.FONT_HEIGHT) / 2;
        vLine(x, y, textY - SPACER, Color.WHITE.getPacked());
        vLine(x, y + (height + font.FONT_HEIGHT) / 2 + SPACER, y + height, Color.WHITE.getPacked());
        font.drawString(String.valueOf(this.height), x, textY, Color.WHITE.getPacked());
    }
}
