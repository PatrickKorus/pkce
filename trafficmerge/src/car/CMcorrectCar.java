package car;

import javax.sound.midi.SysexMessage;

import org.lwjgl.Sys;
import org.newdawn.slick.SlickException;

import game.Game;
import sign.Sign;
import sign.SpeedLimitSign;

public class CMcorrectCar extends Car {

	/**
	 * Car that behaves correctly w.r.t. classic merge
	 * (Reissverschlussverfahren)
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

	public CMcorrectCar(double meter, boolean isRightLane, double initSpeed, Game game) throws SlickException {
		super(meter, isRightLane, initSpeed, game, Color.BLUE);
		MAX_ACC = acc(12);
		MAX_BREAKING_FORCE = acc(3);
	}

	@Override
	public void regulate(Game game) {

		// For testing so far
		if (this.meter > Game.END_OF_LANE - 120 && this.meter < Game.END_OF_LANE && !this.isRightLane()) {
			this.isIndicating = true;
		} else {
			this.isIndicating = false;
		}
		if (this.meter > Game.END_OF_LANE-30) {
			this.isChangingLane = true;
		}

		Car carUpFront = getCarUpFront(game)[0];
//		Car indicatingUpFront =;
		double distance = Game.TOTAL_SIMULATION_DISTANCE;
		double speed = 100.0;
		if (carUpFront != null) {
			distance = this.getDistance(carUpFront);
			speed = carUpFront.getCurrentSpeed();
		}

		//TODO: in public function auskoppeln.
		double minDistance = (this.currentSpeed / (2 * this.MAX_BREAKING_FORCE)) * (this.currentSpeed - speed) + 6;

		double safetyDistance = minDistance + this.currentSpeed / 2;

		//
		// System.out.println("minimum distance = " + minDistance);
		// System.out.println("safe distance = " + safetyDistance);
		// System.out.println("current distance = " + distance);
		
		
		// stop if crashed
		if (distance < 5) {
			this.currentSpeed = 0;
			System.err.println("crash");
		}
		// priority 1: preserve critical distance
		if (distance < safetyDistance) {
			this.regulateTo(distance, safetyDistance, 10);
			return;
		}

		// priority 2: drive safe distance
		this.regulateTo(distance, safetyDistance, 5);

		// priority 3: safety distance to indicating car up front

		// lowest priority: regulate to SpeedLimit
		 double speedLimit = kmhTOmpers(this.getSpeedLimit(game));
		 if (this.currentSpeed > speedLimit && currentAcc > -0.1)
		 this.regulateTo(speedLimit, currentSpeed, 4);
		 
//		 System.out.println(speedLimit * 36/10);
//		 System.out.println(this.currentSpeed * 36/10);
	}

	public void regulateTo(double goal, double current, float importance) {
		double error = goal - current;
		if (error < 0.001 && error > -0.001) {
			this.currentAcc = 0;
			return;
		}
		double newacc;
		if (error < 0) {
			newacc = Math.max(importance * error, -MAX_BREAKING_FORCE);
		} else {
			newacc = Math.min(0.8 * importance * error, MAX_ACC);
		}
		this.currentAcc = newacc;

	}

	double getSpeedLimit(Game game) {
		double speedLimit = 100.0;
		for (Sign sign : game.getSigns()) {
			if (sign instanceof SpeedLimitSign && this.getDistance(sign) < 50.0 && sign.getValue() < speedLimit)
				speedLimit = sign.getValue();
		}
		return speedLimit;
	}

	Car[] getCarUpFront(Game game) {
		Car[] carUpFront = new Car[2];
		double smallestDistance0 = Game.TOTAL_SIMULATION_DISTANCE;
		double currentDistance0 = 0;
		double smallestDistance1 = Game.TOTAL_SIMULATION_DISTANCE;
		for (Car car : game.getCars()) {
			currentDistance0 = this.getDistance(car);
			// for all cars are in front of this car
			if (currentDistance0 > 0) {
				// find the closest car on the same lane
				if (car.isRightLane() == this.isRightLane() || car.isChangingLane) {
					if (currentDistance0 < smallestDistance0) {
						smallestDistance0 = currentDistance0;
						carUpFront[0] = car;
						continue;
					}
				}
				// TODO: closest car on the other lane?
				if (car.isIndicating) {
					currentDistance0 = car.getDistance(this);
					if (currentDistance0 < smallestDistance1) {
						smallestDistance1 = currentDistance0;
						carUpFront[1] = car;
					}
				}
			}
		}
		return carUpFront;
	}

}
