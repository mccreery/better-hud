package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Ordering;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.config.ConfigManager;
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

	private List<Integer> disabledSelection = new ArrayList<>();
	private List<Integer> enabledSelection = new ArrayList<>();

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
			.setBounds(new Rect(150, 20).align(getOrigin().add(1, 22), Direction.NORTH_WEST));

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
		List<HudElement<?>> disabled = new ArrayList<>(HudElements.get().getDisabled());

		for (HudElement<?> element : disabled) {
			HudElements.get().enableElement(element);
		}

		disabledSelection.clear();
		enabledSelection.clear();
		updateLists();
	}

	private void disableAll() {
		List<HudElement<?>> enabled = new ArrayList<>(HudElements.get().getEnabled());

		for (HudElement<?> element : enabled) {
			HudElements.get().disableElement(element);
		}

		disabledSelection.clear();
		enabledSelection.clear();
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
		enableAllButton.enabled = !HudElements.get().getDisabled().isEmpty();
		disableAllButton.enabled = !HudElements.get().getEnabled().isEmpty();

		if (!disabledSelection.isEmpty()) {
			moveButton.enabled = true;
			moveButton.setTexture(new Rect(120, 0, 20, 20));
		} else if (!enabledSelection.isEmpty()) {
			moveButton.enabled = true;
			moveButton.setTexture(new Rect(100, 0, 20, 20));
		} else {
			moveButton.enabled = false;
		}

		if (enabledSelection.isEmpty()) {
			upButton.enabled = false;
			downButton.enabled = false;
		} else {
			upButton.enabled = Collections.min(enabledSelection) != 0;
			downButton.enabled = Collections.max(enabledSelection) != HudElements.get().getEnabled().size() - 1;
		}
		configButton.enabled = !enabledSelection.isEmpty();
	}

	private void openSettings() {
		if (!enabledSelection.isEmpty()) {
			int i = enabledSelection.get(enabledSelection.size() - 1);
			HudElement<?> element = HudElements.get().getEnabled().get(i);
			mc.displayGuiScreen(new GuiElementSettings(element, this));
		}
	}

	private void swapSelected() {
		HudElements registry = HudElements.get();

		for (HudElement<?> element : getAll(registry.getDisabled(), disabledSelection)) {
			registry.enableElement(element);
		}
		for (HudElement<?> element : getAll(registry.getEnabled(), enabledSelection)) {
			registry.disableElement(element);
		}

		disabledSelection.clear();
		enabledSelection.clear();
		updateLists();
	}

	private void moveSelectionUp() {
		if (shiftLeft(HudElements.get().getEnabled(), enabledSelection)) {
			for (int i = 0; i < enabledSelection.size(); i++) {
				enabledSelection.set(i, enabledSelection.get(i) - 1);
			}
			updateLists();
		}
	}

	private void moveSelectionDown() {
		if (shiftRight(HudElements.get().getEnabled(), enabledSelection)) {
			for (int i = 0; i < enabledSelection.size(); i++) {
				enabledSelection.set(i, enabledSelection.get(i) + 1);
			}
			updateLists();
		}
	}

	private <T> List<T> getAll(List<? extends T> list, List<Integer> indices) {
		return indices.stream().map(list::get).collect(Collectors.toList());
	}

	private void updateLists() {
		disabledList = getList(HudElements.get().getDisabled(), disabledSelection);
		disabledScroll.setContentHeight(disabledList.getPreferredSize().getHeight() + SPACER * 2);

		enabledList = getList(HudElements.get().getEnabled(), enabledSelection);
		enabledScroll.setContentHeight(enabledList.getPreferredSize().getHeight() + SPACER * 2);

		checkButtons();
	}

	private Grid<ListItem> getList(List<HudElement<?>> elements, List<Integer> selection) {
		List<ListItem> items = new ArrayList<>(elements.size());
		for (int i = 0; i < elements.size(); i++) {
			items.add(new ListItem(elements.get(i), selection, i));
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
					addToSelection(list.getSource().get(i).selection, i);
					selectedAny = true;
				}
			}

			if (!selectedAny && !list.getSource().isEmpty()) {
				list.getSource().get(0).selection.clear();
			}
			checkButtons();
		}
	}

	private void addToSelection(List<Integer> selection, int index) {
		if (!selection.isEmpty() && isShiftKeyDown()) {
			int prevIndex = selection.get(selection.size() - 1);

			if (prevIndex < index) {
				for (int i = prevIndex + 1; i <= index; i++) {
					toggleItem(selection, i);
				}
			} else {
				for (int i = prevIndex - 1; i >= index; i--) {
					toggleItem(selection, i);
				}
			}
		} else {
			if (!isCtrlKeyDown()) {
				selection.clear();
			}
			toggleItem(selection, index);
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
		private final List<Integer> selection;
		private final int index;

		private final Label label;

		private ListItem(HudElement<?> element, List<Integer> selection, int index) {
			this.selection = selection;
			this.index = index;

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
			if (selection.contains(index)) {
				GlUtil.drawRect(bounds, new Color(48, 0, 0, 0));
				GlUtil.drawBorderRect(bounds, new Color(160, 144, 144, 144));
			}
			label.setBounds(new Rect(label.getPreferredSize()).anchor(bounds, Direction.CENTER)).render();
		}
	}
}
