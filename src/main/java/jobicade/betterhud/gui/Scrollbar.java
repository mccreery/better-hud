package jobicade.betterhud.gui;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;

public class Scrollbar extends Widget {
    public Scrollbar(int x, int y, int width, int height, float thumbSize) {
        super(x, y, width, height, "");
        this.thumbSize = thumbSize;
    }

    private float value = 0;
    /**
     * @return The value between 0 and 1.
     */
    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = MathHelper.clamp(value, 0, 1);
    }

    private float thumbSize = 0.5f;
    /**
     * @param thumbSize The size of the thumb as a fraction of the track.
     */
    public void setThumbSize(float thumbSize) {
        this.thumbSize = thumbSize;
    }

    /**
     * @return The thumb height in pixels.
     */
    private int getThumbHeight() {
        return Math.round(height * thumbSize);
    }

    private Color background = Color.TRANSLUCENT;
    private Color foreground = Color.FOREGROUND;
    private Color highlight = Color.HIGHLIGHT;

    public void setPalette(Color background, Color foreground, Color highlight) {
        this.background = background;
        this.foreground = foreground;
        this.highlight = highlight;
    }

    private Rect getThumb() {
        int thumbHeight = getThumbHeight();
        int track = height - thumbHeight;
        int thumbY = y + Math.round(track * getValue());

        return new Rect(x, thumbY, width, thumbHeight);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        fill(x, y, x + width, y + height, background.getPacked());

        Rect thumb = getThumb();
        GlUtil.drawRect(thumb, isHovered() ? highlight : foreground);
    }

    // The offset of the initial mousedown on the thumb
    private double grabOffset;
    private boolean grabbed;

    public boolean isGrabbed() {
        return grabbed;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (clicked(mouseX, mouseY) && button == 0) {
            Rect thumb = getThumb();

            // Mouse position on thumb defaults to center
            if (thumb.contains((int)mouseX, (int)mouseY)) {
                grabOffset = mouseY - thumb.getY();
            } else {
                grabOffset = thumb.getHeight() / 2;
                mouseDragged(mouseX, mouseY, button, 0, 0);
            }
            grabbed = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double mouseXDelta, double mouseYDelta) {
        // No need to check grabbed due to screen dragging flag
        int thumbY = (int)Math.round(mouseY - grabOffset);
        int track = height - getThumbHeight();

        setValue((float)(thumbY - y) / track);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        grabbed = false;
        return true;
    }
}
