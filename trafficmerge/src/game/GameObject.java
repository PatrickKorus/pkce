package game;

import org.newdawn.slick.*;

public abstract class GameObject {

	protected int x;
	protected int y;
	protected double meter;
	double meterTillEnd;
	boolean isRightLane;
	
	protected Image image;
	
	public GameObject(Double meter, boolean isRightLane, Image image) {
		super();
		this.meter = meter;
		this.isRightLane = isRightLane;
		this.x = getXCoordinate(meter);
		this.y = getYCoordinate(meter);
		this.image = image;
	}
	
	public GameObject(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public abstract void draw(Graphics g);
	
	public void update(int delta){};
	
	public int getXCoordinate(double meter) {
		if (meter < Game.halfMeterMax) {
			return (int) Math.round(meter*Game.meterToPixel);
		} else {
			return (int) Math.round((meter - Game.halfMeterMax)*Game.meterToPixel);
		}
	}
	
	public int getYCoordinate(double meter) {
		if (meter < Game.halfMeterMax) {
			if (isRightLane) {
				return Game.RIGHT_LANE_TOP;
			} else {
				return Game.LEFT_LANE_TOP;
			}
		}
		if (isRightLane) {
			return Game.RIGHT_LANE_BOTTOM;
		} else {
			return Game.LEFT_LANE_BOTTOM;
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	
	
}
