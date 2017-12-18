package tk.nukeduck.hud.element.text;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.RayTraceResult;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementDistance extends ExtraGuiElementText {
	private ElementSettingMode mode;
	
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		pos.value = Position.MIDDLE_CENTER;
	}
	
	@Override
	public String getName() {
		return "distance";
	}
	
	public ExtraGuiElementDistance() {
		super();
		pos.possibleLocations = Position.combine(Position.TOP_LEFT, Position.TOP_RIGHT, Position.MIDDLE_CENTER, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT);
		this.settings.add(0, new ElementSettingDivider("position"));
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(mode = new ElementSettingMode("type", new String[] {"1", "2"}));
	}
	
	private Bounds textBounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		if(this.posMode.index == 0 && this.pos.value == Position.MIDDLE_CENTER) {
			return textBounds;
		}
		return super.getBounds(resolution);
	}
	
	public void update(Minecraft mc) {}
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		
		RayTraceResult mop = mc.getRenderViewEntity().rayTrace(200, 1.0F);
		
		Block te = mc.world.getBlockState(mop.getBlockPos()).getBlock();
		if(te != null && !te.equals(Blocks.AIR)) {
			if(this.posMode.index == 0 && this.pos.value == Position.MIDDLE_CENTER) {
				String s = this.getText(mc)[0];
				int textWidth = mc.fontRenderer.getStringWidth(s);
    			int xPos = resolution.getScaledWidth() / 2 - 5 - textWidth;
    			int yPos = resolution.getScaledHeight() / 2 + 5;
    			
    			this.textBounds = new Bounds(xPos, yPos, textWidth, mc.fontRenderer.FONT_HEIGHT);
    			mc.ingameGUI.drawString(mc.fontRenderer, s, xPos, yPos, this.getColor());
			} else {
				super.render(mc, resolution, stringManager, layoutManager);
			}
		}
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}

	@Override
	protected String[] getText(Minecraft mc) {
		RayTraceResult mop = mc.getRenderViewEntity().rayTrace(200, 1.0F);
		double x = (mop.getBlockPos().getX() + 0.5) - mc.player.posX;
		double y = (mop.getBlockPos().getY() + 0.5) - mc.player.posY;
		double z = (mop.getBlockPos().getZ() + 0.5) - mc.player.posZ;
		int distance = (int) Math.round(Math.sqrt(x*x + y*y + z*z));
		return new String[] {I18n.format("betterHud.strings.distance." + mode.getValue(), String.valueOf(distance))};
	}
}
