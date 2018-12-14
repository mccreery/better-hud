package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.HudElement.SortType;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.IGetSet;
import jobicade.betterhud.util.Paginator;
import jobicade.betterhud.util.geom.Point;
import jobicade.betterhud.util.SortField;

@SideOnly(Side.CLIENT)
public class GuiHudMenu extends GuiScreen {
	private final Map<HudElement, ButtonRow> rows = new HashMap<HudElement, ButtonRow>(HudElement.ELEMENTS.size());
	final Paginator<HudElement> paginator = new Paginator<HudElement>();

	private final GuiButton returnToGame = new GuiActionButton(I18n.format("menu.returnToGame")).setCallback(b -> MC.displayGuiScreen(null));
	private final GuiButton toggleAll = new GuiActionButton("").setCallback(b -> setAll(!allEnabled()));
	private final GuiButton reorder = new GuiActionButton(I18n.format("betterHud.menu.reorder")).setCallback(b -> MC.displayGuiScreen(new GuiReorder(this)));

	private final GuiButton resetDefaults = new GuiActionButton(I18n.format("betterHud.menu.saveLoad"))
		.setCallback(b -> MC.displayGuiScreen(new GuiConfigSaves(this)));

	private final ButtonRow globalRow = new ButtonRow(this, HudElement.GLOBAL);

	private final GuiButton lastPage = new GuiActionButton(I18n.format("betterHud.menu.lastPage"))
		.setCallback(b -> {paginator.previousPage(); initGui();});

	private final GuiButton nextPage = new GuiActionButton(I18n.format("betterHud.menu.nextPage"))
		.setCallback(b -> {paginator.nextPage(); initGui();});

	private SortField<HudElement> sortCriteria = SortType.ALPHABETICAL;
	private boolean descending;

	public SortField<HudElement> getSortCriteria() {
		return sortCriteria;
	}

	public boolean isDescending() {
		return descending;
	}

	private boolean allEnabled() {
		return HudElement.ELEMENTS.stream().allMatch(e -> e.get());
	}

	public void initGui() {
		paginator.setData(HudElement.SORTER.getSortedData(sortCriteria, descending ^ sortCriteria.isInverted()));
		paginator.setPageSize(Math.max(1, (int) Math.floor((height / 8 * 7 - 134) / 24)));

		addDefaultButtons();
		Rect buttonRect = new Rect(170, 20).align(new Point(width / 2, height / 16 + 102), Direction.NORTH);

		for(HudElement element : paginator.getPage()) {
			ButtonRow row = getRow(element);
			buttonList.addAll(row.getButtons());

			row.setRect(buttonRect);
			row.update();

			buttonRect = buttonRect.withY(buttonRect.getBottom() + 4);
		}
	}

	private void addDefaultButtons() {
		Rect buttons = new Rect(300, 42).align(new Point(width / 2, height / 16 + 20), Direction.NORTH);
		Rect halfWidth = new Rect((buttons.getWidth() - 2) / 2, 20);
		Rect thirdWidth = new Rect((buttons.getWidth() - 4) / 3, 20);

		moveButton(returnToGame,   halfWidth.anchor(buttons, Direction.NORTH_WEST));

		globalRow.setRect(halfWidth.anchor(buttons, Direction.NORTH_EAST));

		moveButton(toggleAll,     thirdWidth.anchor(buttons, Direction.SOUTH_WEST));
		moveButton(reorder,    thirdWidth.anchor(buttons,      Direction.SOUTH));
		moveButton(resetDefaults, thirdWidth.anchor(buttons, Direction.SOUTH_EAST));
		toggleAll.displayString = I18n.format(allEnabled() ? "betterHud.menu.disableAll" : "betterHud.menu.enableAll");

		lastPage.enabled = paginator.hasPrevious();
		nextPage.enabled = paginator.hasNext();

		buttons = buttons.align(new Point(width / 2, height - 20 - height / 16), Direction.NORTH);
		moveButton(lastPage, thirdWidth.anchor(buttons, Direction.NORTH_WEST));
		moveButton(nextPage, thirdWidth.anchor(buttons, Direction.NORTH_EAST));

		buttonList.clear();

		buttonList.add(returnToGame);
		buttonList.addAll(globalRow.getButtons());
		globalRow.update();

		buttonList.add(toggleAll);
		buttonList.add(reorder);
		buttonList.add(resetDefaults);

		buttonList.add(lastPage);
		buttonList.add(nextPage);

		List<GuiActionButton> indexerControls = getIndexControls(SortType.values());
		Rect sortButton = new Rect(75, 20);
		Rect bounds = sortButton.withWidth((sortButton.getWidth() + SPACER) * indexerControls.size() - SPACER).align(new Point(width / 2, height / 16 + 78), Direction.NORTH);
		sortButton = sortButton.move(bounds.getPosition());

		for(GuiActionButton button : indexerControls) {
			button.setRect(sortButton);
			sortButton = sortButton.withX(sortButton.getRight() + SPACER);
		}
		buttonList.addAll(indexerControls);
	}

	private void moveButton(GuiButton button, Rect bounds) {
		button.x = bounds.getX();
		button.y = bounds.getY();
		button.width = bounds.getWidth();
		button.height = bounds.getHeight();
	}

	@Override
	public void onGuiClosed() {
		BetterHud.CONFIG.saveSettings();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button instanceof GuiActionButton) {
			((GuiActionButton)button).actionPerformed();
		}
	}

	private void setAll(boolean enabled) {
		for(HudElement element : HudElement.ELEMENTS) {
			element.set(enabled);
		}

		HudElement.SORTER.markDirty(SortType.ENABLED);
		initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, p_73863_3_);

		int enabled = (int)HudElement.ELEMENTS.stream().filter(HudElement::get).count();

		drawCenteredString(fontRenderer, I18n.format("betterHud.menu.hudSettings"), width / 2, height / 16 + 5, Color.WHITE.getPacked());
		drawString(fontRenderer, enabled + "/" + HudElement.ELEMENTS.size() + " enabled", 5, 5, Color.WHITE.getPacked());

		String page = I18n.format("betterHud.menu.page", (paginator.getPageIndex() + 1) + "/" + paginator.getPageCount());
		drawCenteredString(fontRenderer, page, width / 2, height - height / 16 - 13, Color.WHITE.getPacked());
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

	public boolean showArrows() {
		return sortCriteria == SortType.PRIORITY;
	}

	public void swapPriority(HudElement element, int delta) {
		if(sortCriteria != SortType.PRIORITY) {
			throw new IllegalStateException("Must be sorting by priority");
		}

		List<HudElement> data = paginator.getData();
		swapPriority(element, data.get(data.indexOf(element) + delta));
	}

	public void swapPriority(int first, int second) {
		if(sortCriteria != SortType.PRIORITY) {
			throw new IllegalStateException("Must be sorting by priority");
		}

		List<HudElement> data = paginator.getData();
		swapPriority(data.get(first), data.get(second));
	}

	public void swapPriority(HudElement first, HudElement second) {
		IGetSet.swap(first.settings.priority, second.settings.priority);

		HudElement.SORTER.markDirty(SortType.PRIORITY);
		initGui();
	}

	public void changeSort(SortField<HudElement> sortCriteria) {
		if(this.sortCriteria == sortCriteria) {
			descending = !descending;
		} else {
			this.sortCriteria = sortCriteria;
			descending = sortCriteria.isInverted();
		}

		initGui();
	}
}
