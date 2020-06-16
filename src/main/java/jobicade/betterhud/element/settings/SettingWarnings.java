package jobicade.betterhud.element.settings;

import net.minecraft.client.resources.I18n;

import java.util.Collection;

import jobicade.betterhud.geom.Direction;

public class SettingWarnings extends SettingStub {
	private final SettingSlider[] sliders;

	public SettingWarnings(String name) {
		this(name, 3);
	}

	public SettingWarnings(String name, int warnings) {
		super(name);

		addChild(new Legend("damageWarning"));

		sliders = new SettingSlider[warnings];
		for(int i = 0; i < sliders.length; i++) {
			final int index = i;

			addChild(sliders[i] = new SettingPercentage("warning." + String.valueOf(i+1)) {
				@Override
				public String getDisplayValue(double value) {
					SettingSlider next = next();

					if(next == null || next.get() < get()) {
						return super.getDisplayValue(value);
					} else {
						return I18n.format("betterHud.value.disabled");
					}
				}

				@Override
				public void set(Double value) {
					SettingSlider next = next();
					super.set(next != null ? Math.max(value, next.get()) : value);

					for(int i = index - 1; i >= 0; i--) {
						SettingSlider slider = sliders[i];
						if(slider != null) slider.set(Math.max(slider.get(), get()));
					}
				}

				private SettingSlider next() {
					return index == sliders.length - 1 ? null : sliders[index + 1];
				}

				@Override
				public void updateGuiParts(Collection<Setting> settings) {
					slider.updateDisplayString();
				}
			}.setAlignment((i & 1) == 1 ? Direction.EAST : Direction.WEST));
		}

		if((sliders.length & 1) == 1) {
			sliders[sliders.length - 1].setAlignment(Direction.CENTER);
		}
	}

	// TODO why double and float mixed
	public double[] get() {
		double[] values = new double[sliders.length];

		for(int i = 0; i < sliders.length; i++) {
			values[i] = sliders[i].get();
		}
		return values;
	}

	public void set(double... values) {
		for(int i = 0; i < sliders.length; i++) {
			if(values[i] >= 0) {
				sliders[i].set(values[i]);
			}
		}
	}

	public int getWarning(float value) {
		for(int i = sliders.length - 1; i >= 0; i--) {
			if(value <= sliders[i].get()) return i + 1;
		}
		return 0;
	}
}
