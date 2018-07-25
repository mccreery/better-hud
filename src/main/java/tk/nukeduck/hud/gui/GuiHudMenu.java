package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.HudElement.SortType;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.ISaveLoad.IGetSet;
import tk.nukeduck.hud.util.Paginator;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.SortField;

@SideOnly(Side.CLIENT)
public class GuiHudMenu extends GuiScreen {
	private final Map<HudElement, ButtonRow> rows = new HashMap<HudElement, ButtonRow>(HudElement.ELEMENTS.size());
	final Paginator<HudElement> paginator = new Paginator<HudElement>();

	private final GuiButton returnToGame = new GuiActionButton(I18n.format("menu.returnToGame")).setCallback(() -> MC.displayGuiScreen(null));
	private final GuiButton enableAll = new GuiActionButton(I18n.format("betterHud.menu.enableAll")).setCallback(() -> setAll(true));
	private final GuiButton disableAll = new GuiActionButton(I18n.format("betterHud.menu.disableAll")).setCallback(() -> setAll(false));

	private final GuiButton resetDefaults = new GuiActionButton(I18n.format("betterHud.menu.saveLoad"))
		.setCallback(() -> MC.displayGuiScreen(new GuiConfigSaves(this)));

	private final GuiToggleButton globalToggle   = new GuiElementToggle(this, HudElement.GLOBAL);
	private final GuiActionButton globalSettings = new GuiOptionButton(this, HudElement.GLOBAL);

	private final GuiButton lastPage = new GuiActionButton(I18n.format("betterHud.menu.lastPage"))
		.setCallback(() -> {paginator.previousPage(); initGui();});

	private final GuiButton nextPage = new GuiActionButton(I18n.format("betterHud.menu.nextPage"))
		.setCallback(() -> {paginator.nextPage(); initGui();});

	private SortField<HudElement> sortCriteria = SortType.ALPHABETICAL;
	private boolean descending;

	public SortField<HudElement> getSortCriteria() {
		return sortCriteria;
	}

	public boolean isDescending() {
		return descending;
	}

	public void initGui() {
		paginator.setData(HudElement.SORTER.getSortedData(sortCriteria, descending ^ sortCriteria.isInverted()));
		paginator.setPageSize(Math.max(1, (int) Math.floor((height / 8 * 7 - 110) / 24)));

		addDefaultButtons();
		Bounds buttonBounds = Direction.NORTH.align(new Bounds(170, 20), new Point(width / 2, height / 16 + 78));

		for(HudElement element : paginator.getPage()) {
			ButtonRow row = getRow(element);
			buttonList.addAll(row.getButtons());

			row.setBounds(buttonBounds);
			row.update();

			buttonBounds = buttonBounds.withY(buttonBounds.getBottom() + 4);
		}
	}

	private void addDefaultButtons() {
		Bounds buttons = Direction.NORTH.align(new Bounds(300, 42), new Point(width / 2, height / 16 + 20));
		Bounds halfWidth = new Bounds((buttons.getWidth() - 2) / 2, 20);
		Bounds thirdWidth = new Bounds((buttons.getWidth() - 4) / 3, 20);

		moveButton(returnToGame,   Direction.NORTH_WEST.anchor(halfWidth, buttons));

		Bounds global = Direction.NORTH_EAST.anchor(halfWidth, buttons);
		moveButton(globalToggle,   Direction.NORTH_WEST.anchor(halfWidth.withWidth(halfWidth.getWidth() - 20), global));
		moveButton(globalSettings, Direction.NORTH_EAST.anchor(halfWidth.withWidth(20), global));

		moveButton(enableAll,     Direction.SOUTH_WEST.anchor(thirdWidth, buttons));
		moveButton(disableAll,    Direction.SOUTH.anchor(thirdWidth, buttons));
		moveButton(resetDefaults, Direction.SOUTH_EAST.anchor(thirdWidth, buttons));

		lastPage.enabled = paginator.hasPrevious();
		nextPage.enabled = paginator.hasNext();

		buttons = Direction.NORTH.align(buttons, new Point(width / 2, height - 20 - height / 16));
		moveButton(lastPage, Direction.NORTH_WEST.anchor(thirdWidth, buttons));
		moveButton(nextPage, Direction.NORTH_EAST.anchor(thirdWidth, buttons));

		buttonList.clear();

		buttonList.add(returnToGame);
		buttonList.add(globalToggle);
		globalToggle.updateText();
		buttonList.add(globalSettings);

		buttonList.add(enableAll);
		buttonList.add(disableAll);
		buttonList.add(resetDefaults);

		buttonList.add(lastPage);
		buttonList.add(nextPage);

		List<GuiActionButton> indexerControls = getIndexControls(SortType.values());
		Bounds bounds = new Bounds(5, height - 25, 50, 20);

		for(GuiActionButton button : indexerControls) {
			button.setBounds(bounds);
			bounds = bounds.withX(bounds.getRight() + SPACER);
		}
		buttonList.addAll(indexerControls);
	}

	private void moveButton(GuiButton button, Bounds bounds) {
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
			element.setEnabled(enabled);
		}

		HudElement.SORTER.markDirty(SortType.ENABLED);
		initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, p_73863_3_);

		int enabled = 0;
		for(HudElement element : HudElement.ELEMENTS) {
			if(element.settings.get()) ++enabled;
		}

		drawCenteredString(fontRenderer, I18n.format("betterHud.menu.hudSettings"), width / 2, height / 16 + 5, Colors.WHITE);
		drawString(fontRenderer, enabled + "/" + HudElement.ELEMENTS.size() + " enabled", 5, 5, Colors.WHITE);

		String page = I18n.format("betterHud.menu.page", (paginator.getPageIndex() + 1) + "/" + paginator.getPageCount());
		drawCenteredString(fontRenderer, page, width / 2, height - height / 16 - 13, Colors.WHITE);
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

	// TODO check we're in priority mode

	public void swapPriority(HudElement element, int delta) {
		List<HudElement> data = paginator.getData();
		swapPriority(element, data.get(data.indexOf(element) + delta));
	}

	public void swapPriority(int first, int second) {
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
