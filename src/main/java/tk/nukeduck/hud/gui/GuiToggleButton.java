package tk.nukeduck.hud.gui;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.util.ISaveLoad.IGetSet;

@SideOnly(Side.CLIENT)
public class GuiToggleButton extends GuiButton implements IGetSet<Boolean> {
	public String unlocalizedName;
	private String unlocalizedValue = "options";

	/** @see #updateText() */
	private boolean staticText = false;

	public GuiToggleButton(int buttonId, int x, int y, String buttonText) {
		super(buttonId, x, y, buttonText);
		updateText();
	}

	public GuiToggleButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		unlocalizedName = buttonText;
		updateText();
	}

	public GuiToggleButton setStaticText() {
		staticText = true;
		return this;
	}

	public GuiToggleButton setUnlocalizedValue(String value) {
		this.unlocalizedValue = value;
		updateText();
		return this;
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

	public void updateText() {
		if(!staticText) {
			String valueDisplay;
	
			if(get()) {
				valueDisplay = ChatFormatting.GREEN + I18n.format(unlocalizedValue + ".on");
			} else {
				valueDisplay = ChatFormatting.RED + I18n.format(unlocalizedValue + ".off");
			}
			displayString = I18n.format(unlocalizedName) + ": " + valueDisplay;
		} else {
			displayString = unlocalizedName;
		}
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
