package jobicade.betterhud.element.text;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.util.Tickable;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.FormatUtil;

public class CpsCount extends TextElement implements Tickable {
	private SettingSlider timeoutMax;
	private SettingBoolean showBurst, remember;

	private int[] clickHistory = new int[10];
	private int i = 0;

	private int windowTotal = 0;
	private int burstTotal = 0;
	private int burstLength = 0;
	private int timeout = 0;

	private float cps = 0;

	public CpsCount() {
		super("cps");
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(new Legend("misc"));
		settings.add(timeoutMax = new SettingSlider("timeout", 1, 10, 1).setUnlocalizedValue("betterHud.hud.seconds"));
		settings.add(showBurst = new SettingBoolean("showBurst"));
		settings.add(remember = new SettingBoolean("remember"));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.SOUTH_WEST);
		timeoutMax.set(3.0);
		showBurst.set(true);
		remember.set(false);
		settings.priority.set(1);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Ticker.FAST.register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onClick(MouseInputEvent event) {
		if(Mouse.getEventButton() != -1 && Mouse.getEventButtonState()) {
			++clickHistory[i];
		}
	}

	@Override
	public void tick() {
		// Tracker should start or is running
		if(timeout < timeoutMax.get() || clickHistory[i] > 0) {
			//System.out.println(clickHistory[i] + " clicks this second");

			if(clickHistory[i] > 0) {
				if(timeout == timeoutMax.get()) { // New burst
					windowTotal = 0;
					burstTotal = 0;
					burstLength = 0;
				} else {
					burstLength += timeout;
				}

				windowTotal += clickHistory[i];
				burstTotal += clickHistory[i];

				cps = (float)windowTotal / Math.min(++burstLength, clickHistory.length);
				timeout = 0;

				//System.out.println("Burst of " + burstLength + " seconds, total clicks " + windowTotal);
			} else if(++timeout == timeoutMax.get()) {
				//System.out.println("Burst complete.");
				//System.out.println("Length " + burstLength + " seconds, total clicks " + windowTotal);
				return;
			}
			next();
		}
	}

	/** Moves on to the next cell in the buffer, throwing away the oldest value */
	private void next() {
		i = (i + 1) % clickHistory.length;

		// The oldest value contributed to clicksInWindow during this burst
		if((burstLength + timeout) >= clickHistory.length) {
			windowTotal -= clickHistory[i]; // Subtract oldest value from total
		}
		clickHistory[i] = 0; // Throw away oldest value
	}

	@Override
	protected List<String> getText() {
		float cps = timeout < timeoutMax.get() || remember.get() ? this.cps : 0;
		String cpsDisplay = getLocalizedName() + ": " + FormatUtil.formatToPlaces(cps, 1);

		if(showBurst.get() && cps > 0) {
			return Arrays.asList(
				cpsDisplay,
				I18n.format("betterHud.hud.burst", burstTotal, burstLength)
			);
		} else {
			return Arrays.asList(cpsDisplay);
		}
	}
}
