package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Ordering;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.config.HudConfig;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.DefaultBoxed;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.settings.KeyModifier;

public class GuiElementList extends GuiMenuScreen {
    private final ConfigManager configManager;

    private GuiScrollbar disabledScroll;
    private Rect disabledViewport;
    private Grid<ListItem> disabledList;

    private GuiScrollbar enabledScroll;
    private Rect enabledViewport;
    private Grid<ListItem> enabledList;

    private List<HudElement<?>> selection = new ArrayList<>();

    private GuiActionButton upButton;
    private GuiActionButton downButton;
    private GuiActionButton configButton;

    private GuiTexturedButton moveButton;
    private GuiActionButton enableAllButton;
    private GuiActionButton disableAllButton;

    public GuiElementList(ConfigManager configManager) {
        this.configManager = configManager;
        setTitle(I18n.format("betterHud.menu.hudSettings"));
    }

    @Override
    public void initGui() {
        super.initGui();

        GuiActionButton backButton = new GuiActionButton(I18n.format("menu.returnToGame"))
            .setBounds(new Rect(200, 20).align(getOrigin(), Direction.NORTH))
            .setCallback(b -> mc.displayGuiScreen(null));

        GuiActionButton saveLoadButton = new GuiActionButton(I18n.format("betterHud.menu.saveLoad"))
            .setBounds(new Rect(150, 20).align(getOrigin().add(-1, 22), Direction.NORTH_EAST))
            .setCallback(b -> mc.displayGuiScreen(new GuiConfigSaves(configManager, this)));

        GuiActionButton modSettingsButton = new GuiActionButton(I18n.format("betterHud.menu.modSettings"))
            .setBounds(new Rect(150, 20).align(getOrigin().add(1, 22), Direction.NORTH_WEST))
            .setCallback(b -> mc.displayGuiScreen(new GuiElementSettings(HudElements.GLOBAL, this)));

        buttonList.add(backButton);
        buttonList.add(saveLoadButton);
        buttonList.add(modSettingsButton);

        disabledViewport = new Rect(200, 0).align(getOrigin().add(-14, 67), Direction.SOUTH_EAST).withBottom(height - 30);
        disabledScroll = new GuiScrollbar(disabledViewport, 0);

        enabledViewport = new Rect(200, 0).align(getOrigin().add(14, 67), Direction.SOUTH_WEST).withBottom(height - 30);
        enabledScroll = new GuiScrollbar(enabledViewport, 0);

        Point centerButtons = disabledViewport.getAnchor(Direction.EAST).add(SPACER, 0);

        moveButton = new GuiTexturedButton(new Rect(120, 0, 20, 20));
        moveButton.setBounds(new Rect(20, 20).align(centerButtons.add(0, -22), Direction.WEST));
        moveButton.setCallback(b -> swapSelected());
        buttonList.add(moveButton);

        enableAllButton = new GuiTexturedButton(new Rect(160, 0, 20, 20));
        enableAllButton.setBounds(new Rect(20, 20).align(centerButtons, Direction.WEST));
        enableAllButton.setCallback(b -> enableAll());
        buttonList.add(enableAllButton);

        disableAllButton = new GuiTexturedButton(new Rect(140, 0, 20, 20));
        disableAllButton.setBounds(new Rect(20, 20).align(centerButtons.add(0, 22), Direction.WEST));
        disableAllButton.setCallback(b -> disableAll());
        buttonList.add(disableAllButton);

        Point rightButtons = enabledViewport.getAnchor(Direction.EAST).add(SPACER, 0);

        upButton = new GuiTexturedButton(new Rect(60, 0, 20, 20));
        upButton.setBounds(new Rect(20, 20).align(rightButtons.add(0, -22), Direction.WEST));
        upButton.setCallback(b -> moveSelectionUp());
        buttonList.add(upButton);

        downButton = new GuiTexturedButton(new Rect(80, 0, 20, 20));
        downButton.setBounds(new Rect(20, 20).align(rightButtons, Direction.WEST));
        downButton.setCallback(b -> moveSelectionDown());
        buttonList.add(downButton);

        configButton = new GuiTexturedButton(new Rect(40, 0, 20, 20));
        configButton.setBounds(new Rect(20, 20).align(rightButtons.add(0, 22), Direction.WEST));
        configButton.setCallback(b -> openSettings());
        buttonList.add(configButton);

        updateLists();
    }

    private void enableAll() {
        HudConfig.moveAll(getDisabled(), getEnabled());
        selection.clear();
        updateLists();
    }

    private void disableAll() {
        HudConfig.moveAll(getEnabled(), getDisabled());
        selection.clear();
        updateLists();
    }

    @Override
    public void onGuiClosed() {
        BetterHud.getProxy().getConfig().saveSettings();
    }

