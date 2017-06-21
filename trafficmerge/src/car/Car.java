package car;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import game.Game;
import game.GameObject;

public abstract class Car extends GameObject {

	// Image ViewField;

	double MAX_ACC; // specific maximum acc: Seconds from 0 to 100
	double MAX_BREAKING_FORCE; // seconds 100 to 0
	Image basicImage;
	Image indicateImage;
	Image breakImage;
	// backgrounds
	Image normback, indback, breakback, backimage;

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
		this.setSpeed(initSpeed);
		this.currentAcc = 0.0;
		isIndicating = false;
		isChangingLane = false;
	}

	@Override
	public void draw(Graphics g) {
		backimage.drawCentered(x,y);
		image.drawCentered(x, y);
		
	}

	@Override
	public void update(int delta) {
		// invoke superclass
		super.update(delta);
		super.updateCoordinates();
	

		// graphical
		if (isIndicating) {
			indicater(delta);
		}

		if (indicatingLightsOn) {
			backimage = indback;
			super.image = indicateImage;
		} else if (this.currentAcc < -1.0) {
			backimage = breakback;
			super.image = breakImage;
		} else {
			backimage = normback;
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
		this.currentSpeed += currentAcc * delta / 1000.0;
		this.meter += currentSpeed * delta / 1000.0;
	}

	// TODO there is probably a better way to do this
	private int deltaCounter = 700;
	private boolean indicatingLightsOn = false;

	private void indicater(int delta) {
		deltaCounter += delta;
		if (deltaCounter >= 700) {
			deltaCounter = 0;
			indicatingLightsOn = !indicatingLightsOn;
		}
	}

	// TODO wissenschaftlicher..
	public void regulateToGoalSpeed() {

		double error = this.goalSpeed - this.currentSpeed;
		if (error < 0.00001 && error > -0.00001){
			return;
		}
		
		if (error < 0) {
			currentAcc = Math.max(2*error, -MAX_BREAKING_FORCE);
			System.out.println("breaking with " + currentAcc);
		} else {
			currentAcc = Math.min(2*error, MAX_ACC);
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
	
	public void setSpeed(double kmh) {
		this.currentSpeed = kmh*100.0/36.0;
		this.goalSpeed = this.currentSpeed;
	}
	
	public double acc(double Noughtto100) {
		return 10000/(36*Noughtto100); 
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
