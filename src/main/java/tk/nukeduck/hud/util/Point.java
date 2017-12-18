package tk.nukeduck.hud.util;

public class Point {
	private int x, y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return this.y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public Point clone() {
		return new Point(this.x, this.y);
	}
	
	public static Point add(Point p1, Point p2) {
		return new Point(p1.getX() + p2.getX(), p1.getY() + p2.getY());
	}
}
