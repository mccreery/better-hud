package jobicade.betterhud.registry;

import java.util.Comparator;

import jobicade.betterhud.element.HudElement;

public enum SortField implements Comparator<HudElement<?>> {
	ALPHABETICAL("alphabetical", false) {
		@Override
		public int compare(HudElement<?> a, HudElement<?> b) {
			return a.getLocalizedName().compareTo(b.getLocalizedName());
		}
	}, ENABLED("enabled", false) {
		@Override
		public int compare(HudElement<?> a, HudElement<?> b) {
			int compare = Boolean.compare(b.isEnabled(), a.isEnabled());
			return compare != 0 ? compare : ALPHABETICAL.compare(a, b);
		}
	}, PRIORITY("priority", false) {
		@Override
		public int compare(HudElement<?> a, HudElement<?> b) {
			int compare = Integer.compare(a.settings.priority.get(), b.settings.priority.get());
			return compare != 0 ? compare : ALPHABETICAL.compare(a, b);
		}
	};

	private final String unlocalizedName;
	private final boolean inverted;

	SortField(String unlocalizedName, boolean inverted) {
		this.unlocalizedName = "betterHud.menu." + unlocalizedName;
		this.inverted = inverted;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public boolean isInverted() {
		return inverted;
	}
}
