package car;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.newdawn.slick.SlickException;

import car.gaps.Gap;
import game.Game;
import sign.Sign;
import sign.SpeedLimitSign;

public class CMcorrectCar extends Car {

	protected double area0;
	protected double areaI;
	protected double areaII;
	double PANIC_FACTOR = 1.5;
	double SPEEDING = 1.0;
	double SAFE_SPACE = 8;
	int REACTION_DELAY = 250;
	// This is the improvement, this car finds to change to the quicker lane
	// earlier
	protected double speedImprovementFactor = 1.5;

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
		Random r = new Random();
		MAX_ACC = acc(7);
		MAX_BREAKING_FORCE = acc(3);
		areaI = Game.END_OF_LANE - 500 + Math.round(r.nextGaussian() * 50.0);
		areaII = Game.END_OF_LANE
				- 150 /* + Math.round(r.nextGaussian() * 50.0) */;
		PANIC_FACTOR = 1.5 + r.nextGaussian() * 0.2;
		SPEEDING = 1.0 + r.nextGaussian() * 0.1;
		SAFE_SPACE = 8 + r.nextGaussian() * 3;
		speedImprovementFactor = 1.6 + r.nextGaussian() * 0.4;
		REACTION_DELAY = 600 + (int) Math.round(r.nextGaussian() * 200.0);
	}

	private boolean crashed = false;

	int delay = 0;
	double currentPanic = 1;
	Gap gapAimedFor;
	double speeding = SPEEDING;
	// private boolean driveThrough;

	@Override
	public void regulate(Game game, int delta) {

		delay += delta;
		Car[] surroundingCars = getSurroundingCars(game);

		// This causes that if the car ahead is breaking this car is in "panic"
		if (delay >= REACTION_DELAY) {
			currentPanic = surroundingCars[0] != null && surroundingCars[0].isBreaking() ? PANIC_FACTOR : 1;
			delay = 0;
		}

		double distanceCarUpFront = this.getDistance(surroundingCars[0]);
		// double safetyDistance = currentPanic * getSafetyDistance(carUpFront);

		// stop if crashed
		if (distanceCarUpFront < 5 && !crashed) {
			errorPrint(surroundingCars[0]);
			this.currentSpeed = 0;
			crashed = true;
			return;
		}

		// priority 1: preserve critical distance to car&obstacle
		double error = 0;

		// this.regulateTo(safetyDistance, distanceCarUpFront, 50);
		error = this.regulateTo(surroundingCars[0], currentPanic, 10);
		// this.currentAcc = error;
		// return;

		if (!isRightLane) {
			// System.out.println("car ahead regulation:" + error);
			error = Math.min(error, this.regulateTo(game.getObstacle().getMeterDistance(), 0, 0, 10));
			// System.out.println("Obstacle regulation:" + error);
			if (this.meter > Game.END_OF_LANE) {
				error = this.MAX_ACC;
			}
		}


		// priority 3: position dependent
		if (this.meter < this.areaI) {
			error = this.reactAreaI(game, surroundingCars, error);
		} else if (this.meter >= this.areaI && this.meter < this.areaII) {
			error = this.reactAreaII(game, surroundingCars, error);
		} else if (this.meter >= this.areaII && this.meter < Game.END_OF_LANE) {
			error = this.reactAreaIII(game, surroundingCars, error);
		}

		// lowest priority: regulate to SpeedLimit
		double speedLimit = Math.min(kmhTOmps(this.getSpeedLimit(game)), this.goalSpeed);
		error = Math.min(error, this.regulateTo(currentSpeed, speeding * speedLimit, 4));

		// stop indicating after lane has changed
		if (this.isRightLane == true) {
			this.stopInidicating();
		}
		// apply new acceleration
		this.currentAcc = error;
	}

	protected double reactAreaI(Game game, Car[] surroundingCars, double currentErr) {
		double error = currentErr;
		// if average speed of the other lane is considerably quicker - change
		// lane
		if (this.isRightLane
				&& Math.min(game.averageLaneSpeed[0], this.goalSpeed) > speedImprovementFactor
						* game.averageLaneSpeed[1]
				&& this.getDistance(surroundingCars[0]) < 400 && surroundingCars[1] != null
				&& surroundingCars[0].getCurrentSpeed() > this.currentSpeed * speedImprovementFactor) {
			Gap gap = new Gap(surroundingCars[1], surroundingCars[2]);
			if (gap.isSafe(this, SAFE_SPACE)) {
				error = this.regulateTo(gap.getPosition(), gap.getSpeedMpS(), gap.getAcc(), 1);
				this.isChangingLane = true;
			}
		}
		return error;
	}

	protected double reactAreaII(Game game, Car[] surroundingCars, double currentErr) {

		double error = currentErr;
		if (isRightLane) {
			// // area V
			// if fewer cars right than left
			if (moreCarsLeftThanRight(game)) {
				if (surroundingCars[0] == null && !game.getCarsLeft().isEmpty()) {
					error = this.regulateTo(game.getCarsLeft().first(), 1, 10);
					
				} else if (surroundingCars[0] != null) {
					error = this.regulateTo(surroundingCars[0], 1.5*currentPanic, 5);
				}
			}
		} else {
			// area II
			// find gap
			Double speedLimit = getSpeedLimit(game);
			try {
				gapAimedFor = this.findGap(SAFE_SPACE, surroundingCars[0], 1.1 * speeding * speedLimit);
			} catch (SlickException e) {
				e.printStackTrace();
			}

			if (gapAimedFor != null) {
				speeding = 1.1 * SPEEDING;
				double newerror = (8*currentErr + 2*regulateTo(gapAimedFor.getPosition(), gapAimedFor.getSpeedMpS(), gapAimedFor.getAcc(),
						6))/10;
				if (newerror + currentSpeed > 0.8 * this.goalSpeed) {
					error = newerror;
				}
			}
			this.isIndicating = true;
		}

		return Math.min(error, currentErr);
	}

	protected double reactAreaIII(Game game, Car[] surroundingCars, double currentErr) {

		double error = currentErr;

		if (isRightLane) {
			// area VI
			Car indicatingUpFront = surroundingCars[1];
			if (moreCarsLeftThanRight(game) && this.getDistance(indicatingUpFront) > 6) {
				error = Math.min(error, this.regulateTo(indicatingUpFront, 1.5, 6));
			}

		} else {
			// area III
			error = Math.min(error, this.regulateTo(surroundingCars[1], currentPanic, 5));
			// if no car will crash
			Gap gap = new Gap(surroundingCars[1], surroundingCars[2]);
			if (gap.isSafe(this, 0))
				this.isChangingLane = true;
		}

		return error;
	}

	private Gap findGap(double safe_space, Car ahead, double maxSpeed) throws SlickException {

		double METERS_BEHIND = 100;

		Gap gap = null;
		Car from = ahead == null ? new BasicCar(areaII, false, 0, game) : ahead;
		Car to = new BasicCar(this.meter - METERS_BEHIND, false, 0, game);
		SortedSet<Car> relevantCarsSet = game.getCarsRight().subSet(from, to);
		Iterator<Car> itr = relevantCarsSet.iterator();

		Car headCar = null;
		if (!itr.hasNext())
			return null;
		Car tailCar = itr.next();

		while (itr.hasNext()) {

			// next gap
			headCar = tailCar;
			tailCar = itr.next();

			gap = new Gap(headCar, tailCar);
			if (gap.isReachable(meter, maxSpeed, areaII,
					safe_space) /* && gap.isValid(safe_space) */) {
				return gap;
			}

		}

		return null;
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
			newacc = Math.max(0.1 * importance * error, -MAX_BREAKING_FORCE);
		} else {
			newacc = Math.min(0.05 * error, MAX_ACC);
		}
		return newacc;
	}

	/**
	 * 
	 * @param p0
	 *            this cars position
	 * @param p1
	 *            position seek
	 * @param v0
	 *            this cars speed
	 * @param v1
	 *            speed of the seeked position
	 * @param a1
	 *            acc of seeked position
	 * @param importance
	 *            parameter that increases the value of accelerating
	 * @return
	 */
	public double regulateTo(double p1, double v1, double a1, float importance) {

		double p0 = this.meter;
		double v0 = this.currentSpeed;
		// double a1 = -Car.MAX_POSSIBLE_BREAKING_FORCE;

		double x = p0 - p1;
		double xdot = v0 - v1;
		double b = 2.5;
		double k = 2;

		double newAcc = a1 - (4 * k / (b * b)) * (b * xdot + k * x);

		if (newAcc < 0) {
			newAcc = Math.max(newAcc, -MAX_BREAKING_FORCE);
		} else {
			newAcc = Math.min(newAcc, MAX_ACC);
		}
		return newAcc;
	}

	private double regulateTo(Car car, double distanceFactor, float importance) {
		if (car == null) {
			return this.MAX_ACC;
		}
		return regulateTo(car.getMeterDistance() - distanceFactor * this.getSafetyDistance(car), car.currentSpeed,
				-Car.MAX_POSSIBLE_BREAKING_FORCE, importance);
	}

	/**
	 * Gives the current Speed limit (least speed limit that this car has passed
	 * or is within a range of 50 meters.
	 * 
	 * @param game
	 * @return
	 */
	double getSpeedLimit(Game game) {

		double speedLimit = 180 * 36 / 10;
		for (Sign sign : game.getSigns()) {
			if (sign instanceof SpeedLimitSign && this.getDistance(sign) < 50.0 && sign.getValue() < speedLimit)
				speedLimit = sign.getValue();
		}
		return speedLimit;
	}

	/**
	 * Returns surrounding cars
	 * 
	 * @param game
	 * @return [car ahead; car ahead on the other lane; car behind on the other
	 *         lane]
	 */
	Car[] getSurroundingCars(Game game) {

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

		// cars that change lane are treated as if they are on both lanes
		if (this.isRightLane && carUpFront[1] != null && carUpFront[1].isChangingLane
				&& this.getDistance(carUpFront[1]) < this.getDistance(carUpFront[0]))
			carUpFront[0] = carUpFront[1];

		if (this.isChangingLane && carUpFront[1] != null
				&& this.getDistance(carUpFront[1]) < this.getDistance(carUpFront[0]))
			carUpFront[0] = carUpFront[1];
		return carUpFront;
	}

	/**
	 * For debugging purposes
	 * 
	 * @param carUpFront
	 *            the Car crashed into
	 */
	private void errorPrint(Car carUpFront) {
		System.err.println("crash");
		System.out.println("Back: \t Acc->" + this.currentAcc + " Speed->" + this.currentSpeed + " goal->"
				+ this.goalSpeed + "\t ID " + this);
		System.out.println("Front: \t Acc->" + carUpFront.currentAcc + " Speed->" + carUpFront.currentSpeed + " goal->"
				+ carUpFront.goalSpeed + "\t ID " + carUpFront);
		System.out.println(
				"=================================================================================================");
	}

}
