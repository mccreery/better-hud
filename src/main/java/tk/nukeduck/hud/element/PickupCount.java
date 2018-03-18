package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingPercentage;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.network.PickupHandler;
import tk.nukeduck.hud.network.PickupHandler.StackNode;
import tk.nukeduck.hud.network.Version;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Tickable.Ticker;

public class PickupCount extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS | Direction.CENTER.flag());

	public final SettingSlider fadeSpeed = new SettingPercentage("speed", 0, 1) {
		@Override
		public String getDisplayValue(double value) {
			if(value == 0) {
				return I18n.format("betterHud.value.slowest");
			} else if(value == 1) {
				return I18n.format("betterHud.value.fastest");
			} else {
				return super.getDisplayValue(value);
			}
		}
	};

	public final PickupHandler handler = new PickupHandler();

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.set(Direction.SOUTH_EAST);
		fadeSpeed.set(.5);
	}

	public PickupCount() {
		super("itemPickup");

		settings.add(position);
		settings.add(fadeSpeed);
		Ticker.FASTER.register(handler);
	}

	@Override
	public Version getMinimumServerVersion() {
		return new Version(1, 3, 9);
	}

	@Override
	public Bounds render(Event event) {
		Bounds bounds = new Bounds(64, 16);

		if(position.getDirection() == Direction.CENTER) {
			bounds.position = MANAGER.getResolution().scale(.5f, .5f).add(5, 5);
		} else {
			position.applyTo(bounds);
		}

		for(StackNode node : handler.recents) {
			String text = String.format("%dx %s", node.stack.getCount(), node.stack.getDisplayName());

			Bounds margin = position.getAnchor().align(new Bounds(-21, 0, 21, 0));
			PaddedBounds lineBounds = position.getAnchor().anchor(new PaddedBounds(new Bounds(bounds.width(), 16), Bounds.EMPTY, margin), bounds);

			GlUtil.drawString(text, position.getAnchor().align(lineBounds.contentBounds()).position, position.getAnchor(), Colors.WHITE);

			// Draw item
			RenderHelper.enableGUIStandardItemLighting();
			Bounds itemBounds = position.getAnchor().anchor(new Bounds(16, 16), lineBounds);

			//GlStateManager.pushMatrix();
			MC.getRenderItem().renderItemAndEffectIntoGUI(node.stack, itemBounds.x(), itemBounds.y());
			//GlStateManager.popMatrix();
		}
		return bounds;
	}
}
