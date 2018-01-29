package tk.nukeduck.hud.element;

import tk.nukeduck.hud.element.settings.SettingBoolean;

public class GlobalSettings extends ElementStub {
	public GlobalSettings() {
		super("global");

		settings.add(new SettingBoolean("enabled") {
			@Override public void set(Boolean bool) {settings.set(bool);}
			@Override public Boolean get() {return isEnabled();}
			
			@Override
			public String getUnlocalizedName() {
				return GlobalSettings.this.getUnlocalizedName();
			}
		});
	}

	@Override
	public void loadDefaults() {
		settings.set(true);
	}
}
