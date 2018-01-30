package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingPercentage;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.network.PickupHandler;
import tk.nukeduck.hud.network.Version;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
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
		this.settings.set(true);
		position.set(Direction.CENTER);
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
	public Bounds render(RenderGameOverlayEvent event) {
		Map<ItemStack, Float> sortedMap = sortByComparator(handler.pickedUp);

		Bounds bounds = new Bounds(64, 16);

		if(position.getDirection() == Direction.CENTER) {
			bounds.position = MANAGER.getResolution().scale(.5f, .5f).add(5, 5);
		} else {
			position.applyTo(bounds);
		}

		//int i = 0;
		for(Entry<ItemStack, Float> entry : sortedMap.entrySet()) {
			//i++;

			ItemStack stack = entry.getKey();
			String text = String.format("%dx %s", stack.getCount(), stack.getDisplayName());
			//int textWidth = MC.fontRenderer.getStringWidth(text);

			Bounds margin = position.getAnchor().align(new Bounds(-21, 0, 21, 0));
			PaddedBounds lineBounds = position.getAnchor().anchor(new PaddedBounds(new Bounds(bounds.width(), 16), Bounds.EMPTY, margin), bounds);

			drawString(text, position.getAnchor().align(lineBounds.contentBounds()).position, position.getAnchor(), Colors.WHITE);

			// Draw item
			RenderHelper.enableGUIStandardItemLighting();
			Bounds itemBounds = position.getAnchor().anchor(new Bounds(16, 16), lineBounds);

			//GlStateManager.pushMatrix();
			MC.getRenderItem().renderItemAndEffectIntoGUI(stack, itemBounds.x(), itemBounds.y());
			//GlStateManager.popMatrix();
		}
		return bounds;
	}

	@Deprecated
	private static Map<ItemStack, Float> sortByComparator(Map<ItemStack, Float> unsortMap) {
		List<Entry<ItemStack, Float>> list = new LinkedList<Entry<ItemStack, Float>>(unsortMap.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<ItemStack, Float>>() {
			public int compare(Map.Entry<ItemStack, Float> o1, Map.Entry<ItemStack, Float> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		
		Map<ItemStack, Float> sortedMap = new LinkedHashMap<ItemStack, Float>();
		for (Iterator<Map.Entry<ItemStack, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<ItemStack, Float> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
