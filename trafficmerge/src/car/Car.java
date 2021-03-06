package car;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import game.Game;
import game.GameObject;
import game.GameUI;
import game.Obstacle;

public abstract class Car extends GameObject implements Comparable<Car> {

	protected final static double MAX_POSSIBLE_BREAKING_FORCE = acc(1.5);
	protected double MAX_ACC; // specific maximum acceleration: Seconds from 0 to 100
	protected double MAX_BREAKING_FORCE; // seconds 100 to 0
	private long SpawnTime = 0;

	// car images
	protected Image basicImage, indicateImage, breakImage;
	// backgrounds
	protected Image normback, indback, breakback, backimage;
	// color
	private Color color;

	// current data
	double goalSpeed, currentAcc, currentSpeed;
	protected boolean isIndicating, isChangingLane, isBlockingBothLanes = false;
	
	// pointer to the game this car is in
	protected Game game;

	/**
	 * 
	 * @param meter
	 *            - spawn position in meter
	 * @param isRightLane
	 * 
	 * @param initSpeed
	 *            - initial Speed in km/h
	 * @param game
	 *            - the Game that holds the other cars and signs
	 * @throws SlickException
	 */

	public Car(double meter, boolean isRightLane, double initSpeed, double initGoalSpeed, Game game, Color color)
			throws SlickException {
		super(meter, isRightLane);
		this.game = game;
		this.currentSpeed = kmhTOmps(initSpeed);
		this.goalSpeed = kmhTOmps(initGoalSpeed);
		this.currentAcc = 0.0;
		this.setColor(color, Game.SCALE);
		super.setImage(basicImage);
		backimage = normback;
		isIndicating = false;
		isChangingLane = false;
		this.MAX_ACC = acc(2);
	}

	@Override
	protected void draw(Graphics g) {

		if (GameUI.carData) {
			String speedString = (this.currentSpeed * 36 / 10) + "";
			String accString = (this.currentAcc) + "";
			if (isRightLane) {
				g.drawString(this.toString().substring(20), this.x, this.y + 20);
				g.drawString(speedString.substring(0, 3), this.x, this.y + 40);
				g.drawString(accString.substring(0, 3), this.x, this.y + 60);
			} else {
				g.drawString(this.toString().substring(20), this.x, this.y - 75);
				g.drawString(speedString.substring(0, 3), this.x, this.y - 55);
				g.drawString(accString.substring(0, 3), this.x, this.y - 35);
			}
		}
		backimage.drawCentered(x, y);
		image.drawCentered(x, y);
	}

	@Override
	public void update(int delta) {

		// invoke indicator
		if (isIndicating) {
			indicator(delta);
		}

		// set correct display image
		if (indicatingLightsOn) {
			backimage = indback;
			super.image = indicateImage;
		} else if (this.currentAcc < -0.1) {
			backimage = breakback;
			super.image = breakImage;
		} else {
			backimage = normback;
			super.image = basicImage;
		}

		// control
		regulate(this.game, delta);

		// apply changes
		move(delta);
		super.updateCoordinates();
		// vertical position
		if (isChangingLane) {
			changeLane(delta, game);
		}
	}
	
	/**
	 * Here the car makes its choices manipulating only currentACC, isIndicating
	 * & isChangingLane!
	 * 
	 * @param delta
	 */
	public abstract void regulate(Game game, int delta);

	@Override
	public void rescale(float scale) throws SlickException {
		setColor(color, scale);
	}

	@Override
	public int compareTo(Car otherCar) {
		if (this.meter < otherCar.meter) {
			return 1;
		} else if (this.meter > otherCar.meter) {
			return -1;
		}
		return 0;
	}

	/**
	 * Simple method to turn of the indicator.
	 */
	protected void stopInidicating() {
		this.indicatingLightsOn = false;
		this.isIndicating = false;
	}

	/**
	 * Applies speed to position and acceleration to speed.
	 * 
	 * @param delta
	 */
	private void move(int delta) {
		this.currentSpeed += currentAcc * delta / 1000.0;
		if (this.currentSpeed < 0)
			this.currentSpeed = 0;
		this.meter += currentSpeed * delta / 1000.0;
	}

	// for indicating
	private int deltaCounter = 400;
	private boolean indicatingLightsOn = false;
	/**
	 * This is used to make the indicator flash.
	 * 
	 * @param delta
	 */
	private void indicator(int delta) {
		deltaCounter += delta;
		if (deltaCounter >= 400) {
			deltaCounter = 0;
			indicatingLightsOn = !indicatingLightsOn;
		}
	}



