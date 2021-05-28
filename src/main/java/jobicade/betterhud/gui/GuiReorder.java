package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.*;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.IGetSet;
import jobicade.betterhud.geom.Point;

public class GuiReorder extends GuiElements {
    private final GuiScreen parent;

    private Rect toolbox;
    private GuiActionButton moveUp = new GuiTexturedButton(new Rect(20, 60, 20, 20));
    private GuiActionButton moveDown = new GuiTexturedButton(new Rect(40, 60, 20, 20));
    private GuiActionButton moveTop = new GuiTexturedButton(new Rect(60, 60, 20, 20));
    private GuiActionButton moveBottom = new GuiTexturedButton(new Rect(80, 60, 20, 20));

    private HudElement hovered;
    private HudElement selected;

    public GuiReorder(GuiScreen parent) {
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
    public void func_73866_w_() {
        field_146292_n.clear();

        field_146292_n.add(moveTop);
        field_146292_n.add(moveUp);
        field_146292_n.add(moveDown);
        field_146292_n.add(moveBottom);
        select(null);
    }

    @Override
    protected void func_146284_a(GuiButton button) throws IOException {
        if(button instanceof GuiActionButton)
            ((GuiActionButton)button).actionPerformed();
    }

    @Override
    protected void func_73869_a(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1) {
            Minecraft.getInstance().setScreen(parent);
        }
    }

    @Override
    protected void func_73864_a(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(toolbox.contains(mouseX, mouseY)) {
            super.func_73864_a(mouseX, mouseY, mouseButton);
        } else {
            select(hovered);
        }
    }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        hovered = getHoveredElement(mouseX, mouseY, Predicates.alwaysFalse());

        for(HudElement element : HudElement.ELEMENTS) {
            Rect bounds = element.getLastBounds();

            if(!bounds.isEmpty()) {
                drawRect(bounds, element == hovered || element == selected);
            }
        }
        super.func_73863_a(mouseX, mouseY, partialTicks);
    }
}
