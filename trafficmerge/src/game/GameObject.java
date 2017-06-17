package game;

import org.newdawn.slick.*;

public abstract class GameObject {

	protected int x;
	protected int y;
	protected double meter;
	protected boolean isRightLane;
	
	protected Image image;
	
	public GameObject(Double meter, boolean isRightLane, Image image) {
		this.meter = meter;
		this.isRightLane = isRightLane;
		this.x = updateXCoordinate();
		this.y = updateYCoordinate();
		this.image = image;
	}
	
	public GameObject(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public GameObject(double meter, boolean isRightLane) {
		this.meter = meter;
		this.isRightLane = isRightLane;
		this.x = updateXCoordinate();
		this.y = updateYCoordinate();
	}

	public abstract void draw(Graphics g);
	
	public void update(int delta){};
	
	public int updateXCoordinate() {
		if (meter < Game.halfMeterMax) {
			return (int) Math.round(meter*Game.meterToPixel);
		} else {
			return (int) Math.round((meter - Game.halfMeterMax)*Game.meterToPixel);
		}
	}
	
	public int updateYCoordinate() {
		if (meter < Game.halfMeterMax) {
			if (isRightLane) {
				return Game.LEFT_LANE_TOP + Game.SPACE_BETWEEN_LANES;
			} else {
				return Game.LEFT_LANE_TOP;
			}
		}
		if (isRightLane) {
			return Game.LEFT_LANE_BOTTOM + Game.SPACE_BETWEEN_LANES;
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

	public void updateCoordinates() {
		this.x = updateXCoordinate();
		this.y = updateYCoordinate();
	}
	
	
}