	// for moving 
	private int laneMover = 0;
	/**
	 * Moves linearly to the right lane
	 * 
	 * @param delta
	 */
	public void changeLane(int delta, Game game) {

		int sign = isRightLane ? -1 : 1;

		if (isBlockingBothLanes) {
			if (laneMover >= Math.round(Game.SPACE_BETWEEN_LANES / 2.0)) {
				this.y += laneMover;
				this.stopInidicating();
				return;
			}
		}

		laneMover += sign * Math.round((delta / 1800.0) * Game.SPACE_BETWEEN_LANES);
		if (sign * laneMover <= Game.SPACE_BETWEEN_LANES) {
			this.y += laneMover;
		} else if (sign == 1) {
			this.y += laneMover;
			laneMover = 0;
			isChangingLane = false;
			isRightLane = true;
			game.removeCarLeft(this);
			game.addCarRight(this);
		} else if (sign == -1) {
			this.y += laneMover;
			laneMover = 0;
			isChangingLane = false;
			isRightLane = false;
			game.removeCarRight(this);
			game.addCarLeft(this);
		}
	}

	/**
	 * calculates the minimal distance to stop
	 * 
	 * @param carUpFront
	 *            - car ahead
	 * @return
	 */
	public double getMinDist(GameObject carUpFront) {

		double speedCarUpFront = 300.0;
		if (carUpFront instanceof Car && carUpFront != null) {
			speedCarUpFront = ((Car) carUpFront).getCurrentSpeed();
		} else if (carUpFront instanceof Obstacle) {
			speedCarUpFront = 0;
		}
		return Math.max(
				(this.currentSpeed / (2 * this.MAX_BREAKING_FORCE)) * (this.currentSpeed - speedCarUpFront) + 10, 10.0);
	}

	/**
	 * Returns safety distance by Distance that is safe to drive + reaction time
	 * ("halber Tacho")
	 * 
	 * @param carUpFront
	 * @return
	 */
	public double getSafetyDistance(Car carUpFront) {
		return this.currentSpeed / 2.0 + this.getMinDist(carUpFront);
	}

	/**
	 * 
	 * @param kmh
	 *            - speed in km/h
	 */
	public void setSpeed(double kmh) {
		this.currentSpeed = kmhTOmps(kmh);
		this.goalSpeed = this.currentSpeed;
	}

	/**
	 * 
	 * @param Noughtto100
	 *            - Seconds needed to get from 0 to 100 or 100 to 0
	 * @return equivalent acceleration in m/s^2
	 */
	public static double acc(double Noughtto100) {
		return 1000.0 / (36.0 * Noughtto100);
	}

	protected static double kmhTOmps(double kmh) {
		return kmh / 3.60;
	}

	protected static double mpsTOkmh(double mps) {
		return mps * 3.60;
	}

	/**
	 * Sets the images of this car, according to color and scale
	 * @param color
	 * @param scale
	 * @throws SlickException
	 */
	public void setColor(Color color, double scale) throws SlickException {
		this.color = color;
		String name = "basicCar";
		switch (color) {
		case BLUE:
			name = "basicCar";
			break;
		case RED:
			name = "aggressiveCar";
			break;
		case GREEN:
			name = "passiveCar";
			break;
		case DARKBLUE:
			name = "nmcorrect";
		default:
			break;
		}

		basicImage = new Image("res/" + name + "/normal.png").getScaledCopy((float) scale);
		breakImage = new Image("res/" + name + "/breaking.png").getScaledCopy((float) scale);
		indicateImage = new Image("res/" + name + "/indicating.png").getScaledCopy((float) scale);
		normback = new Image("res/" + name + "/normal_back.png");
		breakback = new Image("res/" + name + "/breaking_back.png");
		indback = new Image("res/" + name + "/indicating_back.png");
	}

	public double getCurrentAcc() {
		return currentAcc;
	}

	/**
	 * 
	 * @return max breaking force
	 */
	public double getMaxBreak() {
		return MAX_BREAKING_FORCE;
	}

	/**
	 * returns current speed
	 * 
	 * @return - in kmh
	 */
	public double getCurrentSpeed() {
		// *3.6 to get kmh instead of mph
		return mpsTOkmh(currentSpeed);
	}

	public boolean isBreaking() {
		return this.currentAcc < -0.1;
	}

	public boolean isIndicating() {
		return isIndicating;
	}

	public boolean isChangingLane() {
		return isChangingLane;
	}

	public long getSpawnTime() {
		return SpawnTime;
	}

	public void setSpawnTime(long systemTimer) {
		SpawnTime = systemTimer;
	}

}