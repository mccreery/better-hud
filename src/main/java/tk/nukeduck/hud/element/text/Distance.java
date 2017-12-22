package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public class Distance extends TextElement {
	private SettingMode mode;
	
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		pos.value = Position.MIDDLE_CENTER;
	}
	
	public Distance() {
		super("distance");
		pos.possibleLocations = Position.combine(Position.TOP_LEFT, Position.TOP_RIGHT, Position.MIDDLE_CENTER, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT);
		this.settings.add(0, new Divider("position"));
		this.settings.add(new Divider("misc"));
		this.settings.add(mode = new SettingMode("type", new String[] {"1", "2"}));
	}
	
	private Bounds textBounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		if(this.posMode.index == 0 && this.pos.value == Position.MIDDLE_CENTER) {
			return textBounds;
		}
		return Bounds.EMPTY;
	}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		
		RayTraceResult mop = MC.getRenderViewEntity().rayTrace(200, 1.0F);
		
		Block te = MC.world.getBlockState(mop.getBlockPos()).getBlock();
		if(te != null && !te.equals(Blocks.AIR)) {
			if(this.posMode.index == 0 && this.pos.value == Position.MIDDLE_CENTER) {
				String s = this.getText()[0];
				int textWidth = MC.fontRenderer.getStringWidth(s);
    			int xPos = event.getResolution().getScaledWidth() / 2 - 5 - textWidth;
    			int yPos = event.getResolution().getScaledHeight() / 2 + 5;
    			
    			this.textBounds = new Bounds(xPos, yPos, textWidth, MC.fontRenderer.FONT_HEIGHT);
    			MC.ingameGUI.drawString(MC.fontRenderer, s, xPos, yPos, this.getColor());
			} else {
				super.render(event, stringManager, layoutManager);
			}
		}
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}

	@Override
	protected String[] getText() {
		RayTraceResult mop = MC.getRenderViewEntity().rayTrace(200, 1.0F);
		double x = (mop.getBlockPos().getX() + 0.5) - MC.player.posX;
		double y = (mop.getBlockPos().getY() + 0.5) - MC.player.posY;
		double z = (mop.getBlockPos().getZ() + 0.5) - MC.player.posZ;
		int distance = (int) Math.round(Math.sqrt(x*x + y*y + z*z));
		return new String[] {I18n.format("betterHud.strings.distance." + mode.getValue(), String.valueOf(distance))};
	}
}
