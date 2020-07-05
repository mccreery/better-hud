package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Boxed;
import jobicade.betterhud.render.Grid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionBar extends OverlayElement {
    public static final ResourceLocation INVENTORY = new ResourceLocation("textures/gui/container/inventory.png");

    private SettingPosition position;
    private SettingBoolean showDuration;

    public PotionBar() {
        super("potionBar");

        settings.addChildren(
            position = new SettingPosition(DirectionOptions.X, DirectionOptions.CORNERS),
            showDuration = new SettingBoolean("duration").setValuePrefix(SettingBoolean.VISIBLE)
        );
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return !Minecraft.getMinecraft().player.getActivePotionEffects().isEmpty();
    }

    @Override
    public Rect render(OverlayContext context) {
        Boxed grid = getGrid();

        Rect bounds = new Rect(grid.getPreferredSize());
        if(position.isDirection(Direction.CENTER)) {
            bounds = bounds.align(MANAGER.getScreen().getAnchor(Direction.CENTER).add(SPACER, SPACER), Direction.NORTH_WEST);
        } else {
            bounds = position.applyTo(bounds);
        }
        grid.setBounds(bounds).render();
        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);

        return bounds;
    }

    private void populateEffects(List<PotionEffect> helpful, List<PotionEffect> harmful) {
        Iterable<PotionEffect> activeEffects =  Minecraft.getMinecraft().player.getActivePotionEffects();

        for (PotionEffect effect : activeEffects) {
            if (!effect.doesShowParticles() || !effect.getPotion().shouldRenderHUD(effect)) {
                continue;
            }

            if (effect.getPotion().isBeneficial()) {
                helpful.add(effect);
            } else {
                harmful.add(effect);
            }
        }
        helpful.sort(Collections.reverseOrder());
        harmful.sort(Collections.reverseOrder());
    }

    private void fillRow(Grid<? super PotionIcon> grid, int row, List<PotionEffect> effects) {
        for(int i = 0; i < effects.size(); i++) {
            grid.setCell(new Point(i, row), new PotionIcon(effects.get(i), showDuration.get()));
        }
    }

    private Boxed getGrid() {
        List<PotionEffect> helpful = new ArrayList<>(), harmful = new ArrayList<>();
        populateEffects(helpful, harmful);

        int rows = 0;
        if(!helpful.isEmpty()) ++rows;
        if(!harmful.isEmpty()) ++rows;

        Grid<PotionIcon> grid = new Grid<>(new Point(Math.max(helpful.size(), harmful.size()), rows));
        grid.setAlignment(position.getContentAlignment());
        grid.setGutter(new Point(1, 2));

        int row = 0;
        if(!helpful.isEmpty()) fillRow(grid, row++, helpful);
        if(!harmful.isEmpty()) fillRow(grid, row++, harmful);

        return grid;
    }
}
