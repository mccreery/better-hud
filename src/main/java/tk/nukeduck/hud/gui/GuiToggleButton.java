package tk.nukeduck.hud.gui;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.util.ISaveLoad.IGetSet;

@SideOnly(Side.CLIENT)
public class GuiToggleButton extends GuiButton implements IGetSet<Boolean> {
	public boolean updateText = false;
	public String unlocalized;

	public GuiToggleButton(int buttonId, int x, int y, String buttonText, boolean updateText) {
		super(buttonId, x, y, buttonText);
		this.displayString = unlocalized = buttonText;
		this.updateText = updateText;
		updateText();
	}

	public GuiToggleButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, boolean updateText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		unlocalized = buttonText;
		this.updateText = updateText;
		updateText();
	}

	private boolean value = false;

	@Override
	public void set(Boolean value) {
		this.value = value;
		updateText();
	}

	@Override
	public Boolean get() {
		return value;
	}

	public void toggle() {
		set(!get());
	}

	protected void updateText() {
		if(updateText) displayString = I18n.format(unlocalized) + ": " + (get() ? ChatFormatting.GREEN : ChatFormatting.RED) + I18n.format(get() ? "options.on" : "options.off");
	}

	@Override
	protected int getHoverState(boolean mouseOver) {
		return get() && enabled ? 2 : super.getHoverState(mouseOver);
	}

	@Override
	public String save() {
		return get().toString();
	}

	@Override
	public void load(String save) {
		set(Boolean.valueOf(save));
	}
}