    /**
     * Checks if each button is enabled.
     */
    private void checkButtons() {
        enableAllButton.enabled = !getDisabled().isEmpty();
        disableAllButton.enabled = !getEnabled().isEmpty();

        upButton.enabled = false;
        downButton.enabled = false;
        configButton.enabled = false;

        moveButton.enabled = !selection.isEmpty();
        if (moveButton.enabled) {
            HudElement<?> element = selection.get(0);

            if (getEnabled().contains(element)) {
                moveButton.setTexture(new Rect(100, 0, 20, 20));
                configButton.enabled = true;

                List<Integer> indices = getIndices(getEnabled(), selection);
                upButton.enabled = Collections.min(indices) != 0;
                downButton.enabled = Collections.max(indices) != getEnabled().size() - 1;
            } else {
                moveButton.setTexture(new Rect(120, 0, 20, 20));
            }
        }
    }

    private List<HudElement<?>> getEnabled() {
        return configManager.getConfig().getSelected();
    }

    private List<HudElement<?>> getDisabled() {
        return configManager.getConfig().getAvailable();
    }

    private List<Integer> getIndices(List<?> list, List<?> subList) {
        List<Integer> indices = new ArrayList<>(subList.size());

        for (Object obj : subList) {
            indices.add(list.indexOf(obj));
        }
        return indices;
    }

    private void openSettings() {
        if (!selection.isEmpty()) {
            HudElement<?> element = selection.get(selection.size() - 1);
            mc.displayGuiScreen(new GuiElementSettings(element, this));
        }
    }

    private void swapSelected() {
        if (!selection.isEmpty()) {
            List<HudElement<?>> source, dest;

            // All selection items are from the same list
            if (getEnabled().contains(selection.get(0))) {
                source = getEnabled();
                dest = getDisabled();
            } else {
                source = getDisabled();
                dest = getEnabled();
            }

            for (HudElement<?> element : selection) {
                HudConfig.move(element, source, dest);
            }
        }
        selection.clear();
        updateLists();
    }

    private void moveSelectionUp() {
        List<Integer> indices = getIndices(getEnabled(), selection);

        if (!indices.contains(-1)) {
            shiftLeft(getEnabled(), indices);
            updateLists();
        }
    }

    private void moveSelectionDown() {
        List<Integer> indices = getIndices(getEnabled(), selection);

        if (!indices.contains(-1)) {
            shiftRight(getEnabled(), indices);
            updateLists();
        }
    }

    private void updateLists() {
        disabledList = getList(getDisabled());
        disabledScroll.setContentHeight(disabledList.getPreferredSize().getHeight() + SPACER * 2);

        enabledList = getList(getEnabled());
        enabledScroll.setContentHeight(enabledList.getPreferredSize().getHeight() + SPACER * 2);

        checkButtons();
    }

    private Grid<ListItem> getList(List<HudElement<?>> elements) {
        List<ListItem> items = new ArrayList<>(elements.size());

        for (HudElement<?> element : elements) {
            items.add(new ListItem(element));
        }

        Grid<ListItem> grid = new Grid<>(new Point(1, items.size()), items);
        grid.setStretch(true);

        return grid;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        disabledScroll.mouseClicked(mouseX, mouseY, mouseButton);
        enabledScroll.mouseClicked(mouseX, mouseY, mouseButton);
        mouseClicked(mouseX, mouseY, disabledViewport, disabledScroll, disabledList);
        mouseClicked(mouseX, mouseY, enabledViewport, enabledScroll, enabledList);
    }

    private void mouseClicked(int mouseX, int mouseY, Rect viewport, GuiScrollbar scrollbar, Grid<ListItem> list) {
        if(viewport.contains(mouseX, mouseY) && !scrollbar.getBounds().contains(mouseX, mouseY)) {
            boolean selectedAny = false;

            for(int i = 0; i < list.getSource().size(); i++) {
                Rect listBounds = getListBounds(viewport, scrollbar, list);

                if(list.getCellBounds(listBounds, new Point(0, i)).contains(mouseX, mouseY)) {
                    addToSelection(list.getSource().get(i).element);
                    selectedAny = true;
                }
            }

            if (!selectedAny && !list.getSource().isEmpty()) {
                selection.clear();
            }
            checkButtons();
        }
    }

    private void addToSelection(HudElement<?> element) {
        if (!selection.isEmpty() && isShiftKeyDown()) {
            List<HudElement<?>> selectionSide = getSelectionSide();

            int prevIndex = selectionSide.indexOf(selection.get(selection.size() - 1));
            int index = selectionSide.indexOf(element);

            if (prevIndex < index) {
                for (int i = prevIndex + 1; i <= index; i++) {
                    toggleItem(selection, selectionSide.get(i));
                }
            } else {
                for (int i = prevIndex - 1; i >= index; i--) {
                    toggleItem(selection, selectionSide.get(i));
                }
            }
        } else {
            if (!isCtrlKeyDown()) {
                selection.clear();
            }
            toggleItem(selection, element);
        }
    }

    private List<HudElement<?>> getSelectionSide() {
        if (selection.isEmpty()) {
            return null;
        } else if (getEnabled().contains(selection.get(0))) {
            return getEnabled();
        } else {
            return getDisabled();
        }
    }

    /**
     * If the item is in the list, removes it. Otherwise adds it.
     */
    private <T> void toggleItem(List<T> list, T item) {
        if (!list.remove(item)) {
            list.add(item);
        }
    }

