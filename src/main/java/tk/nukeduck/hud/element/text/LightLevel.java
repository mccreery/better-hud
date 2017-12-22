package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.util.Ticker;

public class LightLevel extends TextElement {
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		pos.value = Position.TOP_RIGHT;
	}
	
	public LightLevel() {
		super("lightLevel");
		Ticker.FASTER.register(this);
	}
	
	String lightLevel = "";
	
	@Override
	public void update() {
		int light = 0;
		if(MC.player == null || MC.world == null) return;
		
		int j3 = MathHelper.floor(MC.player.posX);
		int k3 = MathHelper.floor(MC.player.posY);
		int l3 = MathHelper.floor(MC.player.posZ);
		
		BlockPos blockpos = new BlockPos(j3, k3, l3);
		
		if(MC.world != null && MC.world.isBlockLoaded(blockpos)) {
			light = MC.world.getLightFor(EnumSkyBlock.SKY, blockpos) - MC.world.calculateSkylightSubtracted(1.0F);
			light = Math.max(light, MC.world.getLightFor(EnumSkyBlock.BLOCK, blockpos));
		}
		lightLevel = I18n.format("betterHud.strings.lightLevel", String.valueOf(light > 15 ? 15 : light));
	}

	@Override
	public boolean shouldProfile() {
		return posMode.index == 1;
	}

	@Override
	protected String[] getText() {
		return new String[] {lightLevel};
	}
}