package tk.nukeduck.hud.gui;

import java.util.Arrays;

import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.ExtraGuiElement;
import tk.nukeduck.hud.util.RenderUtil;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

public class GuiHUDMenu extends GuiScreen {
	public int buttonOffset = 0;
	public int currentPage = 0;
	public int perPage = 10;
	
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
    	super.initGui();
    	this.mc = Minecraft.getMinecraft();
    	
        this.buttonList.clear();
        boolean flag = true;

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, height / 16 + 20, I18n.format("menu.returnToGame", new Object[0])));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 149, height / 16 + 42, 98, 20, I18n.format("betterHud.menu.enableAll", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 49, height / 16 + 42, 98, 20, I18n.format("betterHud.menu.disableAll", new Object[0])));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 51, height / 16 + 42, 98, 20, I18n.format("betterHud.menu.resetDefaults", new Object[0])));
        
        this.buttonList.add(new GuiButton(4, this.width / 2 - 129, height - 20 - height / 16, 98, 20, I18n.format("betterHud.menu.lastPage", new Object[0])));
        ((GuiButton) buttonList.get(4)).enabled = false;
        this.buttonList.add(new GuiButton(5, this.width / 2 + 31, height - 20 - height / 16, 98, 20, I18n.format("betterHud.menu.nextPage", new Object[0])));
        
        buttonOffset = this.buttonList.size(); // Set the button offset for actual element buttons
        
        //int perColumn = 10;
        //int columns = (int) Math.ceil(BetterHud.elements.length / (float) perColumn);
        
        this.perPage = height / 48 + 1;
        System.out.println(perPage);
        
        for(int i = 0; i < BetterHud.elements.length; i++) {
        	ExtraGuiElement el = BetterHud.elements[i];
        	this.buttonList.add(new GuiButton(
        		i * 3 + buttonOffset, (int) (this.width / 2) - 120, height / 16 + 75 + ((i % perPage) * 24), 128, 20, (el.enabled ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + I18n.format("betterHud.element." + el.getName(), new Object[] {})
        	));
        	this.buttonList.add(new GuiButton(
            	i * 3 + buttonOffset + 1, (int) (this.width / 2) - 120 + 130, height / 16 + 75 + ((i % perPage) * 24), 20, 20, "<"
            ));
        	this.buttonList.add(new GuiButton(
                i * 3 + buttonOffset + 2, (int) (this.width / 2) - 120 + 216, height / 16 + 75 + ((i % perPage) * 24), 20, 20, ">"
            ));
        	
        	if(el.getModesSize() == 1) {
        		((GuiButton) this.buttonList.get(i * 3 + buttonOffset + 1)).enabled = false;
        		((GuiButton) this.buttonList.get(i * 3 + buttonOffset + 2)).enabled = false;
        	}
        	
        	if(i < (currentPage * perPage) || i >= ((currentPage + 1) * perPage)) {
        		((GuiButton) this.buttonList.get(i * 3 + buttonOffset)).visible = false;
        		((GuiButton) this.buttonList.get(i * 3 + buttonOffset + 1)).visible = false;
        		((GuiButton) this.buttonList.get(i * 3 + buttonOffset + 2)).visible = false;
        	} else {
        		((GuiButton) this.buttonList.get(i * 3 + buttonOffset)).visible = true;
        		((GuiButton) this.buttonList.get(i * 3 + buttonOffset + 1)).visible = true;
        		((GuiButton) this.buttonList.get(i * 3 + buttonOffset + 2)).visible = true;
        	}
        }
    }
    
    public void onGuiClosed() {
    	BetterHud.saveSettings(BetterHud.logger);
    }
    
    private void updatePage() {
    	((GuiButton) buttonList.get(4)).enabled = currentPage != 0;
    	((GuiButton) buttonList.get(5)).enabled = currentPage != Math.ceil((float) BetterHud.elements.length / perPage) - 1;
    	
    	for(int i = 0; i < BetterHud.elements.length; i++) {
    		int offset = i * 3 + buttonOffset;
    		boolean a = i >= (currentPage * perPage) && i < ((currentPage + 1) * perPage);
    		((GuiButton) this.buttonList.get(offset)).visible = a;
    		((GuiButton) this.buttonList.get(offset + 1)).visible = a;
    		((GuiButton) this.buttonList.get(offset + 2)).visible = a;
    	}
    }
    
    protected void actionPerformed(GuiButton p_146284_1_) {
    	int id = p_146284_1_.id;
        if(id < buttonOffset) {
        	switch(id) {
        		case 0:
        			this.mc.displayGuiScreen((GuiScreen)null);
        			this.mc.setIngameFocus();
        			break;
        		case 1:
        			for(int i = 0; i < buttonList.size(); i++) {
        				if(buttonList.get(i) instanceof GuiButton) {
        					GuiButton b = (GuiButton) buttonList.get(i);
        					if(b.id >= buttonOffset && (b.id - buttonOffset) % 3 == 0) {
		        				BetterHud.elements[(b.id - buttonOffset) / 3].enabled = true;
		        				b.displayString = (BetterHud.elements[(b.id - buttonOffset) / 3].enabled ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + I18n.format("betterHud.element." + BetterHud.elements[(b.id - buttonOffset) / 3].getName(), new Object[] {});
        					}
        				}
        			}
        			break;
        		case 2:
        			for(int i = 0; i < buttonList.size(); i++) {
        				if(buttonList.get(i) instanceof GuiButton) {
        					GuiButton b = (GuiButton) buttonList.get(i);
        					if(b.id >= buttonOffset && (b.id - buttonOffset) % 3 == 0) {
		        				BetterHud.elements[(b.id - buttonOffset) / 3].enabled = false;
		        				b.displayString = (BetterHud.elements[(b.id - buttonOffset) / 3].enabled ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + I18n.format("betterHud.element." + BetterHud.elements[(b.id - buttonOffset) / 3].getName(), new Object[] {});
        					}
        				}
        			}
        			break;
        		case 3:
        			BetterHud.loadDefaults();
        			break;
        		case 4:
        			currentPage--;
        			updatePage();
        			break;
        		case 5:
        			currentPage++;
        			updatePage();
        			break;
        	}
        } else {
        	for(int i = 0; i < BetterHud.elements.length; i++) {
        		ExtraGuiElement el = BetterHud.elements[i];
        		int offset = i * 3 + buttonOffset;
        		if(id == offset) {
        			el.enabled = !el.enabled;
        			p_146284_1_.displayString = (el.enabled ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + I18n.format("betterHud.element." + el.getName(), new Object[] {});
        		} else if(id == offset + 1) { // <
        			int size = el.getModesSize();
        			el.mode = el.mode == 0 ? size - 1 : el.mode - 1;
        		} else if(id == offset + 2) { // >
        			int size = el.getModesSize();
        			el.mode = el.mode == size - 1 ? 0 : el.mode + 1;
        		}
        	}
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        super.updateScreen();
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
    	if(currentPage < 0) {
    		currentPage = 0;
    	}
    	
    	if(currentPage > BetterHud.elements.length / perPage) {
    		currentPage = BetterHud.elements.length / perPage;
    	}
    	
    	updatePage();
    	
        this.drawDefaultBackground();
        //this.drawRect(0, 60, width, height - 20, 0x55000000);
        super.drawScreen(mouseX, mouseY, p_73863_3_);
        this.drawCenteredString(mc.fontRenderer, I18n.format("betterHud.menu.hudSettings", new Object[0]), this.width / 2, height / 16 + 5, 16777215);
        
        this.drawString(mc.fontRenderer, countEnabled(BetterHud.elements) + "/" + BetterHud.elements.length + " enabled", 5, 5, 0xffffff);
        
        for(Object button : buttonList) {
        	if(button instanceof GuiButton) {
        		GuiButton b = (GuiButton) button;
        		if(b.displayString == "<" && b.visible) {
	        		ExtraGuiElement e = BetterHud.elements[((b.id - buttonOffset) + 1) / 3];
	        		int color = e.getModesSize() > 1 ? 0xffffff : 0x555555;
	        		this.drawCenteredString(mc.fontRenderer, I18n.format("betterHud.mode." + e.modeAt(e.mode), new Object[0]), b.xPosition + 53, b.yPosition + 6, color);
        		}
        	}
        }
        
        this.drawCenteredString(mc.fontRenderer, I18n.format("betterHud.menu.page", new Object[0]).replace("*", (currentPage + 1) + "/" + (int) Math.ceil((float) BetterHud.elements.length / perPage)), width / 2, height - height / 16 - 13, 0xffffff);
    }
    
    public int countEnabled(ExtraGuiElement[] elements) {
    	int i = 0;
    	for(ExtraGuiElement element : elements) {
    		if(element.enabled) i++;
    	}
    	return i;
    }
    
    public static ExtraGuiElement[][] divideArray(ExtraGuiElement[] source, int chunksize) {
        ExtraGuiElement[][] ret = new ExtraGuiElement[(int)Math.ceil(source.length / (double)chunksize)][chunksize];
        int start = 0;
        for(int i = 0; i < ret.length; i++) {
            ret[i] = Arrays.copyOfRange(source,start, start + chunksize);
            start += chunksize;
        }
        return ret;
    }
}