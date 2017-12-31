package tk.nukeduck.hud.util;

import java.util.ArrayList;

import tk.nukeduck.hud.element.HudElement;

/** Ticks using rollover counters
 * @see HudElement#update() */
public enum Ticker {
	/** Called every game tick */
	FASTER(1),
	/** Called every second */
	FAST(20),
	/** Called every 5 seconds */
	MEDIUM(5),
	/** Called every 10 seconds */
	SLOW(2);

	private Ticker next;
	static {
		for(int i = 0; i < values().length - 1; i++) {
			values()[i].next = values()[i + 1];
		}
	}

	/** A list of elements which are registered to update at this speed. */
	private ArrayList<Tickable> elements = new ArrayList<Tickable>();

	/** The number of ticks from the previous ticker until this one ticks */
	public final int ticks;
	private int counter = 0;

	Ticker(int ticks) {
		this.ticks = ticks;
	}

	public void register(Tickable element) {
		elements.add(element);
	}

	public void tick() {
		if(++counter >= ticks) {
			for(Tickable element : elements) {
				element.tick();
			}

			counter = 0;
			if(next != null) next.tick();
		}
	}

	public static interface Tickable {
		public void tick();
	}
}
