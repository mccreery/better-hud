package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.HudElement.SortType;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Paginator;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.SortField;

@SideOnly(Side.CLIENT)
public class GuiHudMenu extends GuiScreen {
	private final Map<HudElement, ButtonRow> rows = new HashMap<HudElement, ButtonRow>(HudElement.ELEMENTS.size());
	private final Paginator<HudElement> paginator = new Paginator<HudElement>();

	private final GuiButton returnToGame   = new GuiButton(0, 0, 0, I18n.format("menu.returnToGame"));
	private final GuiButton enableAll      = new GuiButton(0, 0, 0, I18n.format("betterHud.menu.enableAll"));
	private final GuiButton disableAll     = new GuiButton(0, 0, 0, I18n.format("betterHud.menu.disableAll"));
	private final GuiButton resetDefaults  = new GuiButton(0, 0, 0, I18n.format("betterHud.menu.resetDefaults"));
	private final GuiButton globalSettings = new GuiButton(0, 0, 0, I18n.format("betterHud.menu.settings", HudElement.GLOBAL.getLocalizedName()));

	private final GuiButton lastPage = new GuiButton(0, 0, 0, I18n.format("betterHud.menu.lastPage"));
	private final GuiButton nextPage = new GuiButton(0, 0, 0, I18n.format("betterHud.menu.nextPage"));

	private SortField<HudElement> sortCriteria = SortType.ALPHABETICAL;
	private boolean descending;

	public GuiHudMenu() {
		for(HudElement element : HudElement.ELEMENTS) {
			rows.put(element, new ButtonRow(element));
		}
	}

	public void initGui() {
		paginator.setData(HudElement.SORTER.getSortedData(sortCriteria, descending ^ sortCriteria.isInverted()));
		paginator.setPageSize(Math.max(1, (int) Math.floor((height / 8 * 7 - 110) / 24)));

		addDefaultButtons();

		Bounds largeButton = new Bounds(150, 20);
		Bounds smallButton = new Bounds(100, 20);
		Bounds arrowButtons = new Bounds(20, 20);

		Bounds buttonBounds = Direction.NORTH.align(new Bounds(largeButton.width() + smallButton.width() + 2, 20), new Point(width / 2, height / 16 + 78));

		for(HudElement element : paginator.getPage()) {
			ButtonRow row = rows.get(element);

			row.toggle.setBounds(Direction.NORTH_WEST.anchor(largeButton, buttonBounds));
			row.toggle.enabled = element.isSupportedByServer();
			row.toggle.updateText();
			buttonList.add(row.toggle);

			row.options.setBounds(Direction.NORTH_EAST.anchor(smallButton, buttonBounds));
			row.options.enabled = row.toggle.enabled && !element.settings.isEmpty();
			buttonList.add(row.options);

			Direction.WEST.anchor(arrowButtons, buttonBounds);
			arrowButtons.x(arrowButtons.x() - SPACER - arrowButtons.width());

			row.moveUp.setBounds(Direction.NORTH_WEST.anchor(new Bounds(20, 10), arrowButtons));
			buttonList.add(row.moveUp);

			row.moveDown.setBounds(Direction.SOUTH_WEST.anchor(new Bounds(20, 10), arrowButtons));
			buttonList.add(row.moveDown);

			buttonBounds.y(buttonBounds.bottom() + 4);
		}
	}

