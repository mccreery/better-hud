package tk.nukeduck.hud.util;

import java.util.ArrayList;

import tk.nukeduck.hud.element.HudElement;

/** Ticks using rollover counters
 * @see HudElement#update() */
public enum Ticker {
	FASTER(1), FAST(20), MEDIUM(5), SLOW(2);

	private Ticker next;
	static {
		for(int i = 0; i < values().length - 1; i++) {
			values()[i].next = values()[i + 1];
		}
	}

	/** A list of elements which are registered to update at this speed. */
	private ArrayList<HudElement> elements = new ArrayList<HudElement>();

	/** The number of ticks from the previous ticker until this one ticks */
	public final int ticks;
	private int counter = 0;

	Ticker(int ticks) {
		this.ticks = ticks;
	}

	public void register(HudElement element) {
		elements.add(element);
	}

	public void tick() {
		if(++counter >= ticks) {
			for(HudElement element : elements) {
				element.update();
			}

			counter = 0;
			if(next != null) next.tick();
		}
	}
}
