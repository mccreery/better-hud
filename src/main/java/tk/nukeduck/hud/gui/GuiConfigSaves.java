package tk.nukeduck.hud.gui;

import static tk.nukeduck.hud.BetterHud.CONFIG;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.MODID;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import tk.nukeduck.hud.gui.GuiActionButton.GuiCallbackButton;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.StringGroup;

public class GuiConfigSaves extends GuiScreen {
	private GuiTextField name;
	private GuiScrollbar scrollbar;
	private Bounds viewport;

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
		scrollbar.setContentHeight(MC.fontRenderer.FONT_HEIGHT * saves.size());
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

	@Override
	public void initGui() {
		super.initGui();

		Point origin = new Point(width / 2, height / 16 + 20);

		buttonList.add(new GuiCallbackButton(I18n.format("gui.done"), () -> MC.displayGuiScreen(previous))
			.setBounds(Direction.NORTH.align(new Bounds(200, 20), origin)));

		Bounds textField = new Bounds(150, 20);
		Bounds smallButton = new Bounds(50, 20);

		Bounds fieldLine = Direction.NORTH.align(new Bounds(textField.getWidth() + (SPACER + smallButton.getWidth()) * 2, 20), origin.add(0, 20 + SPACER));
		textField = Direction.NORTH_WEST.anchor(textField, fieldLine);

		name = new GuiTextField(0, fontRenderer, textField.getX(), textField.getY(), textField.getWidth(), textField.getHeight());
		name.setFocused(true);
		name.setCanLoseFocus(false);

		smallButton = smallButton.withPosition(Direction.NORTH_EAST.getAnchor(textField).add(SPACER, 0));
		buttonList.add(new GuiCallbackButton("Load", () -> System.out.println("bum")).setBounds(smallButton));

		smallButton = smallButton.withPosition(Direction.NORTH_EAST.getAnchor(smallButton).add(SPACER, 0));
		buttonList.add(new GuiCallbackButton("Save", this::save).setBounds(smallButton));

		viewport = Direction.NORTH.align(new Bounds(400, 0), Direction.SOUTH.getAnchor(fieldLine).add(0, SPACER)).withBottom(height - 20);
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
			i /= MC.fontRenderer.FONT_HEIGHT + 2;

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
			name = name.substring(0, name.length() - 4);

			if(name.equals(this.name.getText())) {
				name = ChatFormatting.RED + name;
			}
			return name;
		}).collect(Collectors.toList()));

		displaySaves.setAlignment(Direction.NORTH);

		Bounds scissorBounds = viewport.withY(height - viewport.getBottom()).scaled(MANAGER.getScaledResolution().getScaleFactor());

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_SCISSOR_BIT);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(scissorBounds.getX(), scissorBounds.getY(), scissorBounds.getWidth(), scissorBounds.getHeight());

		displaySaves.draw(Direction.NORTH.getAnchor(viewport).sub(0, scrollbar.getScroll()));

		GL11.glPopAttrib();
	}
}
