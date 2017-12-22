package tk.nukeduck.hud.util;

import java.util.ArrayList;

import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.util.constants.Colors;

public class StringManager {
	/*private ScaledResolution resolution;
	private LayoutManager manager;*/
	
	public StringManager(/*ScaledResolution resolution, LayoutManager manager*/) {
		/*this.resolution = resolution;
		this.manager = manager;*/
	}
	
	private ArrayList<ColoredText> topLeft = new ArrayList<ColoredText>();
	//int topLeftHeight = 0;
	private ArrayList<ColoredText> topRight = new ArrayList<ColoredText>();
	//int topRightHeight = 0;
	private ArrayList<ColoredText> bottomLeft = new ArrayList<ColoredText>();
	//int bottomLeftHeight = 0;
	private ArrayList<ColoredText> bottomRight = new ArrayList<ColoredText>();
	//int bottomRightHeight = 0;
	
	public /*Bounds*/void add(String value, Position corner) {add(new ColoredText(value, Colors.WHITE), corner);}
	public /*Bounds*/void add(ColoredText value, Position corner) {
		/*int w = BetterHud.mc.fontRendererObj.getTextWidth(value.text);
		int h = BetterHud.mc.fontRendererObj.FONT_HEIGHT;
		Bounds bounds = Bounds.EMPTY;*/
		switch(corner) {
			case TOP_LEFT:
				topLeft.add(value);
				//bounds = new Bounds(Constants.SPACER, manager.get(corner) + topLeftHeight, w, h);
				//topLeftHeight += BetterHud.mc.fontRendererObj.FONT_HEIGHT + 2;
				break;
			case TOP_RIGHT:
				topRight.add(value);
				//bounds = new Bounds(resolution.getScaledWidth() - Constants.SPACER - w, manager.get(corner) + topRightHeight, w, h);
				//topRightHeight += BetterHud.mc.fontRendererObj.FONT_HEIGHT + 2;
				break;
			case BOTTOM_LEFT:
				bottomLeft.add(value);
				//bounds = new Bounds(Constants.SPACER, resolution.getScaledHeight() - manager.get(corner) - bottomLeftHeight - h, w, h);
				//bottomLeftHeight += BetterHud.mc.fontRendererObj.FONT_HEIGHT + 2;
				break;
			case BOTTOM_RIGHT:
				bottomRight.add(value);
				//bounds = new Bounds(resolution.getScaledWidth() - Constants.SPACER - w, resolution.getScaledHeight() - manager.get(corner) - bottomRightHeight - h, w, h);
				//bottomRightHeight += BetterHud.mc.fontRendererObj.FONT_HEIGHT + 2;
				break;
			default:
				break;
		}
		//return bounds;
	}
	
	public /*Bounds*/void add(Position corner, ColoredText... values) {
		//Bounds[] bounds = new Bounds[values.length];
		for(int i = 0; i < values.length; i++) {
			/*bounds[i] = */add(values[i], corner);
		}
		//return Bounds.join(bounds);
	}
	
	public /*Bounds*/void add(Position corner, String... values) {add(corner, Colors.WHITE, values);}
	public /*Bounds*/void add(Position corner, int color, String... values) {
		//Bounds[] bounds = new Bounds[values.length];
		for(int i = 0; i < values.length; i++) {
			/*bounds[i] = */add(new ColoredText(values[i], color), corner);
		}
		//return Bounds.join(bounds);
	}
	
	public ArrayList<ColoredText> get(Position corner) {
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
				return null;
		}
	}
}