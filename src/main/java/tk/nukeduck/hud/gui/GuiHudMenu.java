package tk.nukeduck.hud.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.Colors;

@SideOnly(Side.CLIENT)
public class GuiHudMenu extends GuiScreen {
	public int buttonOffset = 0;
	public int currentPage = 0;
	public int perPage = 10;

	public void initGui() {
		super.initGui();
		this.mc = Minecraft.getMinecraft();

		this.buttonList.clear();
		//boolean flag = true;

		int hW = this.width / 2;
		int yBase = this.height / 16 + 42;

		this.buttonList.add(new GuiButton(0, hW - 152, yBase - 22, 150, 20, I18n.format("menu.returnToGame")));
		this.buttonList.add(new GuiButton(1, hW - 152, yBase, 98, 20, I18n.format("betterHud.menu.enableAll")));
		this.buttonList.add(new GuiButton(2, hW - 49, yBase, 98, 20, I18n.format("betterHud.menu.disableAll")));
		this.buttonList.add(new GuiButton(3, hW + 54, yBase, 98, 20, I18n.format("betterHud.menu.resetDefaults")));

		this.buttonList.add(new GuiButton(4, hW - 129, height - 20 - height / 16, 98, 20, I18n.format("betterHud.menu.lastPage")));
		((GuiButton) buttonList.get(4)).enabled = false;
		this.buttonList.add(new GuiButton(5, hW + 31, height - 20 - height / 16, 98, 20, I18n.format("betterHud.menu.nextPage")));

		// Global settings button
		this.buttonList.add(new GuiButton(6, hW + 2, yBase - 22, 150, 20, I18n.format("betterHud.menu.settings", HudElement.GLOBAL.getLocalizedName())));

		buttonOffset = this.buttonList.size(); // Set the button offset for actual element buttons

		this.perPage = Math.max(1, (int) Math.floor((height / 8 * 7 - 110) / 24));

		int id = buttonOffset;
		int top = height / 16 + 78;

		for(int i = 0; i < HudElement.ELEMENTS.length; i++) {
			HudElement element = HudElement.ELEMENTS[i];

			GuiButton enabled = new GuiSettingToggle(id++, hW - 126, top + ((i % perPage) * 24), 150, 20, element.getUnlocalizedName(), element.settings.enabled);
			enabled.enabled = element.isSupportedByServer();

			this.buttonList.add(enabled);

			GuiButton options = new GuiButton(id++, hW + 26, top + ((i % perPage) * 24), 100, 20, I18n.format("betterHud.menu.options"));
			options.enabled = enabled.enabled && !element.settings.isEmpty();
			this.buttonList.add(options);
		}
		updatePage();
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

	private void updatePage() {
		((GuiButton) buttonList.get(4)).enabled = currentPage != 0;
		((GuiButton) buttonList.get(5)).enabled = currentPage != Math.ceil((float) HudElement.ELEMENTS.length / perPage) - 1;

		for(int i = 0; i < HudElement.ELEMENTS.length; i++) {
			int offset = i * 2 + buttonOffset;
			boolean a = i >= (currentPage * perPage) && i < ((currentPage + 1) * perPage);
			((GuiButton) this.buttonList.get(offset)).visible = a;
			((GuiButton) this.buttonList.get(offset + 1)).visible = a;
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		int id = button.id;

		if(id < buttonOffset) {
			switch(id) {
			case 0:
				closeMe();
				break;
			case 1:
				setAll(true);
				break;
			case 2:
				setAll(false);
				break;
			case 3:
				HudElement.loadAllDefaults();
				initGui();
				break;
			case 4:
				currentPage--;
				updatePage();
				break;
			case 5:
				currentPage++;
				updatePage();
				break;
			case 6:
				GuiElementSettings gui = new GuiElementSettings(HudElement.GLOBAL, this);
				mc.displayGuiScreen(gui);
				break;
			}
		} else {
			id -= buttonOffset;

			if((id & 1) == 1) {
				HudElement element = HudElement.ELEMENTS[id / 2];
				mc.displayGuiScreen(new GuiElementSettings(element, this));
			} else {
				((GuiToggleButton)button).toggle();
			}
		}
	}

	private void setAll(boolean enabled) {
		for(GuiButton button : buttonList) {
			if(button instanceof GuiSettingToggle) {
				((GuiSettingToggle) button).set(enabled);
			}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		if(currentPage < 0) {
			currentPage = 0;
		}

		if(currentPage > HudElement.ELEMENTS.length / perPage) {
			currentPage = HudElement.ELEMENTS.length / perPage;
		}

		updatePage();

		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, p_73863_3_);
		
		for(Object obj : this.buttonList) {
			if(!(obj instanceof GuiToggleButton)) continue;
			GuiButton button = (GuiButton) obj;
			
			if(button.isMouseOver() && !button.enabled) {
				List<String> tooltip = new ArrayList<String>();
				tooltip.add(I18n.format("betterHud.unsupported"));
				this.drawHoveringText(tooltip, mouseX, mouseY);
			}
		}

		this.drawCenteredString(this.fontRenderer, I18n.format("betterHud.menu.hudSettings"), this.width / 2, height / 16 + 5, 16777215);
		this.drawString(this.fontRenderer, countEnabled(HudElement.ELEMENTS) + "/" + HudElement.ELEMENTS.length + " enabled", 5, 5, Colors.WHITE);
		this.drawCenteredString(this.fontRenderer, I18n.format("betterHud.menu.page", (currentPage + 1) + "/" + (int) Math.ceil((float) HudElement.ELEMENTS.length / perPage)), width / 2, height - height / 16 - 13, Colors.WHITE);
	}

	public int countEnabled(HudElement[] elements) {
		int i = 0;
		for(HudElement element : elements) {
			if(element.settings.get()) i++;
		}
		return i;
	}
}
