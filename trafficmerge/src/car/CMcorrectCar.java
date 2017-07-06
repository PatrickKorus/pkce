package car;

import java.util.Collection;
import java.util.TreeSet;

import org.newdawn.slick.SlickException;

import car.gaps.Gap;
import game.Game;
import sign.Sign;
import sign.SpeedLimitSign;

public class CMcorrectCar extends Car {

	private final double areaI;
	private final double areaII;

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

	public CMcorrectCar(double meter, boolean isRightLane, double initSpeed, double initGoalSpeed, Game game)
			throws SlickException {
		super(meter, isRightLane, initSpeed, initGoalSpeed, game, Color.BLUE);
		MAX_ACC = acc(9);
		MAX_BREAKING_FORCE = acc(3);
		areaI = Game.END_OF_LANE - 500;
		areaII = Game.END_OF_LANE - 200;
	}

	private boolean crashed = false;

	int delay = 0;
	int reactionDelay = 250;
	double panicFactor = 1;

	@Override
	public void regulate(Game game, int delta) {
		//
		// System.out.println(iteration++);
		// System.out.println(isRightLane);

		delay += delta;
		Car[] carsUpFront = getCarUpFront(game);
		Car carUpFront = carsUpFront[0];

		if (delay >= reactionDelay) {
			panicFactor = carsUpFront[0] != null && carsUpFront[0].isBreaking() ? 2 : 1;
			delay = 0;
		}

//		System.out.println("This is car " + this);
//		System.out.println("Car ahaed is " + carsUpFront[0]);
//		System.out.println("Car next to ahaed is " + carsUpFront[1]);
//		System.out.println("Car next to behind is " + carsUpFront[2]);
		double distanceCarUpFront = this.getDistance(carUpFront);
		double minDistance = panicFactor * getMinDist(carUpFront);
		double safetyDistance = panicFactor * getSafetyDistance(carUpFront);

		// stop if crashed
		if (distanceCarUpFront < 5 && !crashed) {
			this.currentSpeed = 0;
			crashed = true;
			System.err.println("crash");
		}

		// priority 1: preserve critical distance
		double error = this.regulateTo(minDistance, distanceCarUpFront, 10);
		// System.out.println("critical Distance = " + error );

		// priority 2: drive safe distance
		error = Math.min(error, this.regulateTo(safetyDistance, distanceCarUpFront, 8));
//		System.out.println("rec Distance = " + error);

		// priority 3: position dependent
		if (this.meter > this.areaI && this.meter < this.areaII) {
			if (isRightLane) {
				// // area V
				Car indicatingUpFront = carsUpFront[1];
				double distanceIndicating = this.getDistance(indicatingUpFront);
				// make some space for cars that indicate
//				error = Math.min(error, this.regulateTo(this.getMinDist(indicatingUpFront), distanceIndicating, 4));
				// if fewer cars right than left
				if (moreCarsLeftThanRight(game))
					error = Math.min(error, this.regulateTo(safetyDistance * 1.5, distanceCarUpFront, 6));
				//

			} else {
				// area II
				// TODO
				// find gap
				this.isIndicating = true;
			}
		} else if (this.meter > this.areaII && this.meter < Game.END_OF_LANE) {
			if (isRightLane) {
				// area VI
				// TODO
				// let the correct car pass
				if (moreCarsLeftThanRight(game)) {
					error = Math.min(error, this.regulateTo(getMinDist(carsUpFront[1]), distanceCarUpFront, 6));
				}

			} else {
				// area III
				error = Math.min(error,
						this.regulateTo(this.getMinDist(carsUpFront[1]), this.getDistance(carsUpFront[1]), 5));
				// if no car will crash
				Gap gap = new Gap(carsUpFront[1], carsUpFront[2]);
				// if (isSafeToChangeLane(carsUpFront[1], carsUpFront[2]))
				if (gap.isSafe(this, 5))
					this.isChangingLane = true;
			}
		}

		// lowest priority: regulate to SpeedLimit
		double speedLimit = Math.min(kmhTOmps(this.getSpeedLimit(game)), this.goalSpeed);
		if (this.currentSpeed > speedLimit) {
			error = Math.min(error, this.regulateTo(currentSpeed, speedLimit, 4));

		}

		// stop indicating after lane has changed
		if (this.isRightLane == true) {
			this.stopInidicating();
		}

		this.currentAcc = error;
//		System.out.println("applied " + error);
		// System.out.println("speed Limit" + speedLimit * 36 / 10);
		// System.out.println("current speed " + this.currentSpeed * 36 / 10);
		// System.out.println("goal speed " + this.goalSpeed *36/10);
	}

	/**
	 * Counts all cars that are up to metersBehind behind and up to metersAhead
	 * ahead of this car, on the given Lane and have not passed the obstacle
	 * yet.
	 * 
	 * @param metersBehind
	 * @param metersUpFront
	 * @param rightLane
	 * @param game
	 * @return
	 */
	private int countCars(double metersBehind, double metersAhead, boolean rightLane, Game game) {
		int counter = 0;
		Collection<Car> cars = isRightLane ? game.getCarsRight() : game.getCarsLeft();
		for (Car car : cars) {
			if (car.isRightLane() == rightLane && this.getDistance(car) < metersAhead
					&& this.getDistance(car) > -metersBehind && car.getDistance(game.getObstacle()) > -20) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * True if in a range of 200m there are more cars on the left lane than on
	 * the right lane
	 * 
	 * @param game
	 * @return
	 */
	private boolean moreCarsLeftThanRight(Game game) {

		int carsLeft = this.countCars(200, 300, false, game);
		int carsRight = this.countCars(0, 300, true, game);
		return carsLeft > carsRight;
	}

	/**
	 * regulate to goal speed
	 * 
	 * @param goal
	 *            - goal speed
	 * @param current
	 *            - speed
	 * @param importance
	 */
	public double regulateTo(double goal, double current, float importance) {
		double error = current - goal;
		if (error < 0.001 && error > -0.001) {
			return 0;
		}
		double newacc;
		if (error < 0) {
			newacc = Math.max(importance * error, -MAX_BREAKING_FORCE);
		} else {
			newacc = Math.min(0.8 * importance * error, MAX_ACC);
		}
		return newacc;
	}

	int it = 0;

	double getSpeedLimit(Game game) {
		double speedLimit = 180 * 36 / 10;
		for (Sign sign : game.getSigns()) {
			if (sign instanceof SpeedLimitSign && this.getDistance(sign) < 50.0 && sign.getValue() < speedLimit)
				speedLimit = sign.getValue();
		}
		return speedLimit;
	}

	/**
	 * 
	 * @param game
	 * @return [car ahead; car ahead on the other lane; car behind on the other
	 *         lane]
	 */
	Car[] getCarUpFront(Game game) {
		Car[] carUpFront = new Car[3];

		TreeSet<Car> itrThisLane = null;
		TreeSet<Car> itrOtherLane = null;

		if (isRightLane) {
			itrThisLane = game.getCarsRight();
			itrOtherLane = game.getCarsLeft();
		} else {
			itrThisLane = game.getCarsLeft();
			itrOtherLane = game.getCarsRight();
		}

		carUpFront[0] = itrThisLane.lower(this);
		carUpFront[1] = itrOtherLane.lower(this);
		carUpFront[2] = itrOtherLane.ceiling(this);

		if (this.isRightLane && carUpFront[1] != null && carUpFront[1].isChangingLane
				&& this.getDistance(carUpFront[1]) < this.getDistance(carUpFront[0]))
			carUpFront[0] = carUpFront[1];
		return carUpFront;
	}

}
