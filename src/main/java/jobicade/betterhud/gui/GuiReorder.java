package jobicade.betterhud.gui;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.IGetSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.SPACER;

public class GuiReorder extends GuiElements {
    private final Screen parent;

    private Rect toolbox;
    private GuiActionButton moveUp = new GuiTexturedButton(new Rect(20, 60, 20, 20));
    private GuiActionButton moveDown = new GuiTexturedButton(new Rect(40, 60, 20, 20));
    private GuiActionButton moveTop = new GuiTexturedButton(new Rect(60, 60, 20, 20));
    private GuiActionButton moveBottom = new GuiTexturedButton(new Rect(80, 60, 20, 20));

    private HudElement hovered;
    private HudElement selected;

    public GuiReorder(Screen parent) {
        this.parent = parent;

        moveTop.setCallback(new ActionMove(false, HudElement.ELEMENTS.size()));
        moveUp.setCallback(new ActionMove(true, 1));
        moveDown.setCallback(new ActionMove(true, -1));
        moveBottom.setCallback(new ActionMove(false, -1));

        moveTop.setTooltip(I18n.get("betterHud.menu.moveTop"));
        moveUp.setTooltip(I18n.get("betterHud.menu.moveUp"));
        moveDown.setTooltip(I18n.get("betterHud.menu.moveDown"));
        moveBottom.setTooltip(I18n.get("betterHud.menu.moveBottom"));
    }

    private class ActionMove implements ActionCallback {
        boolean relative;
        int offset;

        ActionMove(boolean relative, int offset) {
            this.relative = relative;
            this.offset = offset;
        }

        @Override
        public void actionPerformed(GuiActionButton button) {
            if(relative) {
                List<HudElement> elements = HudElement.SORTER.getSortedData(HudElement.SortType.PRIORITY);
                int i = elements.indexOf(selected) + offset;

                if(i >= 0 && i < elements.size()) {
                    IGetSet.swap(selected.settings.priority, elements.get(i).settings.priority);
                    HudElement.SORTER.markDirty(HudElement.SortType.PRIORITY);
                }
            } else {
                selected.settings.priority.set(offset);
                HudElement.normalizePriority();
                HudElement.SORTER.markDirty(HudElement.SortType.PRIORITY);
            }
        }
    }

    private void select(HudElement element) {
        selected = element;
        Rect button = new Rect(20, 20);

        if(element != null) {
            toolbox = button
                .withHeight(button.getHeight() * 4 + 6)
                .align(element.getLastBounds().grow(SPACER, 0, SPACER, 0).getPosition(), Direction.NORTH_WEST);

            toolbox = toolbox.move(
                    MathHelper.clamp(toolbox.getX(), 0, MANAGER.getScreen().getWidth() - toolbox.getWidth()),
                    MathHelper.clamp(toolbox.getY(), 0, MANAGER.getScreen().getHeight() - toolbox.getHeight()));
        } else {
            toolbox = new Rect(MANAGER.getScreen().getSize(), Point.zero());
        }

        moveTop.setBounds(button = button.anchor(toolbox, Direction.NORTH_WEST));
        moveUp.setBounds(button = button.withY(button.getBottom() + 2));
        moveDown.setBounds(button = button.withY(button.getBottom() + 2));
        moveBottom.setBounds(button.withY(button.getBottom() + 2));
    }

    @Override
    public void init() {
        buttons.clear();

        buttons.add(moveTop);
        buttons.add(moveUp);
        buttons.add(moveDown);
        buttons.add(moveBottom);
        select(null);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 1) {
            Minecraft.getInstance().setScreen(parent);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(toolbox.contains((int)mouseX, (int)mouseY)) {
            super.mouseClicked(mouseX, mouseY, button);
        } else {
            select(hovered);
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        hovered = getHoveredElement(mouseX, mouseY, Predicates.alwaysFalse());

        for(HudElement element : HudElement.ELEMENTS) {
            Rect bounds = element.getLastBounds();

            if(!bounds.isEmpty()) {
                drawRect(bounds, element == hovered || element == selected);
            }
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
