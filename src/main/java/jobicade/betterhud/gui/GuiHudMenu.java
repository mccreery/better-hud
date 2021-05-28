package jobicade.betterhud.gui;

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
        .setCallback(b -> {paginator.previousPage(); func_73866_w_();});

    private final GuiActionButton nextPage = new GuiActionButton(I18n.get("betterHud.menu.nextPage"))
        .setCallback(b -> {paginator.nextPage(); func_73866_w_();});

    private SortField<HudElement> sortCriteria = SortType.ALPHABETICAL;
    private boolean descending;

    public GuiHudMenu(ConfigManager configManager) {
        resetDefaults.setCallback(button -> {
            Minecraft.getInstance().setScreen(new GuiConfigSaves(configManager, this));
        });
    }

    @Override
    public void func_146281_b() {
        BetterHud.getConfig().saveSettings();
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

    public void func_73866_w_() {
        setTitle(I18n.get("betterHud.menu.hudSettings"));
        paginator.setData(HudElement.SORTER.getSortedData(sortCriteria, descending ^ sortCriteria.isInverted()));
        paginator.setPageSize(Math.max(1, (int) Math.floor((field_146295_m / 8 * 7 - 134) / 24)));

        addDefaultButtons();
        Rect buttonRect = new Rect(170, 20).align(getOrigin().add(0, 82), Direction.NORTH);

        for(HudElement element : paginator.getPage()) {
            ButtonRow row = getRow(element);
            field_146292_n.addAll(row.getButtons());

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
        toggleAll.field_146126_j = I18n.get(allEnabled() ? "betterHud.menu.disableAll" : "betterHud.menu.enableAll");

        lastPage.field_146124_l = paginator.hasPrevious();
        nextPage.field_146124_l = paginator.hasNext();

        buttons = buttons.align(new Point(field_146294_l / 2, field_146295_m - 20 - field_146295_m / 16), Direction.NORTH);
        lastPage.setBounds(thirdWidth.anchor(buttons, Direction.NORTH_WEST));
        nextPage.setBounds(thirdWidth.anchor(buttons, Direction.NORTH_EAST));

        field_146292_n.clear();

        field_146292_n.add(returnToGame);
        field_146292_n.addAll(globalRow.getButtons());
        globalRow.update();

        field_146292_n.add(toggleAll);
        field_146292_n.add(reorder);
        field_146292_n.add(resetDefaults);

        field_146292_n.add(lastPage);
        field_146292_n.add(nextPage);

        List<GuiActionButton> indexerControls = getIndexControls(SortType.values());
        Rect sortButton = new Rect(75, 20);
        Rect bounds = sortButton.withWidth((sortButton.getWidth() + SPACER) * indexerControls.size() - SPACER).align(getOrigin().add(0, 58), Direction.NORTH);
        sortButton = sortButton.move(bounds.getPosition());

        for(GuiActionButton button : indexerControls) {
            button.setBounds(sortButton);
            sortButton = sortButton.withX(sortButton.getRight() + SPACER);
        }
        field_146292_n.addAll(indexerControls);
    }

    private void setAll(boolean enabled) {
        for(HudElement element : HudElement.ELEMENTS) {
            element.set(enabled);
        }

        HudElement.SORTER.markDirty(SortType.ENABLED);
        func_73866_w_();
    }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float p_73863_3_) {
        super.func_73863_a(mouseX, mouseY, p_73863_3_);

        int enabled = (int)HudElement.ELEMENTS.stream().filter(HudElement::get).count();
        GlUtil.drawString(enabled + "/" + HudElement.ELEMENTS.size() + " enabled", new Point(SPACER, SPACER), Direction.NORTH_WEST, Color.WHITE);

        String page = I18n.get("betterHud.menu.page", (paginator.getPageIndex() + 1) + "/" + paginator.getPageCount());
        func_73732_a(field_146289_q, page, field_146294_l / 2, field_146295_m - field_146295_m / 16 - 13, Color.WHITE.getPacked());
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

        func_73866_w_();
    }
}
