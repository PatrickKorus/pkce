package car;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import game.Game;

public class BasicCar extends Car {

	public BasicCar(double meter, boolean isRightLane, double initSpeed) throws SlickException {
		super(meter, isRightLane, initSpeed);
		basicImage = new Image("res/basicCar/normal.png").getScaledCopy(Game.SCALE);
		breakImage = new Image("res/basicCar/breaking.png").getScaledCopy(Game.SCALE);
		indicateImage = new Image("res/basicCar/indicating.png").getScaledCopy(Game.SCALE);
		normback = new Image("res/basicCar/normal_back.png");
		breakback = new Image("res/basicCar/breaking_back.png");
		indback = new Image("res/basicCar/indicating_back.png");
		super.setImage(basicImage);
		backimage = normback;
		MAX_ACC = acc(12);
		MAX_BREAKING_FORCE = acc(5);
		goalSpeed = 100;
	}

	// TODO just for testing so far
	@Override
	public void update(int delta) {
		super.update(delta);
		if (meter > 500 && meter < 700) {
			this.goalSpeed = 50;
		} else if (meter > 700 && meter < 800) {
			this.goalSpeed = 100;
		} else if (meter > 800 && meter < 900) {
			isIndicating = true;
		} else if (meter > 900 && meter < 1000) {
			this.goalSpeed = 20;
		} else if (meter > 1000 && meter < 1100) {
			this.isChangingLane = true;
		} else {
			this.isIndicating = false;
			this.goalSpeed = 0;
		}

		// if (meter > 1000) {
		// this.goalSpeed = 0;
		// }
	}

}
