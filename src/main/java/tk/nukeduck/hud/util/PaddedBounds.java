package tk.nukeduck.hud.util;

public class PaddedBounds extends Bounds {
	private final Bounds padding, margin;

	public PaddedBounds(Bounds content, Bounds padding, Bounds margin) {
		super(content.pad(padding).pad(margin));
		this.padding = padding;
		this.margin = margin;
	}

	public Bounds paddingBounds() {
		return inset(margin);
	}

	public Bounds contentBounds() {
		return paddingBounds().inset(padding);
	}
}
