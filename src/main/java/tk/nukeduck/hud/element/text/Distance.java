package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.RayTraceResult;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;

public class Distance extends TextElement {
	private final SettingChoose mode;

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.set(Direction.CENTER);
	}

	public Distance() {
		super("distance", Direction.CORNERS | Direction.CENTER.flag());
		this.settings.add(new Legend("misc"));
		this.settings.add(mode = new SettingChoose("type", new String[] {"1", "2"}));
	}

	@Override
	protected PaddedBounds moveBounds(LayoutManager manager, PaddedBounds bounds) {
		if(position.getDirection() == Direction.CENTER) {
			bounds.position = new Point(manager.getResolution().x / 2 - SPACER, manager.getResolution().y / 2 - SPACER);
			return Direction.SOUTH_EAST.align(bounds);
		} else {
			return super.moveBounds(manager, bounds);
		}
	}

	@Override
	protected String[] getText() {
		RayTraceResult trace = MC.getRenderViewEntity().rayTrace(200, 1.0F);

		if(trace != null) {
			long distance = Math.round(Math.sqrt(trace.getBlockPos().distanceSqToCenter(MC.player.posX, MC.player.posY, MC.player.posZ)));
			return new String[] {I18n.format("betterHud.strings.distance." + mode.getIndex(), String.valueOf(distance))};
		} else {
			return new String[0];
		}
	}
}
