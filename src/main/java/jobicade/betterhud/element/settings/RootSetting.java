package jobicade.betterhud.element.settings;

public class RootSetting extends Setting {
	private final SettingBoolean enabled = SettingBoolean.builder("enabled").setHidden().build();
	private final SettingInteger priority = SettingInteger.builder("priority").setHidden().build();

	public RootSetting() {
		super((String)null);

		addChild(enabled);
		addChild(priority);
	}

	public boolean isEnabled() {
		return enabled.get();
	}

	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	public int getPriority() {
		return priority.get();
	}

	public void setPriority(int priority) {
		this.priority.set(priority);
	}

	@Override
	public boolean isEmpty() {
		return children.size() <= 2;
	}
}
