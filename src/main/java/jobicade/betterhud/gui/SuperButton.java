package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.gui.ChatFormatting;

import jobicade.betterhud.geom.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * A Button with added capabilities:
 *
 * <ul>
 * <li>Custom texture
 * <li>Bounds using Rect
 * <li>Forced hover state
 * <li>Tooltip rendering
 * <li>Repeat clicks
 * </ul>
 *
 * Screens should use {@link #renderToolTip(int, int)} and {@link #getRepeat()}.
 */
public class SuperButton extends Button {
    public SuperButton(IPressable onPress) {
        this(0, 0, 0, 0, "", onPress);
    }

    public SuperButton(int x, int y, int width, int height, String text, IPressable onPress) {
        super(x, y, width, height, text, onPress);
    }

    public Rect getBounds() {
        return new Rect(x, y, width, height);
    }

    public void setBounds(Rect bounds) {
        this.x = bounds.getX();
        this.y = bounds.getY();
        this.width = bounds.getWidth();
        this.height = bounds.getHeight();
    }

    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;

        minecraft.getTextureManager().bindTexture(texture);
        RenderSystem.color4f(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int v = topV + getYImage(isHovered()) * 20;
        int hw = width / 2;

        blit(x, y, leftU, v, hw, height);
        blit(x + hw, y, leftU + textureWidth - hw, v, hw, height);

        renderBg(minecraft, mouseX, mouseY);
        int color = getFGColor() | MathHelper.ceil(alpha * 255) << 24;
        this.drawCenteredString(fontrenderer, getMessage(), x + hw, y + (height - 8) / 2, color);
    }

    private ResourceLocation texture = WIDGETS_LOCATION;
    private int leftU = 0;
    private int topV = 46;
    private int textureWidth = 200;

    /**
     * Textures must be tightly stacked vertically: disabled, enabled, hovered.
     * The height of the textures should match the height of the button.
     *
     * @param leftU The left coordinate of the disabled texture.
     * @param topV The top coordinate of the disabled texture.
     * @param textureWidth The width of the button textures.
     */
    public void setTexture(ResourceLocation texture, int leftU, int topV, int textureWidth) {
        this.texture = texture;
        this.leftU = leftU;
        this.topV = topV;
        this.textureWidth = textureWidth;
    }

    private boolean forceHovered;
    /**
     * @param forceHovered {@code true} to always render as hovered.
     */
    public void setForceHovered(boolean forceHovered) {
        this.forceHovered = forceHovered;
    }

    @Override
    protected int getYImage(boolean mouseOver) {
        return super.getYImage(forceHovered || mouseOver);
    }

    private List<String> tooltip;
    /**
     * @param tooltip The tooltip to show on hover or {@code null}.
     */
    public void setTooltip(List<String> tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * @param tooltip The tooltip to show on hover or {@code null}.
     */
    public void setTooltip(String tooltip) {
        setTooltip(Arrays.asList(tooltip));
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        MC.currentScreen.renderTooltip(tooltip, mouseX, mouseY);
    }

    private boolean repeat;
    /**
     * @param repeat {@code true} to allow repeat clicks.
     */
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean getRepeat() {
        return repeat;
    }

    public void setMessage(String name, String value) {
        setMessage(name + ": " + value);
    }

    public void setMessage(String unlocalizedName, String valuePrefix, boolean value) {
        String valueDisplay;

        if(value) {
            valueDisplay = ChatFormatting.GREEN + I18n.format(valuePrefix + ".on");
        } else {
            valueDisplay = ChatFormatting.RED + I18n.format(valuePrefix + ".off");
        }

        setMessage(I18n.format(unlocalizedName), valueDisplay);
        forceHovered = value;
    }
}
