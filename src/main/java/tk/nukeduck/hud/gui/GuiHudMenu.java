package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SETTINGS;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.HudElement.SortType;
import tk.nukeduck.hud.gui.GuiActionButton.GuiCallbackButton;
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

	private final GuiButton returnToGame = new GuiCallbackButton(I18n.format("menu.returnToGame"), () -> MC.displayGuiScreen(null));
	private final GuiButton enableAll = new GuiCallbackButton(I18n.format("betterHud.menu.enableAll"), () -> setAll(true));
	private final GuiButton disableAll = new GuiCallbackButton(I18n.format("betterHud.menu.disableAll"), () -> setAll(false));

	private final GuiButton resetDefaults = new GuiCallbackButton(I18n.format("betterHud.menu.saveLoad"),
		() -> MC.displayGuiScreen(new GuiConfigSaves(this)));

	private final GuiToggleButton globalToggle = new GuiElementToggle(this, HudElement.GLOBAL);
	private final GuiActionButton globalSettings = new GuiOptionButton(this, HudElement.GLOBAL);

	private final GuiButton lastPage = new GuiCallbackButton(I18n.format("betterHud.menu.lastPage"), () -> {paginator.previousPage(); initGui();});
	private final GuiButton nextPage = new GuiCallbackButton(I18n.format("betterHud.menu.nextPage"), () -> {paginator.nextPage(); initGui();});

	private SortField<HudElement> sortCriteria = SortType.ALPHABETICAL;
	private boolean descending;

	public void initGui() {
		paginator.setData(HudElement.SORTER.getSortedData(sortCriteria, descending ^ sortCriteria.isInverted()));
		paginator.setPageSize(Math.max(1, (int) Math.floor((height / 8 * 7 - 110) / 24)));

		addDefaultButtons();

		Bounds largeButton = new Bounds(150, 20);
		Bounds smallButton = new Bounds(20, 20);
		Bounds arrowButtons = new Bounds(20, 20);

		Bounds buttonBounds = Direction.NORTH.align(new Bounds(largeButton.getWidth() + smallButton.getWidth(), 20), new Point(width / 2, height / 16 + 78));

		for(HudElement element : paginator.getPage()) {
			ButtonRow row = getRow(element);

			row.toggle.setBounds(Direction.NORTH_WEST.anchor(largeButton, buttonBounds));
			row.toggle.enabled = element.isSupportedByServer();
			row.toggle.updateText();
			buttonList.add(row.toggle);

			row.options.setBounds(Direction.NORTH_EAST.anchor(smallButton, buttonBounds));
			row.options.enabled = row.toggle.enabled && row.toggle.get() && !element.settings.isEmpty();
			buttonList.add(row.options);

			arrowButtons = Direction.WEST.anchor(arrowButtons, buttonBounds);
			arrowButtons = arrowButtons.withX(arrowButtons.getX() - SPACER - arrowButtons.getWidth());

			row.moveUp.setBounds(Direction.NORTH_WEST.anchor(new Bounds(20, 10), arrowButtons));
			buttonList.add(row.moveUp);

			row.moveDown.setBounds(Direction.SOUTH_WEST.anchor(new Bounds(20, 10), arrowButtons));
			buttonList.add(row.moveDown);

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
			getRow(element).toggle.set(enabled);
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
			buttons.add(new SortButton(sortValue));
		}
		return buttons;
	}

	private ButtonRow getRow(HudElement element) {
		return rows.computeIfAbsent(element, e -> new ButtonRow(e));
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
				Point position = Direction.EAST.anchor(new Bounds(arrowTexture), getBounds()).getPosition().add(-2, 0);

				MC.getTextureManager().bindTexture(new ResourceLocation("textures/gui/resource_packs.png"));
				GlUtil.drawTexturedModalRect(position, arrowTexture);
			}
		}
	}

	private static class GuiOptionButton extends GuiCallbackButton {
		GuiOptionButton(GuiScreen parent, HudElement element) {
			super("", () -> MC.displayGuiScreen(new GuiElementSettings(element, parent)));
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			mc.getTextureManager().bindTexture(SETTINGS);
			GlUtil.color(Colors.WHITE);

			this.hovered = getBounds().contains(mouseX, mouseY);
			int k = this.getHoverState(this.hovered);

			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(770, 771);

			drawTexturedModalRect(this.x, this.y, 40, k * 20, this.width, this.height);
		}
	}

	private class ButtonRow {
		final HudElement element;
		final GuiElementToggle toggle;
		final GuiActionButton options;
		final GuiActionButton moveUp, moveDown;

		ButtonRow(HudElement element) {
			this.element = element;

			toggle = new GuiElementToggle(GuiHudMenu.this, element);
			options = new GuiOptionButton(GuiHudMenu.this, element);
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
