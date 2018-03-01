package tk.nukeduck.hud.util;

import java.io.File;
import java.util.Comparator;

import net.minecraftforge.common.config.Configuration;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.HudElement.SortType;
import tk.nukeduck.hud.util.Indexer.Order;

public class HudConfig extends Configuration {
	public HudConfig(File file) {
		super(file);
	}

	@Override
	public void load() {
		super.load();

		for(HudElement element : HudElement.ELEMENTS) {
			element.settings.bindConfig(this);
			element.settings.loadConfig();
		}

		HudElement.INDEXER.recalculateIndices();
		normalizePriority();

		if(hasChanged()) save();
	}

	public void saveSettings() {
		for(HudElement element : HudElement.ELEMENTS) {
			element.settings.saveConfig();
		}
		if(hasChanged()) save();
	}

	private static void normalizePriority() {
		Comparator<HudElement> previousComparator = HudElement.INDEXER.getComparator();
		Order previousOrder = HudElement.INDEXER.getOrder();

		HudElement.INDEXER.setComparator(SortType.PRIORITY, Order.ASCENDING);
		for(int i = 0; i < HudElement.INDEXER.size(); i++) {
			HudElement.INDEXER.get(i).settings.priority.set(i);
		}

		HudElement.INDEXER.setComparator(previousComparator, previousOrder);
	}
}
