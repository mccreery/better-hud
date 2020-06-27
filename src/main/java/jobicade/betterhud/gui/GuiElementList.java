package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;
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

	private HudElement<?> selected;

	private GuiButton backButton;
	private GuiButton swapButton;
	private GuiButton upButton, downButton, configButton;

	@Override
	public void initGui() {
		super.initGui();
		Point origin = new Point(width / 2, height / 16 + 20);

		disabledViewport = new Rect(300, 0).align(origin.sub(14, 0), Direction.SOUTH_EAST).withBottom(height - 20);
		disabledScroll = new GuiScrollbar(disabledViewport, 0);

		enabledViewport = new Rect(300, 0).align(origin.add(14, 0), Direction.SOUTH_WEST).withBottom(height - 20);
		enabledScroll = new GuiScrollbar(enabledViewport, 0);

		backButton = new GuiButton(0, (width - 200) / 2, origin.getY() - 24, 200, 20, I18n.format("menu.returnToGame"));

		Point center = disabledViewport.getAnchor(Direction.CENTER).add(enabledViewport.getAnchor(Direction.CENTER)).scale(0.5f, 0.5f);
		swapButton = new GuiButton(1, center.getX() - 10, center.getY() - 10, 20, 20, "<>");

		Point rightAnchor = enabledViewport.getAnchor(Direction.EAST).add(4, 0);
		upButton = new GuiButton(2, rightAnchor.getX(), rightAnchor.getY() - 44, 20, 20, "Up");
		downButton = new GuiButton(3, rightAnchor.getX(), rightAnchor.getY() - 22, 20, 20, "Down");
		configButton = new GuiButton(4, rightAnchor.getX(), rightAnchor.getY(), 20, 20, "Config");

		buttonList.add(backButton);
		buttonList.add(swapButton);
		buttonList.add(upButton);
		buttonList.add(downButton);
		buttonList.add(configButton);
		updateLists();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button instanceof GuiActionButton) {
			((GuiActionButton)button).actionPerformed();
		} else {
			switch (button.id) {
				case 0: mc.displayGuiScreen(null); break;
				case 1: break;
			}
		}
	}

	private void updateLists() {
		disabledList = getList(HudElements.get().getDisabled());
		disabledScroll.setContentHeight(disabledList.getPreferredSize().getHeight());

		enabledList = getList(HudElements.get().getEnabled());
		enabledScroll.setContentHeight(enabledList.getPreferredSize().getHeight());
	}

	private Grid<ListItem> getList(List<HudElement<?>> elements) {
		List<ListItem> items = elements.stream().map(ListItem::new).collect(Collectors.toList());
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
		if(viewport.contains(mouseX, mouseY)) {
			for(int i = 0; i < list.getSource().size(); i++) {
				Rect listBounds = getListBounds(viewport, scrollbar, list);

				if(list.getCellBounds(listBounds, new Point(0, i)).contains(mouseX, mouseY)) {
					selected = list.getSource().get(i).element;
				}
			}
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
		return new Rect(list.getPreferredSize().withWidth(200)).align(origin, Direction.NORTH);
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
			if(selected == element) {
				GlUtil.drawRect(bounds, new Color(48, 0, 0, 0));
				GlUtil.drawBorderRect(bounds, new Color(160, 144, 144, 144));
			}
			label.setBounds(new Rect(label.getPreferredSize()).anchor(bounds, Direction.CENTER)).render();
		}
	}
}
