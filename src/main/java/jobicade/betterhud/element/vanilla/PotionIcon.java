package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.DefaultBoxed;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.render.Quad;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

public class PotionIcon extends DefaultBoxed {
    private final PotionEffect effect;
    private final Potion potion;

    private final boolean showDuration;

    public PotionIcon(PotionEffect effect, boolean showDuration) {
        this.effect = effect;
        this.potion = effect.getPotion();
        this.showDuration = showDuration;
    }

    @Override
    public void render() {
        Color iconColor = getIconColor();
        Rect background = getIconBackground();
        Rect iconBounds = background.anchor(bounds, Direction.NORTH);

        MC.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
        new Quad().setTexture(background).setBounds(iconBounds).render();

        iconColor.apply();
        Rect iconInnerBounds = new Rect(18, 18).anchor(iconBounds, Direction.CENTER);

        if(potion.hasStatusIcon()) {
            int index = potion.getStatusIconIndex();
            Rect icon = new Rect((index % 8) * 18, 198 + (index / 8) * 18, 18, 18);

            new Quad().setTexture(icon).setBounds(iconInnerBounds).render();
        }
        potion.renderHUDEffect(iconBounds.getX(), iconBounds.getY(), effect, MC, iconColor.getAlpha() / 255.0f);

        String levelLabel = getLevelLabel();
        if(levelLabel != null) {
            Label label = new Label(levelLabel);
            label.setBounds(new Rect(label.getPreferredSize()).anchor(iconInnerBounds, Direction.SOUTH_EAST)).render();
        }

        String durationLabel = getDurationLabel();
        if(durationLabel != null) {
            Label label = new Label(durationLabel);
            label.setBounds(new Rect(label.getPreferredSize()).anchor(iconBounds.grow(2), Direction.SOUTH, true)).render();
        }

        MC.getTextureManager().bindTexture(Gui.ICONS);
        Color.WHITE.apply();
    }

    @Override
    public Size negotiateSize(Point size) {
        String label = getDurationLabel();
        if(label == null) return new Size(24, 24);
        Size labelSize = new Label(label).getPreferredSize();

        int width = Math.max(24, labelSize.getWidth());
        return new Size(width, 26 + labelSize.getHeight());
    }

    private String getDurationLabel() {
        if(potion.shouldRenderInvText(effect) && showDuration) {
            return Potion.getPotionDurationString(effect, 1.0f);
        } else {
            return null;
        }
    }

    private String getLevelLabel() {
        if(potion.shouldRenderInvText(effect)) {
            int amplifier = effect.getAmplifier();

            if(amplifier > 0) {
                String unlocalized = "enchantment.level." + (amplifier + 1);

                if(I18n.hasKey(unlocalized)) {
                    return I18n.format(unlocalized);
                }
            }
        }
        return null;
    }

    private Color getIconColor() {
        if(effect.getIsAmbient() || effect.getDuration() > 200) {
            return Color.WHITE;
        } else {
            int duration = effect.getDuration();
            double center = MathHelper.clamp(duration / 100.0, 0.0, 0.5);
            double oscillate = Math.cos(Math.PI / 5.0 * duration) * MathHelper.clamp(10.0 - duration / 800.0, 0.0, 0.25);

            return Color.WHITE.withAlpha((int)Math.round((center + oscillate) * 255.0));
        }
    }

    private Rect getIconBackground() {
        return new Rect(effect.getIsAmbient() ? 165 : 141, 166, 24, 24);
    }
}
