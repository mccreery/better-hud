package jobicade.betterhud.registry;

import java.util.Comparator;

import jobicade.betterhud.element.HudElement;

public enum SortField implements Comparator<HudElement<?>> {
	ALPHABETICAL("alphabetical", false) {
		@Override
		public int compare(HudElement<?> a, HudElement<?> b) {
			return a.getLocalizedName().compareTo(b.getLocalizedName());
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
