package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;

public class MaxLevelIndicator extends HudElement {
	private static final ItemStack BOOK = new ItemStack(Items.ENCHANTED_BOOK);

	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS | Direction.SOUTH.flag());

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.set(Direction.SOUTH);
	}

	public MaxLevelIndicator() {
		super("enchantIndicator");
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event) {
		Bounds bounds;
		if(position.getDirection() == Direction.SOUTH) {
			bounds = new Bounds(MANAGER.getResolution().x / 2 - 8, MANAGER.getResolution().y - 50, 16, 16);
		} else {
			bounds = position.applyTo(new Bounds(16, 16));
		}

		GlUtil.renderSingleItem(BOOK, bounds.position);
		return bounds;
	}

	@Override
	public boolean shouldRender() {
		return MC.playerController.gameIsSurvivalOrAdventure()
			&& !(MC.player.getRidingEntity() != null && MC.player.getRidingEntity() instanceof EntityHorse)
			&& MC.player.experienceLevel >= 30;
	}
}
