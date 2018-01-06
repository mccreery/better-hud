package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;
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
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		String[] text = getText();

		if(text.length > 0) {
			String distance = text[0];
			Point size = new Point(MC.fontRenderer.getStringWidth(distance), MC.fontRenderer.FONT_HEIGHT);

			if(position.getDirection() == Direction.CENTER) {
				Bounds bounds = new Bounds(new Point(manager.getResolution().x / 2 - 5 - size.x, manager.getResolution().y / 2 + 5), size);
				MC.ingameGUI.drawString(MC.fontRenderer, distance, bounds.x(), bounds.y(), this.getColor());
				return bounds;
			} else {
				return super.render(event, manager);
			}
		}
		return null;
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