    /**
     * Shifts the item corresponding to each index left by one. Fails if any of
     * the indices are 0.
     *
     * @return {@code true} if successful.
     */
    private boolean shiftLeft(List<?> list, Iterable<Integer> indices) {
        List<Integer> sortedIndices = Ordering.natural()
            .immutableSortedCopy(indices);

        if (sortedIndices.isEmpty() || sortedIndices.get(0) == 0) {
            return false;
        } else {
            for (int i : sortedIndices) {
                Collections.swap(list, i, i - 1);
            }
            return true;
        }
    }

    /**
     * Shifts the item corresponding to each index right by one. Fails if any of
     * the indices are {@code list.size() - 1}.
     *
     * @return {@code true} if successful.
     */
    private boolean shiftRight(List<?> list, Iterable<Integer> indices) {
        List<Integer> sortedIndices = Ordering.natural().reverse()
            .immutableSortedCopy(indices);

        if (sortedIndices.isEmpty() || sortedIndices.get(0) == list.size() - 1) {
            return false;
        } else {
            for (int i : sortedIndices) {
                Collections.swap(list, i, i + 1);
            }
            return true;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long heldTime) {
        super.mouseClickMove(mouseX, mouseY, button, heldTime);
        disabledScroll.mouseClickMove(mouseX, mouseY, button, heldTime);
        enabledScroll.mouseClickMove(mouseX, mouseY, button, heldTime);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        disabledScroll.mouseReleased(mouseX, mouseY, button);
        enabledScroll.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        disabledScroll.handleMouseInput();
        enabledScroll.handleMouseInput();
    }

    private Rect getListBounds(Rect viewport, GuiScrollbar scrollbar, Grid<ListItem> list) {
        Point origin = viewport.getAnchor(Direction.NORTH).sub(0, scrollbar.getScroll() - SPACER);
        return new Rect(list.getPreferredSize().withWidth(150)).align(origin, Direction.NORTH);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawViewport(disabledViewport, disabledScroll, disabledList);
        disabledScroll.drawScrollbar(mouseX, mouseY);

        drawViewport(enabledViewport, enabledScroll, enabledList);
        enabledScroll.drawScrollbar(mouseX, mouseY);

        String hint = I18n.format("betterHud.menu.modifiers",
            KeyModifier.CONTROL.getLocalizedComboName(-100),
            KeyModifier.SHIFT.getLocalizedComboName(-100));

        drawCenteredString(fontRenderer, hint, width / 2, height - fontRenderer.FONT_HEIGHT - SPACER, 0xffffff);

        Point p = disabledViewport.getAnchor(Direction.NORTH).sub(0, fontRenderer.FONT_HEIGHT + SPACER);
        drawCenteredString(fontRenderer, I18n.format("betterHud.menu.disabledElements"), p.getX(), p.getY(), 0xffffff);

        p = enabledViewport.getAnchor(Direction.NORTH).sub(0, fontRenderer.FONT_HEIGHT + SPACER);
        drawCenteredString(fontRenderer, I18n.format("betterHud.menu.enabledElements"), p.getX(), p.getY(), 0xffffff);
    }

    private void drawViewport(Rect viewport, GuiScrollbar scrollbar, Grid<ListItem> list) {
        GlUtil.drawRect(viewport, new Color(32, 0, 0, 0));

        GlUtil.beginScissor(viewport, new ScaledResolution(Minecraft.getMinecraft()));
        list.setBounds(getListBounds(viewport, scrollbar, list)).render();
        GlUtil.endScissor();
    }

    private class ListItem extends DefaultBoxed {
        private final HudElement<?> element;
        private final Label label;

        private ListItem(HudElement<?> element) {
            this.element = element;
            label = new Label(element.getLocalizedName());
        }

        @Override
        public Size getPreferredSize() {
            return label.getPreferredSize().add(40, 0).withHeight(20);
        }

        @Override
        public Size negotiateSize(Point size) {
            Size labelSize = label.getPreferredSize();
            int minWidth = labelSize.getWidth() + 5;
            int minHeight = Math.max(labelSize.getHeight(), 20);

            return new Size(Math.max(minWidth, size.getX()), Math.max(minHeight, size.getY()));
        }

        @Override
        public void render() {
            if (selection.contains(element)) {
                GlUtil.drawRect(bounds, new Color(48, 0, 0, 0));
                GlUtil.drawBorderRect(bounds, new Color(160, 144, 144, 144));
            }
            label.setBounds(new Rect(label.getPreferredSize()).anchor(bounds, Direction.CENTER));

            if (!element.getServerDependency().containsVersion(BetterHud.getServerVersion())) {
                // 16 tall so remove 2 pixels on either side/4 in total
                Rect warningBounds = new Rect(16, 16).anchor(bounds.grow(-2), Direction.WEST);

                Minecraft.getMinecraft().getTextureManager().bindTexture(Textures.SETTINGS);
                GlUtil.drawRect(warningBounds, new Rect(100, 60, 16, 16));

                label.setColor(Color.GRAY);
            } else {
                label.setColor(Color.WHITE);
            }
            label.render();
        }
    }
}
