package jobicade.betterhud.util;

import java.util.ArrayList;

import jobicade.betterhud.BetterHud;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

/** A generic interface for something which responds to ticks */
public interface Tickable {
	public void tick();

	/** Ticked during {@link net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent}.
	 *
	 * <p>Use {@link #register(Tickable)} to register a tickable at this speed.<br>
	 * Use {@link #startTick()} to tick the whole chain
	 *
	 * @see BetterHud#clientTick(net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent) */
	public enum Ticker implements Tickable {
		/** Called every tick */
		FASTER(1),
		/** Called every 20 ticks or 1 second */
		FAST(20),
		/** Called every 100 ticks or 5 seconds */
		MEDIUM(5),
		/** Called every 200 ticks or 10 seconds */
		SLOW(2);

		private Ticker next;
		static {
			for(int i = 0; i < values().length - 1; i++) {
				values()[i].next = values()[i + 1];
			}
		}

		/** @see #tick() */
		private ArrayList<Tickable> children = new ArrayList<Tickable>();

		/** The number of ticks before rollover to the next slowest ticker in the chain
		 * @see #tick() */
		private final int ticks;

		/** Counts towards {@link #ticks} */
		private int counter = 0;

		Ticker(int ticks) {
			this.ticks = ticks;
		}

		/** Adds {@code element} to this ticker's children, which will update at our rate
		 * @see #tick() */
		public void register(Tickable element) {
			children.add(element);
		}

		/** Every {@link #ticks} ticks, children registered through {@link #register(Tickable)},
		 * and the next slowest ticker, will be ticked
		 *
		 * @see #register(Tickable) */
		public void tick() {
			if(++counter >= ticks) {
				for(Tickable element : children) {
					element.tick();
				}

				counter = 0;
				if(next != null) next.tick();
			}
		}

		/** Ticks the whole chain of tickers */
		public static void startTick() {
			FASTER.tick();
		}

		public static void registerEvents() {
			MinecraftForge.EVENT_BUS.register(FASTER);
		}

		@SubscribeEvent
		public void clientTick(ClientTickEvent event) {
			// Event called twice per tick, for start and end
			if (event.phase == Phase.END && BetterHud.getProxy().isModEnabled()) {
				startTick();
			}
		}
	}
}
