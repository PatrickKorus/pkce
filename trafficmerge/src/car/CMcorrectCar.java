package car;

import java.util.Collection;
import java.util.Iterator;
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
	Gap gapAimedFor;

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

		System.out.println("This is car " + this);
		System.out.println("Car ahaed is " + carsUpFront[0]);
		System.out.println("Car next to ahaed is " + carsUpFront[1]);
		System.out.println("Car next to behind is " + carsUpFront[2]);
		double distanceCarUpFront = this.getDistance(carUpFront);
		double minDistance = panicFactor * getMinDist(carUpFront);
		double safetyDistance = panicFactor * getSafetyDistance(carUpFront);

		// stop if crashed

		if (distanceCarUpFront < 5 && !crashed) {
			this.currentSpeed = 0;
			crashed = true;
			System.err.println("crash");
			// TODO: Debugg-Output
			System.out.println(
					"Back: \t Acc->" + this.currentAcc + " Speed->" + this.currentSpeed + " goal->" + this.goalSpeed);
			System.out.println("Front: \t Acc->" + carUpFront.currentAcc + " Speed->" + carUpFront.currentSpeed
					+ " goal->" + carUpFront.goalSpeed);
			System.out.println(
					"=================================================================================================");
			this.currentSpeed = 0;
		}

		System.out.println(isRightLane ? "Right lane" : " Left lane");
		System.out.println(isChangingLane ? "Is changing Lane" : "is not changing lane");

		// priority 1: preserve critical distance to car&obstacle
		double error = this.regulateTo(minDistance, distanceCarUpFront, 10);
		if (!isRightLane)
			error = Math.min(error,
					this.regulateTo(this.getMinDist(game.getObstacle()), this.getDistance(game.getObstacle()), 10));
		// System.out.println("critical Distance = " + error );

		// priority 2: drive safe distance
		error = Math.min(error, this.regulateTo(safetyDistance, distanceCarUpFront, 5));
		// System.out.println("rec Distance = " + error);

		// priority 3: position dependent
		if (this.meter > this.areaI && this.meter < this.areaII) {
			if (isRightLane) {
				// // area V
				// if fewer cars right than left
				if (moreCarsLeftThanRight(game))
					error = Math.min(error, this.regulateTo(safetyDistance * 1.5, distanceCarUpFront, 6));
				//

			} else {
				// area II
				// TODO
				// find gap
				gapAimedFor = this.findGap(game);
				if (gapAimedFor != null)
					error = Math.max(error, this.regulateTo(this.meter, gapAimedFor.getPosition(), 6));
				this.isIndicating = true;
			}
		} else if (this.meter > this.areaII && this.meter < Game.END_OF_LANE) {
			if (isRightLane) {
				// area VI
				// TODO
				Car indicatingUpFront = carsUpFront[1];
				double distanceIndicating = this.getDistance(indicatingUpFront);
				// make some space for cars that indicate
				// error = Math.min(error,
				// this.regulateTo(this.getMinDist(indicatingUpFront),
				// distanceIndicating, 0.2f));
				// let the correct car pass
				if (moreCarsLeftThanRight(game) && this.getDistance(indicatingUpFront) > 6) {
					System.out.println("Distance is " + this.getDistance(indicatingUpFront));
					error = Math.min(error, this.regulateTo(getMinDist(indicatingUpFront), distanceIndicating, 6));
				}

			} else {
				// area III
				error = Math.min(error,
						this.regulateTo(this.getMinDist(carsUpFront[1]), this.getDistance(carsUpFront[1]), 5));
				// if no car will crash
				Gap gap = new Gap(carsUpFront[1], carsUpFront[2]);
				// if (isSafeToChangeLane(carsUpFront[1], carsUpFront[2]))
				if (gap.isSafe(this, this.currentSpeed / 30))
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
		// System.out.println("applied " + error);
		// System.out.println("speed Limit" + speedLimit * 36 / 10);
		// System.out.println("current speed " + this.currentSpeed * 36 / 10);
		// System.out.println("goal speed " + this.goalSpeed *36/10);
	}

	private Gap findGap(Game game) {
		int carsLeft = this.countCars(0, 200, false, game);
		TreeSet<Car> relevantCarsSet = (TreeSet<Car>) game.getCarsRight().subSet(game.getCarsLeft().first(), this);
		Car[] relevantCars = new Car[relevantCarsSet.size()];
		Iterator<Car>  itr = relevantCarsSet.iterator();
		for (int i = 0; i < relevantCars.length; i++) {
			relevantCars[i] = itr.next();
		}

		Gap result = null;
		for (int i = 0; i < relevantCars.length - 1; i++) {
			result = new Gap(relevantCars[0], relevantCars[1]);
			// TODO make accurate
			if (result.isValid(0)) {
				if (--carsLeft <= 0) {
					return result;
				}
			}
		}
		return result;
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
	public int countCars(double metersBehind, double metersAhead, boolean rightLane, Game game) {
		int counter = 0;
		Collection<Car> cars = isRightLane ? game.getCarsRight() : game.getCarsLeft();
		for (Car car : cars) {
			if (this.getDistance(car) < metersAhead && this.getDistance(car) > -metersBehind
					&& (game.getCarsLeft().isEmpty() || car.compareTo(game.getCarsLeft().first()) > 0)) {
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

		int carsLeft = this.countCars(0, 300, false, game);
		int carsRight = this.countCars(0, 300, true, game);
		System.out.println("This is car " + this);
		System.out.println(carsLeft + " cars left");
		System.out.println(carsRight + " cars right");
		return carsLeft + 1 > carsRight;
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
