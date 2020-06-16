package jobicade.betterhud.element.settings;

public class RootSetting extends SettingStub {
	private final SettingBoolean enabled = new SettingBoolean("enabled").setHidden();
	// TODO nasty publicses
	public final SettingInteger priority = new SettingInteger("priority").setHidden();

	public RootSetting() {
		super();

		addChild(enabled);
		addChild(priority);
	}

	public boolean isEnabled() {
		return enabled.get();
	}

	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	@Override
	public boolean isEmpty() {
		return children.size() <= 2;
	}
}
