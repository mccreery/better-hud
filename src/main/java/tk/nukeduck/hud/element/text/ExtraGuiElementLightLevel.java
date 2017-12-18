package tk.nukeduck.hud.element.text;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.util.FormatUtil;

public class ExtraGuiElementLightLevel extends ExtraGuiElementText {
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		pos.value = Position.TOP_RIGHT;
	}
	
	@Override
	public String getName() {
		return "lightLevel";
	}
	
	public ExtraGuiElementLightLevel() {
		super();
		this.registerUpdates(UpdateSpeed.FASTER);
	}
	
	String lightLevel = "";
	
	public void update(Minecraft mc) {
		int light = 0;
		if(mc.thePlayer == null || mc.theWorld == null) return;
		
	    int j3 = MathHelper.floor_double(mc.thePlayer.posX);
	    int k3 = MathHelper.floor_double(mc.thePlayer.posY);
	    int l3 = MathHelper.floor_double(mc.thePlayer.posZ);
		
	    BlockPos blockpos = new BlockPos(j3, k3, l3);
	    
	    if(mc.theWorld != null && mc.theWorld.isBlockLoaded(blockpos)) {
	    	light = mc.theWorld.getLightFor(EnumSkyBlock.SKY, blockpos) - mc.theWorld.calculateSkylightSubtracted(1.0F);
	    	light = Math.max(light, mc.theWorld.getLightFor(EnumSkyBlock.BLOCK, blockpos));
	    }
	    lightLevel = FormatUtil.translatePre("strings.lightLevel", String.valueOf(light > 15 ? 15 : light));
	}

	@Override
	public boolean shouldProfile() {
		return posMode.index == 1;
	}

	@Override
	protected String[] getText(Minecraft mc) {
		return new String[] {lightLevel};
	}
}