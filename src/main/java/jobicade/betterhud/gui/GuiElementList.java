package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;

public class GuiElementList extends GuiScreen {
	private GuiScrollbar disabledScroll;
	private Rect disabledViewport;
	private Grid<ListItem> disabledList;

	private GuiScrollbar enabledScroll;
	private Rect enabledViewport;
	private Grid<ListItem> enabledList;

	private List<Integer> disabledSelection = new ArrayList<>();
	private List<Integer> enabledSelection = new ArrayList<>();

	@Override
	public void initGui() {
		super.initGui();
		Point origin = new Point(width / 2, height / 16 + 20);

		disabledViewport = new Rect(200, 0).align(origin.sub(14, 0), Direction.SOUTH_EAST).withBottom(height - 20);
		disabledScroll = new GuiScrollbar(disabledViewport, 0);

		enabledViewport = new Rect(200, 0).align(origin.add(14, 0), Direction.SOUTH_WEST).withBottom(height - 20);
		enabledScroll = new GuiScrollbar(enabledViewport, 0);

		GuiActionButton backButton = new GuiActionButton(I18n.format("menu.returnToGame"))
			.setBounds(new Rect((width - 200) / 2, origin.getY() - 24, 200, 20))
			.setCallback(b -> mc.displayGuiScreen(null));
		buttonList.add(backButton);

		Point center = disabledViewport.getAnchor(Direction.CENTER).add(enabledViewport.getAnchor(Direction.CENTER)).scale(0.5f, 0.5f);
		GuiActionButton swapButton = new GuiActionButton("<>")
			.setBounds(new Rect(center.getX() - 10, center.getY() - 10, 20, 20))
			.setCallback(b -> swapSelected());
		buttonList.add(swapButton);

		Point rightAnchor = enabledViewport.getAnchor(Direction.EAST).add(4, 0);
		GuiActionButton upButton = new GuiTexturedButton(new Rect(60, 0, 20, 20))
			.setBounds(new Rect(rightAnchor.getX(), rightAnchor.getY() - 44, 20, 20));
		buttonList.add(upButton);

		GuiActionButton downButton = new GuiTexturedButton(new Rect(80, 0, 20, 20))
			.setBounds(new Rect(rightAnchor.getX(), rightAnchor.getY() - 22, 20, 20));
		buttonList.add(downButton);

		GuiActionButton configButton = new GuiTexturedButton(new Rect(40, 0, 20, 20))
			.setBounds(new Rect(rightAnchor.getX(), rightAnchor.getY(), 20, 20));
		buttonList.add(configButton);

		updateLists();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button instanceof GuiActionButton) {
			((GuiActionButton)button).actionPerformed();
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

	private <T> List<T> getAll(List<? extends T> list, List<Integer> indices) {
		return indices.stream().map(list::get).collect(Collectors.toList());
	}

	private void updateLists() {
		disabledList = getList(HudElements.get().getDisabled(), disabledSelection);
		disabledScroll.setContentHeight(disabledList.getPreferredSize().getHeight() + SPACER * 2);

		enabledList = getList(HudElements.get().getEnabled(), enabledSelection);
		enabledScroll.setContentHeight(enabledList.getPreferredSize().getHeight() + SPACER * 2);
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

			if (!selectedAny) {
				list.getSource().get(0).selection.clear();
			}
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
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		drawViewport(disabledViewport, disabledScroll, disabledList);
		disabledScroll.drawScrollbar(mouseX, mouseY);

		drawViewport(enabledViewport, enabledScroll, enabledList);
		enabledScroll.drawScrollbar(mouseX, mouseY);
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
