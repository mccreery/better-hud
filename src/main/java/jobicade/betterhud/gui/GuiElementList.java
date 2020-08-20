package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

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
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.settings.KeyModifier;

public class GuiElementList extends GuiMenuScreen {
    private final ConfigManager configManager;

    private Scrollbar disabledScroll;
    private Rect disabledViewport;
    private Grid<ListItem> disabledList;

    private Scrollbar enabledScroll;
    private Rect enabledViewport;
    private Grid<ListItem> enabledList;

    private List<HudElement<?>> selection = new ArrayList<>();

    private SuperButton upButton;
    private SuperButton downButton;
    private SuperButton configButton;

    private SuperButton moveButton;
    private SuperButton enableAllButton;
    private SuperButton disableAllButton;

    public GuiElementList(ConfigManager configManager) {
        super(new TranslationTextComponent("betterHud.menu.hudSettings"));
        this.configManager = configManager;
    }

    @Override
    public void init() {
        SuperButton backButton = addButton(new SuperButton(b -> minecraft.displayGuiScreen(null)));
        backButton.setMessage(I18n.format("menu.returnToGame"));
        backButton.setBounds(new Rect(200, 20).align(getOrigin(), Direction.NORTH));

        SuperButton saveLoadButton = addButton(new SuperButton(b -> minecraft.displayGuiScreen(new GuiConfigSaves(configManager, this))));
        saveLoadButton.setMessage(I18n.format("betterHud.menu.saveLoad"));
        saveLoadButton.setBounds(new Rect(150, 20).align(getOrigin().add(-1, 22), Direction.NORTH_EAST));

        SuperButton modSettingsButton = addButton(new SuperButton(b -> minecraft.displayGuiScreen(new GuiElementSettings(HudElements.GLOBAL, this))));
        modSettingsButton.setMessage(I18n.format("betterHud.menu.modSettings"));
        modSettingsButton.setBounds(new Rect(150, 20).align(getOrigin().add(1, 22), Direction.NORTH_WEST));

        disabledViewport = new Rect(200, 0).align(getOrigin().add(-14, 67), Direction.SOUTH_EAST).withBottom(height - 30);
        disabledScroll = new Scrollbar(disabledViewport.getRight() - 8, disabledViewport.getY(), 8, disabledViewport.getHeight(), 0.5f);
        addButton(disabledScroll);

        enabledViewport = new Rect(200, 0).align(getOrigin().add(14, 67), Direction.SOUTH_WEST).withBottom(height - 30);
        disabledScroll = new Scrollbar(enabledViewport.getRight() - 8, enabledViewport.getY(), 8, enabledViewport.getHeight(), 0.5f);
        addButton(enabledScroll);

        Point centerButtons = disabledViewport.getAnchor(Direction.EAST).add(SPACER, 0);

        moveButton = addButton(new SuperButton(b -> swapSelected()));
        moveButton.setBounds(new Rect(20, 20).align(centerButtons.add(0, -22), Direction.WEST));
        moveButton.setTexture(Textures.SETTINGS, 120, 0, 20);

        enableAllButton = addButton(new SuperButton(b -> enableAll()));
        enableAllButton.setBounds(new Rect(20, 20).align(centerButtons, Direction.WEST));
        enableAllButton.setTexture(Textures.SETTINGS, 160, 0, 20);

        disableAllButton = new SuperButton(b -> disableAll());
        disableAllButton.setBounds(new Rect(20, 20).align(centerButtons.add(0, 22), Direction.WEST));
        disableAllButton.setTexture(Textures.SETTINGS, 140, 0, 20);

        Point rightButtons = enabledViewport.getAnchor(Direction.EAST).add(SPACER, 0);

        upButton = addButton(new SuperButton(b -> moveSelectionUp()));
        upButton.setBounds(new Rect(20, 20).align(rightButtons.add(0, -22), Direction.WEST));
        upButton.setTexture(Textures.SETTINGS, 60, 0, 20);

        downButton = addButton(new SuperButton(b -> moveSelectionDown()));
        downButton.setBounds(new Rect(20, 20).align(rightButtons, Direction.WEST));
        downButton.setTexture(Textures.SETTINGS, 80, 0, 20);

        configButton = addButton(new SuperButton(b -> openSettings()));
        configButton.setBounds(new Rect(20, 20).align(rightButtons.add(0, 22), Direction.WEST));
        configButton.setTexture(Textures.SETTINGS, 40, 0, 20);

        updateLists();
    }

    private void enableAll() {
        HudConfig.moveAll(getDisabled(), getEnabled());
        updateLists();
    }

    private void disableAll() {
        HudConfig.moveAll(getEnabled(), getDisabled());
        updateLists();
    }

    @Override
    public void onClose() {
        BetterHud.getConfig().save();
    }

