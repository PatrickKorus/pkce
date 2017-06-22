package car;

import org.newdawn.slick.SlickException;

import game.Game;
import sign.Sign;

public class CMcorrectCar extends Car {

	/**
	 * Car that behaves correctly w.r.t. classic merge
	 * (Reissverschlussverfahren)
	 * 
	 * @param meter
	 * @param isRightLane
	 * @param initSpeed
	 * @param game
	 * @throws SlickException
	 */

	public CMcorrectCar(double meter, boolean isRightLane, double initSpeed, Game game) throws SlickException {
		super(meter, isRightLane, initSpeed, game, Color.BLUE);
		MAX_ACC = acc(12);
		MAX_BREAKING_FORCE = acc(5);
	}

	@Override
	public void regulate(Game game) {
		// For testing so far
		if (this.meter > 1050 && this.meter < 1130 && !this.isRightLane()) {
			this.isIndicating = true;
		} else {
			this.isIndicating = false;
		}
		if (this.meter > 1130) {
			this.isChangingLane = true;
		}

		Car carUpFront = getCarUpFront(game);
		double distance = Game.TOTAL_SIMULATION_DISTANCE;
		double safetyDistance = this.currentSpeed / 1 + 2.0;
		if (carUpFront != null)
			distance = carUpFront.getDistance(this);

		// priority 1: preserve security distance & TODO don't crash into
		// obstacle
		if (distance < safetyDistance) {
			this.regulateTo(distance, safetyDistance);
			return;
		}

		// lowest priority: regulat to SpeedLimit
		double speedLimit = this.getSpeedLimit(game);
		this.regulateTo(speedLimit, currentSpeed);

	}

	public void regulateTo(double goal, double current) {
		double error = goal - current;
		if (error < 0.00001 && error > -0.00001) {
			this.currentAcc = 0;
			return;
		}
		double newacc;
		if (error < 0) {
			newacc = Math.max(5 * error, -MAX_BREAKING_FORCE);
		} else {
			newacc = Math.min(2 * error, MAX_ACC);
		}
		if (this.currentSpeed + newacc < 0) {
			this.currentSpeed = 0;
			this.currentAcc = 0;
		} else {
			this.currentAcc = newacc;
		}
	}

	double getSpeedLimit(Game game) {
		double speedLimit = this.goalSpeed;
		for (Sign sign : game.getSigns()) {
			if (sign.getDistance(this) < 100.0) {
				if (sign.getValue() > 1 && sign.getValue() < speedLimit)
					speedLimit = sign.getValue();
			}
		}
		return speedLimit;
	}

	Car getCarUpFront(Game game) {
		Car carUpFront = null;
		double smallestDistance = Game.TOTAL_SIMULATION_DISTANCE;
		double currentDistance = 0;
		for (Car car : game.getCars()) {
			if (car.isRightLane() == this.isRightLane() || car.isChangingLane
					|| (car.isIndicating && car.isRightLane() != this.isRightLane)) {
				currentDistance = car.getDistance(this);
				if (currentDistance > 0 && currentDistance < smallestDistance) {
					smallestDistance = currentDistance;
					carUpFront = car;
				}
			}
		}
		return carUpFront;
	}

}
