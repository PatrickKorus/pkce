package car;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import game.Game;
import game.GameObject;

public abstract class Car extends GameObject implements Comparable<Car> {

	protected double MAX_ACC; // specific maximum acc: Seconds from 0 to 100
	protected double MAX_BREAKING_FORCE; // seconds 100 to 0

	// car images
	protected Image basicImage, indicateImage, breakImage;
	// backgrounds
	protected Image normback, indback, breakback, backimage;
	// color
	private Color color;

	// current data
	protected double goalSpeed, currentAcc, currentSpeed;
	protected boolean isIndicating, isChangingLane;
	// boolean isBreaking; // redundant since true when currentACC < 0

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

	public Car(double meter, boolean isRightLane, double initSpeed, Game game, Color color) throws SlickException {
		super(meter, isRightLane);
		this.game = game;
		this.setSpeed(initSpeed);
		this.currentAcc = 0.0;
		this.setColor(color, Game.SCALE);
		super.setImage(basicImage);
		backimage = normback;
		isIndicating = false;
		isChangingLane = false;
	}

	@Override
	public void draw(Graphics g) {
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
		regulate(this.game);

		// apply changes
		move(delta);
		super.updateCoordinates();
		// vertical position
		if (isChangingLane) {
			changeLane(delta);
		}

		// invoke superclass
//		super.update(delta);
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

	// TODO: There is probably a better way to do this
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

	/**
	 * Here the car makes its choices manipulating only currentACC, isIndicating
	 * & isChangingLane!
	 */
	public abstract void regulate(Game game);

	private int laneMover = 0;

	/**
	 * Moves linearly to the right lane
	 * 
	 * @param delta
	 */
	public void changeLane(int delta) {
		if (isRightLane) {
			return;
		}
		laneMover += Math.round((delta / 1000.0) * Game.SPACE_BETWEEN_LANES);
		if (laneMover <= Game.SPACE_BETWEEN_LANES) {
			this.y += laneMover;
		} else {
			this.y += laneMover;
			isChangingLane = false;
			isRightLane = true;
		}
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
	public double acc(double Noughtto100) {
		return 1000.0 / (36.0 * Noughtto100);
	}

	protected double kmhTOmps(double kmh) {
		return kmh / 3.60;
	}
	
	protected double mpsTOkmh(double mps) {
		return mps * 3.60;
	}

	public void setColor(Color color, float scale) throws SlickException {
		this.color = color;
		switch (color) {
		// TODO: Other colors
		case BLUE:
			basicImage = new Image("res/basicCar/normal.png").getScaledCopy(scale);
			breakImage = new Image("res/basicCar/breaking.png").getScaledCopy(scale);
			indicateImage = new Image("res/basicCar/indicating.png").getScaledCopy(scale);
			normback = new Image("res/basicCar/normal_back.png");
			breakback = new Image("res/basicCar/breaking_back.png");
			indback = new Image("res/basicCar/indicating_back.png");
			break;
		default:
			basicImage = new Image("res/basicCar/normal.png").getScaledCopy(scale);
			breakImage = new Image("res/basicCar/breaking.png").getScaledCopy(scale);
			indicateImage = new Image("res/basicCar/indicating.png").getScaledCopy(scale);
			normback = new Image("res/basicCar/normal_back.png");
			breakback = new Image("res/basicCar/breaking_back.png");
			indback = new Image("res/basicCar/indicating_back.png");
			break;
		}
	}

	@Override
	public void rescale(float scale) throws SlickException {
		setColor(color, scale);
	}
	
	@Override
	public int compareTo(Car otherCar) {
		if (this.meter < otherCar.meter) {
			return -1;
		} else if (this.meter > otherCar.meter) {
			return 1;
		}
		return 0;
	}

	public double getCurrentAcc() {
		return currentAcc;
	}
	
	/**
	 * 
	 * @return max breaking force
	 */
	public double getMaxBreak(){
		return MAX_BREAKING_FORCE;
	}

	/**
	 *  returns current speed
	 * @return - in kmh
	 */
	public double getCurrentSpeed() {
		//*3.6 to get kmh instead of mph
		return mpsTOkmh(currentSpeed);
	}

	public boolean isIndicating() {
		return isIndicating;
	}

	public boolean isChangingLane() {
		return isChangingLane;
	}

}	