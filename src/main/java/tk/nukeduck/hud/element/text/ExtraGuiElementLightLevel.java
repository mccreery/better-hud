package tk.nukeduck.hud.element.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;

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
		if(mc.player == null || mc.world == null) return;
		
	    int j3 = MathHelper.floor(mc.player.posX);
	    int k3 = MathHelper.floor(mc.player.posY);
	    int l3 = MathHelper.floor(mc.player.posZ);
		
	    BlockPos blockpos = new BlockPos(j3, k3, l3);
	    
	    if(mc.world != null && mc.world.isBlockLoaded(blockpos)) {
	    	light = mc.world.getLightFor(EnumSkyBlock.SKY, blockpos) - mc.world.calculateSkylightSubtracted(1.0F);
	    	light = Math.max(light, mc.world.getLightFor(EnumSkyBlock.BLOCK, blockpos));
	    }
	    lightLevel = I18n.format("betterHud.strings.lightLevel", String.valueOf(light > 15 ? 15 : light));
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