package jobicade.betterhud.render;

import java.util.Arrays;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class Color {
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color GRAY = new Color(127, 127, 127);
    public static final Color BLACK = new Color(0, 0, 0);

    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);

    public static final Color TRANSLUCENT = new Color(95, 0, 0, 0);
    public static final Color FOREGROUND  = new Color(127, 63, 63, 63);
    public static final Color HIGHLIGHT   = new Color(191, 63, 63, 63);

    private final int alpha, red, green, blue;
    private final int packed;

    public Color(int red, int green, int blue) {
        this(255, red, green, blue);
    }

    public Color(int alpha, int red, int green, int blue) {
        this.alpha = MathHelper.clamp(alpha, 0, 255);
        this.red = MathHelper.clamp(red, 0, 255);
        this.green = MathHelper.clamp(green, 0, 255);
        this.blue = MathHelper.clamp(blue, 0, 255);
        this.packed = (this.alpha << 24) | (this.red << 16) | (this.green << 8) | this.blue;
    }

    public Color(int packed) {
        this.alpha = packed >> 24;
        this.red = (packed >> 16) & 0xff;
        this.green = (packed >> 8) & 0xff;
        this.blue = packed & 0xff;
        this.packed = packed;
    }

    public int getPacked() {
        return packed;
    }

    public int getAlpha() {return alpha;}
    public int getRed() {return red;}
    public int getGreen() {return green;}
    public int getBlue() {return blue;}

    public Color withAlpha(int alpha) {return new Color(alpha, red, green, blue);}
    public Color withRed(int red) {return new Color(alpha, red, green, blue);}
    public Color withGreen(int green) {return new Color(alpha, red, green, blue);}
    public Color withBlue(int blue) {return new Color(alpha, red, green, blue);}

    public void apply() {
        GlStateManager.color(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Color)) return false;
        Color color = (Color)obj;

        return red == color.red &&
            green == color.green &&
            blue == color.blue &&
            alpha == color.alpha;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[] {red, green, blue, alpha});
    }

    @Override
    public String toString() {
        return String.format("{rgba: %d, %d, %d, %d}", red, green, blue, alpha);
    }

    public static Color fromHSV(float hue, float saturation, float value) {
        hue -= MathHelper.floor(hue);
        saturation = MathHelper.clamp(saturation, 0, 1);
        value = MathHelper.clamp(value, 0, 1);

        return new Color(MathHelper.hsvToRGB(hue, saturation, value)).withAlpha(255);
    }

    public static Color getProgressColor(float progress) {
        progress = MathHelper.clamp(progress, 0, 1);
        return Color.fromHSV(progress / 3f, 1, 1);
    }
}