    /**
     * Checks if each button is enabled.
     */
    private void checkButtons() {
        enableAllButton.active = !getDisabled().isEmpty();
        disableAllButton.active = !getEnabled().isEmpty();

        upButton.active = false;
        downButton.active = false;
        configButton.active = false;

        moveButton.active = !selection.isEmpty();
        if (moveButton.active) {
            HudElement<?> element = selection.get(0);

            if (getEnabled().contains(element)) {
                moveButton.setTexture(Textures.SETTINGS, 100, 0, 20);
                configButton.active = true;

                List<Integer> indices = getIndices(getEnabled(), selection);
                upButton.active = Collections.min(indices) != 0;
                downButton.active = Collections.max(indices) != getEnabled().size() - 1;
            } else {
                moveButton.setTexture(Textures.SETTINGS, 120, 0, 20);
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
            minecraft.displayGuiScreen(new GuiElementSettings(element, this));
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
        int disabledHeight = disabledList.getPreferredSize().getHeight() + SPACER * 2;
        disabledScroll.setThumbSize((float)disabledHeight / disabledViewport.getHeight());

        enabledList = getList(getEnabled());
        int enabledHeight = enabledList.getPreferredSize().getHeight() + SPACER * 2;
        enabledScroll.setThumbSize((float)enabledHeight / enabledViewport.getHeight());

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
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseClicked((int)mouseX, (int)mouseY, disabledViewport, disabledScroll, disabledList)
                || mouseClicked((int)mouseX, (int)mouseY, enabledViewport, enabledScroll, enabledList)) {
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    private Rect getBounds(Widget widget) {
        return new Rect(widget.x, widget.y, widget.getWidth(), widget.getHeight());
    }

    private boolean mouseClicked(int mouseX, int mouseY, Rect viewport, Scrollbar scrollbar, Grid<ListItem> list) {
        if(viewport.contains(mouseX, mouseY) && !getBounds(scrollbar).contains(mouseX, mouseY)) {
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
            return selectedAny;
        } else {
            return false;
        }
    }

    private void addToSelection(HudElement<?> element) {
        if (!selection.isEmpty() && hasShiftDown()) {
            List<HudElement<?>> selectionSide = getSelectionSide();

            int index = selectionSide.indexOf(element);
            if (index == -1) {
                return;
            }
            int prevIndex = selectionSide.indexOf(selection.get(selection.size() - 1));

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
            if (!hasControlDown()) {
                selection.clear();
            } else {
                List<HudElement<?>> selectionSide = getSelectionSide();
                if (selectionSide != null && !selectionSide.contains(element)) {
                    return;
                }
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

    private Rect getListBounds(Rect viewport, Scrollbar scrollbar, Grid<ListItem> list) {
        int scroll = Math.round(scrollbar.getValue() * (list.getPreferredSize().getHeight() - viewport.getHeight()));

        Point origin = viewport.getAnchor(Direction.NORTH).sub(0, scroll - SPACER);
        return new Rect(list.getPreferredSize().withWidth(150)).align(origin, Direction.NORTH);
    }

    private int mouseX;
    private int mouseY;

    // Set to true to show an unsupported warning tooltip on top of the GUI
    private boolean showWarning;

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        drawViewport(disabledViewport, disabledScroll, disabledList);
        drawViewport(enabledViewport, enabledScroll, enabledList);

        super.render(mouseX, mouseY, partialTicks);

        this.mouseX = mouseX;
        this.mouseY = mouseY;
        showWarning = false;

        String hint = I18n.format("betterHud.menu.modifiers",
            KeyModifier.CONTROL.getLocalizedComboName(null, () -> I18n.format("key.mouse.left")),
            KeyModifier.SHIFT.getLocalizedComboName(null, () -> I18n.format("key.mouse.left")));

        drawCenteredString(font, hint, width / 2, height - font.FONT_HEIGHT - SPACER, 0xffffff);

        Point p = disabledViewport.getAnchor(Direction.NORTH).sub(0, font.FONT_HEIGHT + SPACER);
        drawCenteredString(font, I18n.format("betterHud.menu.disabledElements"), p.getX(), p.getY(), 0xffffff);

        p = enabledViewport.getAnchor(Direction.NORTH).sub(0, font.FONT_HEIGHT + SPACER);
        drawCenteredString(font, I18n.format("betterHud.menu.enabledElements"), p.getX(), p.getY(), 0xffffff);

        if (showWarning) {
            renderTooltip(I18n.format("betterHud.menu.unsupported"), mouseX, mouseY);
            // Side-effect is enabling item lighting
            RenderHelper.disableStandardItemLighting();
        }
    }

    private void drawViewport(Rect viewport, Scrollbar scrollbar, Grid<ListItem> list) {
        GlUtil.drawRect(viewport, new Color(32, 0, 0, 0));

        GlUtil.beginScissor(viewport);
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
                if (bounds.contains(mouseX, mouseY)) {
                    showWarning = true;
                }
                // 16 tall so remove 2 pixels on either side/4 in total
                Rect warningBounds = new Rect(16, 16).anchor(bounds.grow(-2), Direction.WEST);

                MC.getTextureManager().bindTexture(Textures.SETTINGS);
                GlUtil.drawRect(warningBounds, new Rect(100, 60, 16, 16));

                label.setColor(Color.GRAY);
            } else {
                label.setColor(Color.WHITE);
            }
            label.render();
        }
    }
}
