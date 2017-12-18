package tk.nukeduck.hud.util;

import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;

public class LayoutManager {
	private int topLeft;
	private int topRight;
	private int bottomLeft;
	private int bottomRight;
	
	public static final int SPACER = 5;
	
	public LayoutManager() {
		this.topLeft = SPACER;
		this.topRight = SPACER;
		this.bottomLeft = SPACER;
		this.bottomRight = SPACER;
	}
	
	public void add(int value, Position corner) {
		switch(corner) {
			case TOP_LEFT:
				topLeft += value + SPACER;
				break;
			case TOP_RIGHT:
				topRight += value + SPACER;
				break;
			case BOTTOM_LEFT:
				bottomLeft += value + SPACER;
				break;
			case BOTTOM_RIGHT:
				bottomRight += value + SPACER;
				break;
			default:
		}
	}
	
	public int get(Position corner) {
		switch(corner) {
			case TOP_LEFT:
				return topLeft;
			case TOP_RIGHT:
				return topRight;
			case BOTTOM_LEFT:
				return bottomLeft;
			case BOTTOM_RIGHT:
				return bottomRight;
			default:
				return 0;
		}
	}
}