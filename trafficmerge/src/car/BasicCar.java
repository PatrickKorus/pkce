package car;

import java.util.Collection;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import game.Sign;

public class BasicCar extends Car {
	
	
	

	public BasicCar(double meter, boolean isRightLane, double initSpeed) throws SlickException {
		super(meter, isRightLane, initSpeed);
		basicImage = new Image("res/basicCar/normal.png");
		breakImage = new Image("res/basicCar/breaking.png");
		indicateImage = new Image("res/basicCar/indicating.png");
		super.setImage(basicImage);
		MAX_ACC = 12;
		MAX_BREAKING_FORCE = 10;
		
	}

	// TODO just for testing so far
	@Override
	public void update(int delta, Collection<? extends Car> cars, Collection<? extends Sign> signs) {
		super.update(delta);
		if(meter > 100 && meter < 200) {
			this.goalSpeed = 50;
		} else if (meter > 400 && meter < 500) {
			this.goalSpeed = 100;
		} else if (meter > 500 && meter < 900) {
			isIndicating = true;
		} else if (meter > 900 && meter < 1000) {
			this.goalSpeed = 20;
		} else if (meter > 1000 && meter < 1100) {
			this.isChangingLane = true;
		} else {
			this.isIndicating = false;
		}
	}

}
