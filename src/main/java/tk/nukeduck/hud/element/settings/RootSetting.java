package tk.nukeduck.hud.element.settings;

import java.util.List;

import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.util.HudConfig;
import tk.nukeduck.hud.util.IGetSet.IBoolean;

public class RootSetting extends SettingStub<Boolean> implements IBoolean {
	private final HudElement element;
	private int priorityRank = 0;

	public final SettingBoolean enabled = (SettingBoolean)new SettingBoolean("enabled").setHidden();

	public final Setting<Integer> priority = new SettingStub<Integer>("priority") {
		@Override public void set(Integer value) {priorityRank = value;}
		@Override public Integer get() {return priorityRank;}

		@Override public String save() {return String.valueOf(priorityRank);}
		@Override public void load(String save) {priorityRank = Integer.parseInt(save);}

		@Override protected boolean hasValue() {return true;}
	}.setHidden();

	public final void bindConfig(HudConfig config) {
		bindConfig(config, element.name, new StringBuilder());
	}

	public RootSetting(HudElement element, List<Setting<?>> settings) {
		super();
		this.element = element;

		add(enabled);
		add(priority);
		addAll(settings);
	}

	@Override
	public Boolean get() {
		return enabled.get();
	}

	@Override
	public void set(Boolean value) {
		enabled.set(value);
	}

	@Override
	public boolean isEmpty() {
		return children.size() <= 2;
	}
}
