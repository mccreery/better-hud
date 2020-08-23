package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.opengl.GL11;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiElementSettings extends GuiMenuScreen {
    //private static final int REPEAT_SPEED       = 20; // Rate of speed-up to 20/s
    //private static final int REPEAT_SPEED_FAST = 10; // Rate of speed-up beyond 20/s

    public HudElement<?> element;
    //private ArrayList<TextFieldWidget> textboxList = new ArrayList<>();

    private List<TextFieldWidget> textboxes = new ArrayList<>();

    private Rect viewport;

    private SuperButton done;
    private Scrollbar scrollbar;

    //private int repeatTimer = 0;

    private final Screen previousScreen;

    public GuiElementSettings(HudElement<?> element, Screen previousScreen) {
        super(new TranslationTextComponent("betterHud.menu.settings", new TranslationTextComponent(element.getUnlocalizedName())));

        this.element = element;
        this.previousScreen = previousScreen;
    }

    private int contentHeight;

    @Override
    public void init() {
        done = addButton(new SuperButton(b -> MC.displayGuiScreen(previousScreen)));
        done.setBounds(new Rect(200, 20).align(getOrigin(), Direction.NORTH));
        done.setMessage(I18n.format("gui.done"));

        // old LWJGL 2 code
        //Keyboard.enableRepeatEvents(true);

        contentHeight = element.getRootSetting().getGuiParts(new Populator(), new Point(width / 2, SPACER)).getY();

        viewport = new Rect(width / 2 - 200, height / 16 + 40 + SPACER, 400, 0).withBottom(height - 20);
        scrollbar = new Scrollbar(viewport.getRight() - 8, viewport.getY(), 8, viewport.getHeight(), (float)viewport.getHeight() / contentHeight);

        element.getRootSetting().updateGuiParts();
    }

    @Override
    public void onClose() {
        try {
            BetterHud.getConfigManager().saveFile();
        } catch (IOException e) {
            BetterHud.getLogger().error(e);
        }
    }

    /** @see GuiScreen#handleMouseInput() */
    @Override
    public void tick() {
        for(TextFieldWidget field : textboxes) {
            field.tick();
        }

        // TODO repeat
        /*if(getFocused() instanceof SuperButton && ((GuiActionButton)selectedButton).getRepeat()) {
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
        }*/
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
            element.getRootSetting().updateGuiParts();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseY >= viewport.getTop() && mouseY < viewport.getBottom() && super.mouseClicked(mouseX, mouseY + getMouseOffset(), button)) {
            element.getRootSetting().updateGuiParts();
            return true;
        } else if (done.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else if (scrollbar.mouseClicked(mouseX, mouseY, button)) {
            setDragging(true);
            setFocused(scrollbar);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrollbar.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        drawTitle();

        // done button doesn't get translated
        done.render(mouseX, mouseY, partialTicks);
        scrollbar.render(mouseX, mouseY, partialTicks);

        RenderSystem.pushMatrix();
        GlUtil.beginScissor(viewport);
        GL11.glTranslatef(0, -getMouseOffset(), 0);

        int viewportMouseY = mouseY + getMouseOffset();
        super.render(mouseX, viewportMouseY, partialTicks);

        element.getRootSetting().draw();

        GlUtil.endScissor();
        RenderSystem.popMatrix();

        drawResolution(10, 10, 100);
    }

    private int getScroll() {
        return Math.round(scrollbar.getValue() * (contentHeight - viewport.getHeight()));
    }

    /** Add to {@code mouseY} to get the effective {@code mouseY} taking into account scroll */
    @Deprecated private int getMouseOffset() {
        return getScroll() - viewport.getTop();
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

    /**
     * Semantic interface for adding buttons, subject to change at Mojang's whim.
     */
    public class Populator {
        private Populator() {
        }

        /**
         * Adds a widget to the scrolling viewport.
         */
        public <T extends Widget> T add(T widget) {
            return addButton(widget);
        }

        public <T extends TextFieldWidget> T add(T textbox) {
            textboxes.add(textbox);
            return addButton(textbox);
        }
    }
}
