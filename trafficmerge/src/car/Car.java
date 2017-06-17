package car;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import game.Game;
import game.GameObject;
import sun.util.resources.cldr.ur.CurrencyNames_ur;

public abstract class Car extends GameObject {

	// Image ViewField;

	// TODO ! factors that transform metric speed into pixel speed
	private static double ACC_CONSTANT = 0.1;
	private static double SPEED_CONSTANT = 0.001;

	double MAX_ACC; // specific maximum acc: Seconds from 0 to 100
	double MAX_BREAKING_FORCE; // seconds 100 to 0
	Image basicImage;
	Image indicateImage;
	Image breakImage;

	double goalSpeed;
	double currentSpeed;
	double currentAcc;

	boolean isIndicating;
	boolean isChangingLane;
	// boolean isBreaking; // redundant since true when currentACC < 0

	/**
	 * 
	 * @param meter
	 * @param isRightLane
	 * @param initSpeed
	 */

	public Car(double meter, boolean isRightLane, double initSpeed) {
		super(meter, isRightLane);
		this.goalSpeed = initSpeed;
		this.currentSpeed = initSpeed;
		this.currentAcc = 0.0;
		isIndicating = false;
		isChangingLane = false;
	}

	@Override
	public void draw(Graphics g) {
		image.drawCentered(x, y);
	}

	@Override
	public void update(int delta) {
		// invoke superclass
		super.updateCoordinates();

		// graphical
		if (isIndicating) {
			indicater(delta);
		}

		if (indicatingLightsOn) {
			super.image = indicateImage;
		} else if (this.currentAcc < -1.0) {
			super.image = breakImage;
		} else {
			super.image = basicImage;
		}

		if (isChangingLane) {
		changeLane(delta);
		}

		// control
		regulateToGoalSpeed();

		// apply changes
		move(delta);
	}

	private void move(int delta) {
		// TODO do mathematically correct
		this.currentSpeed += currentAcc * delta / 100.0;
		this.meter += currentSpeed * delta / 1000.0;
	}

	// TODO there is probably a better way to do this
	private int deltaCounter = 901;
	private boolean indicatingLightsOn = false;

	private void indicater(int delta) {
		deltaCounter += delta;
		if (deltaCounter > 700) {
			deltaCounter = 0;
			indicatingLightsOn = !indicatingLightsOn;
		}
	}

	// TODO wissenschaftlicher..
	public void regulateToGoalSpeed() {

		double error = this.goalSpeed - this.currentSpeed;
		if (error < 0) {
			currentAcc = Math.max(error, -MAX_BREAKING_FORCE);
		} else {
			currentAcc = Math.min(error, MAX_ACC);
		}
	}

	// TODO make pretty
	int laneMover = 0;

	public void changeLane(int delta) {
		if (isRightLane) {
			return;
		}
		laneMover += Math.round((delta/1000.0) * Game.SPACE_BETWEEN_LANES);
		if (laneMover <= Game.SPACE_BETWEEN_LANES) {
			this.y += laneMover;
		} else {
			this.y += laneMover;
			isChangingLane = false;
			isRightLane = true;
		}
	}

	// public double accelerate(double CarAcc) {
	// if (!isBreaking) { // && currentSpeed < Speedlimit){ //spaeter:
	// // Speedlimit durch Sign gegeben
	// return 0.5 * CarAcc;
	// }
	// if (isBreaking) { // || currentSpeed > Speedlimit){
	// return -CarAcc;
	// }
	// return 0.0;
	// }

}
