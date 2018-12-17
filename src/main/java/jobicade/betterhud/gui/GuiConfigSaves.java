package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.CONFIG;
import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.MODID;
import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.HudConfig;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.util.StringGroup;

public class GuiConfigSaves extends GuiScreen {
	private GuiTextField name;
	private GuiScrollbar scrollbar;
	private Rect viewport;

	private final GuiScreen previous;

	private final List<Path> saves = new ArrayList<Path>();

	public GuiConfigSaves(GuiScreen previous) {
		this.previous = previous;
	}

	private Path getDirectory() throws IOException {
		Path directory = Paths.get(CONFIG.getConfigFile().getParent(), MODID);
		Files.createDirectories(directory);

		return directory;
	}

	private void reloadSaves() {
		saves.clear();

		try {
			Files.list(getDirectory()).filter(path -> path.getFileName().toString().endsWith(".cfg")).collect(Collectors.toCollection(() -> saves));
		} catch(IOException e) {
			e.printStackTrace();
		}
		scrollbar.setContentHeight((MC.fontRenderer.FONT_HEIGHT + SPACER) * saves.size() + SPACER * 2);
	}

	private void save() {
		if(name.getText().isEmpty()) return;
		CONFIG.save();

		try {
			Path destination = Paths.get(getDirectory().toString(), name.getText() + ".cfg");
			Files.copy(CONFIG.getConfigFile().toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
		} catch(IOException e) {
			e.printStackTrace();
		}

		reloadSaves();
	}

	private void load() {
		try {
			Path source = Paths.get(getDirectory().toString(), name.getText() + ".cfg");
			Files.copy(source, CONFIG.getConfigFile().toPath(), StandardCopyOption.REPLACE_EXISTING);

			CONFIG = new HudConfig(CONFIG.getConfigFile());
		} catch(IOException e) {
			e.printStackTrace();
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
		buttonList.add(new GuiActionButton("Load").setCallback(b -> load()).setRect(smallButton));

		smallButton = smallButton.move(smallButton.getAnchor(Direction.NORTH_EAST).add(SPACER, 0));
		buttonList.add(new GuiActionButton("Save").setCallback(b -> save()).setRect(smallButton));

		viewport = new Rect(400, 0).align(fieldLine.getAnchor(Direction.SOUTH).add(0, SPACER), Direction.NORTH).withBottom(height - 20);
		scrollbar = new GuiScrollbar(viewport, 0);

		reloadSaves();
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
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		name.mouseClicked(mouseX, mouseY, mouseButton);
		scrollbar.mouseClicked(mouseX, mouseY, mouseButton);

		if(viewport.contains(mouseX, mouseY)) {
			int i = mouseY - viewport.getY() + scrollbar.getScroll();
			i /= MC.fontRenderer.FONT_HEIGHT + SPACER;

			if(i >= 0 && i < saves.size()) {
				String filename = saves.get(i).getFileName().toString();
				name.setText(filename.substring(0, filename.length() - 4));
			}
		}
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

		StringGroup displaySaves = new StringGroup(saves.stream().map(path -> {
			String name = path.getFileName().toString();
			return name.substring(0, name.length() - 4);
		}).collect(Collectors.toList())).setAlignment(Direction.NORTH).setGutter(SPACER);

		Rect scissorRect = viewport.withY(height - viewport.getBottom()).scale(new ScaledResolution(MC).getScaleFactor());

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

		GL11.glPopAttrib();
	}
}
