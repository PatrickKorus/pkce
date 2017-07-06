package car;

import org.newdawn.slick.SlickException;

import game.Game;

/**
 * Basic blue car with not AI so far - mainly for testing purposes
 * 
 * @author Paddy
 *
 */
public class BasicCar extends Car {

	public BasicCar(double meter, boolean isRightLane, double initSpeed, Game game) throws SlickException {
		super(meter, isRightLane, initSpeed, game, Color.BLUE);
		MAX_ACC = acc(12);
		MAX_BREAKING_FORCE = acc(4);
	}

	@Override
	public void update(int delta) {
		super.update(delta);
		this.goalSpeed = kmhTOmps(80);

		// while (currentSpeed + currentAcc <0)
		// this.currentAcc *= 0.5;
		// }
		// // testing
		// if (meter > 500 && meter < 700) {
		// super.goalSpeed = 50;
		// } else if (meter > 700 && meter < 800) {
		// super.goalSpeed = 100;
		// } else if (meter > 800 && meter < 900) {
		// isIndicating = true;
		// } else if (meter > 900 && meter < 1000) {
		// this.goalSpeed = 30;
		// } else if (meter > 1000 && meter < 1100) {
		// this.isChangingLane = true;
		// } else {
		// this.isIndicating = false;
		// this.goalSpeed = 10;
		// }

		// this.goalSpeed = 0.0;
		// this.isRightLane = true;
		// this.isChangingLane = true;
	}

	@Override
	public void regulate(Game game, int delta) {
		double error = this.goalSpeed - this.currentSpeed;
		if (error < 0.00001 && error > -0.00001) {
			this.currentAcc = 0;
			return;
		}

		if (error < 0) {
			currentAcc = Math.max(error, -MAX_BREAKING_FORCE);
		} else {
			currentAcc = Math.min(error, MAX_ACC);
		}
	}

}