	private void addDefaultButtons() {
		Bounds buttons = Direction.NORTH.align(new Bounds(300, 42), new Point(width / 2, height / 16 + 20));
		Bounds halfWidth = new Bounds((buttons.width() - 2) / 2, 20);
		Bounds thirdWidth = new Bounds((buttons.width() - 4) / 3, 20);

		moveButton(returnToGame,   Direction.NORTH_WEST.anchor(halfWidth, buttons));
		moveButton(globalSettings, Direction.NORTH_EAST.anchor(halfWidth, buttons));

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
			bounds.x(bounds.right() + SPACER);
		}
		buttonList.addAll(indexerControls);
	}

	private void moveButton(GuiButton button, Bounds bounds) {
		button.x = bounds.x();
		button.y = bounds.y();
		button.width = bounds.width();
		button.height = bounds.height();
	}

	private void closeMe() {
		mc.displayGuiScreen((GuiScreen)null);

		if(this.mc.currentScreen == null) {
			mc.setIngameFocus();
		}
		BetterHud.CONFIG.saveSettings();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1) {
			closeMe();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button == returnToGame) {
			closeMe();
		} else if(button == enableAll) {
			setAll(true);
		} else if(button == disableAll) {
			setAll(false);
		} else if(button == resetDefaults) {
			HudElement.loadAllDefaults();
			HudElement.SORTER.markDirty();
			initGui();
		} else if(button == lastPage) {
			paginator.previousPage();
			initGui();
		} else if(button == nextPage) {
			paginator.nextPage();
			initGui();
		} else if(button instanceof GuiActionButton) {
			((GuiActionButton)button).actionPerformed();
		}
	}

	private void setAll(boolean enabled) {
		for(HudElement element : HudElement.ELEMENTS) {
			rows.get(element).toggle.set(enabled);
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

		drawString(MC.fontRenderer, String.valueOf(descending), 5, 5, Colors.WHITE);
	}

	private List<GuiActionButton> getIndexControls(SortField<HudElement>[] sortValues) {
		List<GuiActionButton> buttons = new ArrayList<GuiActionButton>(sortValues.length);

		for(SortField<HudElement> sortValue : sortValues) {
			buttons.add(new SortButton(sortValue));
		}
		return buttons;
	}

	private class SortButton extends GuiActionButton {
		SortField<HudElement> sortValue;

		SortButton(SortField<HudElement> sortValue) {
			super(I18n.format(sortValue.getUnlocalizedName()));

			this.sortValue = sortValue;
		}

		@Override
		public void actionPerformed() {
			if(isTargeted()) {
				descending = !descending;
			} else {
				sortCriteria = sortValue;
				descending = sortValue.isInverted();
			}

			initGui();
		}

		private boolean isTargeted() {
			return sortCriteria == sortValue;
		}

		@Override
		protected int getHoverState(boolean mouseOver) {
			return isTargeted() ? 2 : super.getHoverState(mouseOver);
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			super.drawButton(mc, mouseX, mouseY, partialTicks);

			if(isTargeted()) {
				Bounds arrowTexture;

				if(descending) {
					arrowTexture = new Bounds(82, 20, 11, 7);
				} else {
					arrowTexture = new Bounds(114, 5, 11, 7);
				}
				Point position = Direction.EAST.anchor(new Bounds(arrowTexture), getBounds()).position.add(-2, 0);

				MC.getTextureManager().bindTexture(new ResourceLocation("textures/gui/resource_packs.png"));
				GlUtil.drawTexturedModalRect(position, arrowTexture);
			}
		}
	}

	private class ButtonRow {
		final HudElement element;
		final GuiElementToggle toggle;
		final GuiActionButton options;
		final GuiActionButton moveUp, moveDown;

		ButtonRow(HudElement element) {
			this.element = element;
			toggle = new GuiElementToggle(element, GuiHudMenu.this);

			options = new GuiActionButton(I18n.format("betterHud.menu.options")) {
				@Override
				public void actionPerformed() {
					MC.displayGuiScreen(new GuiElementSettings(element, GuiHudMenu.this));
				}
			};

			moveUp = new ArrowButton(true);
			moveDown = new ArrowButton(false);
		}

		private class ArrowButton extends GuiUpDownButton {
			final boolean up;

			ArrowButton(boolean up) {
				super(up ? 0 : 1);
				this.up = up;
			}

			@Override
			public void actionPerformed() {
				int swapIndex = paginator.getData().indexOf(element);
				swapIndex += up ? -1 : 1;

				if(swapIndex >= 0 && swapIndex < paginator.getData().size()) {
					HudElement toSwap = paginator.getData().get(swapIndex);

					int tempPriority = element.settings.priority.get();
					element.settings.priority.set(toSwap.settings.priority.get());
					toSwap.settings.priority.set(tempPriority);

					HudElement.SORTER.markDirty(SortType.PRIORITY);
					initGui();
				}
			}

			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				if(sortCriteria == SortType.PRIORITY) {
					super.drawButton(mc, mouseX, mouseY, partialTicks);
				} else {
					hovered = false;
				}
			}

			@Override
			public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
				return sortCriteria == SortType.PRIORITY && super.mousePressed(mc, mouseX, mouseY);
			}
		}
	}
}
