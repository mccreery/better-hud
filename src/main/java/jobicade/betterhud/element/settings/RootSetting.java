package jobicade.betterhud.element.settings;

public class RootSetting extends Setting {
	private final SettingBoolean enabled = new SettingBoolean("enabled").setHidden();
	private final SettingInteger priority = new SettingInteger("priority").setHidden();

	public RootSetting() {
		super(null);

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
		this.setPriority(priority);
	}

	@Override
	public boolean isEmpty() {
		return children.size() <= 2;
	}
}
