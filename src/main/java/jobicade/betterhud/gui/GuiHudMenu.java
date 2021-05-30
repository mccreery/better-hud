package jobicade.betterhud.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Paginator;
import jobicade.betterhud.util.SortField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jobicade.betterhud.BetterHud.SPACER;

public class GuiHudMenu extends GuiMenuScreen {
    private final Map<HudElement, ButtonRow> rows = new HashMap<HudElement, ButtonRow>(HudElement.ELEMENTS.size());
    final Paginator<HudElement> paginator = new Paginator<HudElement>();

    private final GuiActionButton returnToGame = new GuiActionButton(I18n.get("menu.returnToGame")).setCallback(b -> Minecraft.getInstance().setScreen(null));
    private final GuiActionButton toggleAll = new GuiActionButton("").setCallback(b -> setAll(!allEnabled()));
    private final GuiActionButton reorder = new GuiActionButton(I18n.get("betterHud.menu.reorder")).setCallback(b -> Minecraft.getInstance().setScreen(new GuiReorder(this)));

    private final GuiActionButton resetDefaults = new GuiActionButton(I18n.get("betterHud.menu.saveLoad"));

    private final ButtonRow globalRow = new ButtonRow(this, HudElement.GLOBAL);

    private final GuiActionButton lastPage = new GuiActionButton(I18n.get("betterHud.menu.lastPage"))
        .setCallback(b -> {paginator.previousPage(); init();});

    private final GuiActionButton nextPage = new GuiActionButton(I18n.get("betterHud.menu.nextPage"))
        .setCallback(b -> {paginator.nextPage(); init();});

    private SortField<HudElement> sortCriteria = SortType.ALPHABETICAL;
    private boolean descending;

    public GuiHudMenu(ConfigManager configManager) {
        resetDefaults.setCallback(button -> {
            Minecraft.getInstance().setScreen(new GuiConfigSaves(configManager, this));
        });
    }

    @Override
    public void onClose() {
        BetterHud.getConfig().save();
        super.onClose();
    }

    public SortField<HudElement> getSortCriteria() {
        return sortCriteria;
    }

    public boolean isDescending() {
        return descending;
    }

    private boolean allEnabled() {
        return HudElement.ELEMENTS.stream().allMatch(e -> e.get());
    }

    public void init() {
        setTitle(I18n.get("betterHud.menu.hudSettings"));
        paginator.setData(HudElement.SORTER.getSortedData(sortCriteria, descending ^ sortCriteria.isInverted()));
        paginator.setPageSize(Math.max(1, (int) Math.floor((height / 8 * 7 - 134) / 24)));

        addDefaultButtons();
        Rect buttonRect = new Rect(170, 20).align(getOrigin().add(0, 82), Direction.NORTH);

        for(HudElement element : paginator.getPage()) {
            ButtonRow row = getRow(element);
            buttons.addAll(row.getButtons());

            row.setBounds(buttonRect);
            row.update();

            buttonRect = buttonRect.withY(buttonRect.getBottom() + 4);
        }
    }

    private void addDefaultButtons() {
        Rect buttons = new Rect(300, 42).align(getOrigin(), Direction.NORTH);
        Rect halfWidth = new Rect((buttons.getWidth() - 2) / 2, 20);
        Rect thirdWidth = new Rect((buttons.getWidth() - 4) / 3, 20);

        returnToGame.setBounds(halfWidth.anchor(buttons, Direction.NORTH_WEST));

        globalRow.setBounds(halfWidth.anchor(buttons, Direction.NORTH_EAST));

        toggleAll.setBounds(thirdWidth.anchor(buttons, Direction.SOUTH_WEST));
        reorder.setBounds(thirdWidth.anchor(buttons,      Direction.SOUTH));
        resetDefaults.setBounds(thirdWidth.anchor(buttons, Direction.SOUTH_EAST));
        toggleAll.setMessage(new TranslationTextComponent(allEnabled() ? "betterHud.menu.disableAll" : "betterHud.menu.enableAll"));

        lastPage.active = paginator.hasPrevious();
        nextPage.active = paginator.hasNext();

        buttons = buttons.align(new Point(width / 2, height - 20 - height / 16), Direction.NORTH);
        lastPage.setBounds(thirdWidth.anchor(buttons, Direction.NORTH_WEST));
        nextPage.setBounds(thirdWidth.anchor(buttons, Direction.NORTH_EAST));

        this.buttons.clear();

        this.buttons.add(returnToGame);
        this.buttons.addAll(globalRow.getButtons());
        globalRow.update();

        this.buttons.add(toggleAll);
        this.buttons.add(reorder);
        this.buttons.add(resetDefaults);

        this.buttons.add(lastPage);
        this.buttons.add(nextPage);

        List<GuiActionButton> indexerControls = getIndexControls(SortType.values());
        Rect sortButton = new Rect(75, 20);
        Rect bounds = sortButton.withWidth((sortButton.getWidth() + SPACER) * indexerControls.size() - SPACER).align(getOrigin().add(0, 58), Direction.NORTH);
        sortButton = sortButton.move(bounds.getPosition());

        for(GuiActionButton button : indexerControls) {
            button.setBounds(sortButton);
            sortButton = sortButton.withX(sortButton.getRight() + SPACER);
        }
        this.buttons.addAll(indexerControls);
    }

    private void setAll(boolean enabled) {
        for(HudElement element : HudElement.ELEMENTS) {
            element.set(enabled);
        }

        HudElement.SORTER.markDirty(SortType.ENABLED);
        init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        int enabled = (int)HudElement.ELEMENTS.stream().filter(HudElement::get).count();
        GlUtil.drawString(matrixStack, enabled + "/" + HudElement.ELEMENTS.size() + " enabled", new Point(SPACER, SPACER), Direction.NORTH_WEST, Color.WHITE);

        String page = I18n.get("betterHud.menu.page", (paginator.getPageIndex() + 1) + "/" + paginator.getPageCount());
        drawCenteredString(matrixStack, font, page, width / 2, height - height / 16 - 13, Color.WHITE.getPacked());
    }

    private List<GuiActionButton> getIndexControls(SortField<HudElement>[] sortValues) {
        List<GuiActionButton> buttons = new ArrayList<GuiActionButton>(sortValues.length);

        for(SortField<HudElement> sortValue : sortValues) {
            buttons.add(new SortButton(this, sortValue));
        }
        return buttons;
    }

    private ButtonRow getRow(HudElement element) {
        return rows.computeIfAbsent(element, e -> new ButtonRow(this, e));
    }

    public void changeSort(SortField<HudElement> sortCriteria) {
        if(this.sortCriteria == sortCriteria) {
            descending = !descending;
        } else {
            this.sortCriteria = sortCriteria;
            descending = sortCriteria.isInverted();
        }

        init();
    }
}
