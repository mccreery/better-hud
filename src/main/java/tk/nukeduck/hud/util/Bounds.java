package tk.nukeduck.hud.util;

import java.util.ArrayList;

import tk.nukeduck.hud.util.constants.Constants;

public class Bounds {
	public static final Bounds EMPTY = new Bounds(0, 0, 0, 0);

	private Point position;
	private Point size;

	public Bounds(int x, int y, int width, int height) {
		this.position = new Point(x, y);
		this.size = new Point(width, height);
	}

	public Bounds clone() {
		return new Bounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	public int getX() {
		return this.position.getX();
	}
	public void setX(int x) {
		this.position.setX(x);
	}

	public int getY() {
		return this.position.getY();
	}
	public void setY(int y) {
		this.position.setY(y);
	}

	public int getWidth() {
		return this.size.getX();
	}
	public void setWidth(int width) {
		this.size.setX(width);
	}

	public int getHeight() {
		return this.size.getY();
	}
	public void setHeight(int height) {
		this.size.setY(height);
	}

	public int getX2() {
		return this.getX() + this.getWidth();
	}
	public void setX2(int x2) {
		this.setWidth(x2 - this.getX());
	}

	public int getY2() {
		return this.getY() + this.getHeight();
	}
	public void setY2(int y2) {
		this.setHeight(y2 - this.getY());
	}

	public void setPosition(Point position) {
		this.position = position;
	}
	public Point getPosition() {
		return this.position;
	}

	public void setSize(Point size) {
		this.size = size;
	}
	public Point getSize() {
		return this.size;
	}

	public static Bounds join(Bounds... bounds) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;

		for(Bounds b : bounds) {
			if(b.getX() < minX) minX = b.getX();
			if(b.getY() < minY) minY = b.getY();
			if(b.getX2() > maxX) maxX = b.getX2();
			if(b.getY2() > maxY) maxY = b.getY2();
		}

		return new Bounds(minX, minY, maxX - minX, maxY - minY);
	}

	public Bounds pad(int padding) {
		return new Bounds(this.getX() - padding, this.getY() - padding, this.getWidth() + padding * 2, this.getHeight() + padding * 2);
	}

	public Point getPoint(Corner corner) {
		switch(corner) {
			case TOP_LEFT:
				return this.position;
			case TOP_RIGHT:
				return Point.add(this.position, new Point(this.getWidth(), 0));
			case BOTTOM_LEFT:
				return Point.add(this.position, new Point(this.getHeight(), 0));
			case BOTTOM_RIGHT:
				return Point.add(this.position, this.size);
			default:
				return getPoint(Corner.TOP_LEFT);
		}
	}

	public void snapTest(Bounds... b) {snapTest(Constants.SPACER, b);}
	public void snapTest(int hitRadius, Bounds... bounds) {
		ArrayList<Integer> xClips = new ArrayList<Integer>();
		ArrayList<Integer> yClips = new ArrayList<Integer>();

		for(Bounds b : bounds) {
			b = b.pad(Constants.SPACER);

			int clipX = this.getX();
			int clipY = this.getY();

			if(intersects(this.getX(), this.getX2(), b.getX(), b.getX2())) {
				int toClip = b.getY2();
				if(Math.abs(toClip - this.getY()) < hitRadius) {
					clipY = toClip;
				} else {
					toClip = b.getY();
					if(Math.abs(toClip - this.getY2()) < hitRadius) {
						clipY = toClip - this.getHeight();
					}
				}
			}
			if(intersects(this.getY(), this.getY2(), b.getY(), b.getY2())) {
				int toClip = b.getX2();
				if(Math.abs(toClip - this.getX()) < hitRadius) {
					clipX = toClip;
				} else {
					toClip = b.getX();
					if(Math.abs(toClip - this.getX2()) < hitRadius) {
						clipX = toClip - this.getWidth();
					}
				}
			}

			if(clipX != this.getX()) xClips.add(clipX);
			if(clipY != this.getY()) yClips.add(clipY);
		}

		this.position = new Point(FuncsUtil.getSmallestDistance(this.getX(), xClips), FuncsUtil.getSmallestDistance(this.getY(), yClips));
	}

	public static boolean isWithin(float val, float min, float max) {
		return val > min && val < max;
	}

	public static boolean intersects(float min, float max, float min2, float max2) {
		return isWithin(min, min2, max2)
			|| isWithin(max, min2, max2)
			|| isWithin(min2, min, max)
			|| isWithin(max2, min, max);
	}

	public enum Corner {
		TOP_LEFT,
		TOP_RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT;
	}
}
