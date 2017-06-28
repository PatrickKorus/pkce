package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

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

	public GameObject(double meter, boolean isRightLane) {
		this.meter = meter;
		this.isRightLane = isRightLane;
		updateCoordinates();
	}

	// public GameObject(int x, int y) {
	// this.x = x;
	// this.y = y;
	// }

	/**
	 * Draws object only if on Displayed Part of the road (Frustum Culling)
	 * 
	 * @param g
	 */
	public void drawWithCulling(Graphics g) {
		
		if (this.meter < Game.meter_out_of_window || this.meter > Game.TOTAL_SIMULATION_DISTANCE) {
			return;
		}
		this.draw(g);
	}

	public abstract void draw(Graphics g);

	public void update(int delta) {
		updateCoordinates();
	};

	/**
	 * Calculates X coordinate by current distance in meter
	 * 
	 * @return
	 */
	public int updateXCoordinate() {
		if (viewCase() == 1) {
			return Game.meterToPixel(meter - Game.meter_per_width - Game.meter_out_of_window);
		} else {
			return Game.meterToPixel(meter - Game.meter_out_of_window);
		}
	}

	/**
	 * Calculates Y coordinate by using current distance in meter and current
	 * Lane
	 * 
	 * @return
	 */
	public int updateYCoordinate() {

		if (this.viewCase() == 1) {
			if (isRightLane) {
				return Game.LEFT_LANE_BOTTOM + Game.SPACE_BETWEEN_LANES;
			} else {
				return Game.LEFT_LANE_BOTTOM;
			}
		} else {
			if (isRightLane) {
				return Game.LEFT_LANE_TOP + Game.SPACE_BETWEEN_LANES;
			} else {
				return Game.LEFT_LANE_TOP;
			}
		}
	}

	/**
	 * Updates pixel coordinates from meter distance
	 */
	public void updateCoordinates() {
		this.x = updateXCoordinate();
		this.y = updateYCoordinate();
	}

	/**
	 * 
	 * @return -1 if out of window; +0 if on top lane; +1 if on bottom lane;
	 */
	private int viewCase() {
		if (this.meter < Game.meter_out_of_window + 2) {
			return -1;
		} else if (this.meter < Game.meter_out_of_window + Game.meter_per_width) {
			return 0;
		} else {
			return 1;
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

	/**
	 * positive Value if given Object lies in front of this.
	 */
	public double getDistance(GameObject object) {
		return object.meter - this.meter;
	}

	public boolean isRightLane() {
		return isRightLane;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	
	public void rescale(float scale) throws SlickException {
		// TODO this is not working at all.
	};

}
