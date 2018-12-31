package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.render.Boxed;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.config.ConfigSlot;
import jobicade.betterhud.config.FileConfigSlot;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.geom.Point;

public class GuiConfigSaves extends GuiScreen {
	private GuiTextField name;
	private GuiScrollbar scrollbar;
	private Rect viewport;

	private final GuiScreen previous;
	private final ConfigManager manager;

	private Grid<ListItem> list;
	private ConfigSlot selected;

	private GuiActionButton load, save;

	public GuiConfigSaves(ConfigManager manager, GuiScreen previous) {
		this.previous = previous;
		this.manager = manager;
	}

	private ConfigSlot getSelectedEntry() {
		if(StringUtils.isBlank(name.getText())) return null;

		return list.getSource().stream().map(li -> li.entry)
			.filter(e -> e.matches(name.getText())).findFirst()
			.orElseGet(() -> new FileConfigSlot(manager.getRootDirectory().resolve(name.getText() + ".cfg")));
	}

	private void updateSelected() {
		selected = getSelectedEntry();
		load.enabled = selected != null;
		save.enabled = selected != null && selected.isDest();
	}

	private void save() {
		try {
			manager.getConfig().saveSettings();
			selected.copyFrom(manager.getConfigPath());
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void load() {
		try {
			selected.copyTo(manager.getConfigPath());
			manager.reloadConfig();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		Point origin = new Point(width / 2, height / 16 + 20);

		buttonList.add(new GuiActionButton(I18n.format("gui.done"))
			.setCallback(b -> MC.displayGuiScreen(previous))
			.setRect(new Rect(200, 20).align(origin, Direction.NORTH)));

		Rect textField = new Rect(150, 20);
		Rect smallButton = new Rect(50, 20);

		Rect fieldLine = new Rect(textField.getWidth() + (SPACER + smallButton.getWidth()) * 2, 20).align(origin.add(0, 20 + SPACER), Direction.NORTH);
		textField = textField.anchor(fieldLine, Direction.NORTH_WEST);

		name = new GuiTextField(0, fontRenderer, textField.getX(), textField.getY(), textField.getWidth(), textField.getHeight());
		name.setFocused(true);
		name.setCanLoseFocus(false);

		smallButton = smallButton.move(textField.getAnchor(Direction.NORTH_EAST).add(SPACER, 0));
		buttonList.add(load = new GuiActionButton("Load").setCallback(b -> load()).setRect(smallButton));

		smallButton = smallButton.move(smallButton.getAnchor(Direction.NORTH_EAST).add(SPACER, 0));
		buttonList.add(save = new GuiActionButton("Save").setCallback(b -> save()).setRect(smallButton));

		updateSelected();

		viewport = new Rect(400, 0).align(fieldLine.getAnchor(Direction.SOUTH).add(0, SPACER), Direction.NORTH).withBottom(height - 20);
		scrollbar = new GuiScrollbar(viewport, 0);

		List<ListItem> listItems = manager.getSlots().stream().map(ListItem::new).collect(Collectors.toList());
		list = new Grid<>(new Point(1, listItems.size()), listItems);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		name.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		name.textboxKeyTyped(typedChar, keyCode);
		updateSelected();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		name.mouseClicked(mouseX, mouseY, mouseButton);
		scrollbar.mouseClicked(mouseX, mouseY, mouseButton);

		for(int i = 0; i < list.getSource().size(); i++) {
			Rect bounds = new Rect(list.getPreferredSize().withWidth(this.width)).withY(150);

			if(list.getCellBounds(bounds, new Point(0, i)).contains(mouseX, mouseY)) {
				name.setText(list.getSource().get(i).entry.getName());
				updateSelected();
			}
		}
/*
		if(viewport.contains(mouseX, mouseY)) {
			int i = mouseY - viewport.getY() + scrollbar.getScroll();
			i /= MC.fontRenderer.FONT_HEIGHT + SPACER;

			if(i >= 0 && i < saves.size()) {
				String filename = saves.get(i).getFileName().toString();
				name.setText(filename.substring(0, filename.length() - 4));
			}
		}*/
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long heldTime) {
		super.mouseClickMove(mouseX, mouseY, button, heldTime);
		scrollbar.mouseClickMove(mouseX, mouseY, button, heldTime);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
		scrollbar.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		scrollbar.handleMouseInput();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button instanceof GuiActionButton) {
			((GuiActionButton)button).actionPerformed();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		name.drawTextBox();
		scrollbar.drawScrollbar(mouseX, mouseY);
/*
		StringGroup displaySaves = new StringGroup(saves.stream().map(path -> {
			String name = path.getFileName().toString();
			return name.substring(0, name.length() - 4);
		}).collect(Collectors.toList())).setAlignment(Direction.NORTH).setGutter(SPACER);*/

/*		Rect scissorRect = viewport.withY(height - viewport.getBottom()).scale(new ScaledResolution(MC).getScaleFactor());

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_SCISSOR_BIT);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(scissorRect.getX(), scissorRect.getY(), scissorRect.getWidth(), scissorRect.getHeight());

		Point origin = viewport.getAnchor(Direction.NORTH).sub(0, scrollbar.getScroll() - SPACER);

		for(int i = 0; i < saves.size(); i++) {
			String fileName = saves.get(i).getFileName().toString();

			if(fileName.length() == name.getText().length() + 4 && fileName.regionMatches(0, name.getText(), 0, fileName.length() - 4)) {
				Rect bounds = new Rect(300, MC.fontRenderer.FONT_HEIGHT).align(origin.add(0, (MC.fontRenderer.FONT_HEIGHT + SPACER) * i), Direction.NORTH).grow(2);

				GlUtil.drawRect(bounds, new Color(48, 0, 0, 0));
				GlUtil.drawBorderRect(bounds, new Color(160, 144, 144, 144));

				break;
			}
		}
		displaySaves.draw(origin);

		GL11.glPopAttrib();*/

		list.render(new Rect(list.getPreferredSize().withWidth(this.width)).withY(150));
	}

	private class ListItem implements Boxed {
		private final ConfigSlot entry;
		private final Label label;

		private ListItem(ConfigSlot entry) {
			this.entry = entry;

			this.label = new Label(entry.getName());
			if(!entry.isDest()) label.setColor(Color.GRAY);
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
		public void render(Rect bounds) {
			if(selected == this.entry) {
				GlUtil.drawRect(bounds, new Color(48, 0, 0, 0));
				GlUtil.drawBorderRect(bounds, new Color(160, 144, 144, 144));
			}
			label.render(new Rect(label.getPreferredSize()).anchor(bounds, Direction.CENTER));
		}
	}
}
