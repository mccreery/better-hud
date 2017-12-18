package tk.nukeduck.hud.element;

public class ExtraGuiElementHidePlayers extends ExtraGuiElement {
	public ExtraGuiElementHidePlayers() {
		name = "hidePlayers";
		modes = new String[] {"hidePlayers.onlyOthers", "hidePlayers.includeMe"};
	}
}