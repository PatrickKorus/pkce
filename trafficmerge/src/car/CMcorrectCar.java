package car;

import java.util.Collection;

import org.newdawn.slick.SlickException;

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

	public CMcorrectCar(double meter, boolean isRightLane, double initSpeed, Game game) throws SlickException {
		super(meter, isRightLane, initSpeed, game, Color.BLUE);
		MAX_ACC = acc(12);
		MAX_BREAKING_FORCE = acc(3);
		areaI = Game.END_OF_LANE - 500;
		areaII = Game.END_OF_LANE - 200;
		System.out.println(isRightLane);

	}

	@Override
	public void regulate(Game game) {
		//
		// System.out.println(iteration++);
		// System.out.println(isRightLane);
		Car[] carsUpFront = getCarUpFront(game);
		Car carUpFront = carsUpFront[0];

		double distanceCarUpFront = this.getDistance(carUpFront);
		double minDistance = minDist(carUpFront);
		double safetyDistance = minDistance + this.currentSpeed / 2;
		// System.out.println(isRightLane);

		// stop if crashed
		if (distanceCarUpFront < 5) {
			this.currentSpeed = 0;
			System.err.println("crash");
		}

		// priority 1: preserve critical distance

		double error = this.regulateTo(minDistance, distanceCarUpFront, 10);
		System.out.println("error mindist " + error);
		// System.out.println("critical Distance = " + error );

		// priority 2: drive safe distance
		error = Math.min(error, this.regulateTo(safetyDistance, distanceCarUpFront, 5));
		System.out.println("error recdist " + error);
		// System.out.println("rec Distance = " + error);

		// priority 3: position dependent
		if (this.meter > this.areaI && this.meter < this.areaII) {
			if (isRightLane) {
				// // area V
				Car indicatingUpFront = carsUpFront[1];
				double distanceIndicating = this.getDistance(indicatingUpFront);
				// make some space for cars that indicate
				error = Math.min(error, this.regulateTo(this.minDist(indicatingUpFront), distanceIndicating, 4));
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
			} else {
				// area III
				// TODO
				// if no car will crash
				this.isChangingLane = true;
			}
		}

		// lowest priority: regulate to SpeedLimit
		double speedLimit = kmhTOmpers(this.getSpeedLimit(game));
		if (this.currentSpeed > speedLimit) {
			error = Math.min(error, this.regulateTo(currentSpeed, speedLimit, 4));

			System.out.println("error speedlimit " + error);
		}
		
		// stop indicating after lane has changed
		 if (this.isRightLane == true) {
		 this.isIndicating = false;
		 }

		this.currentAcc = error;
		System.out.println("applied " + error);
		System.out.println(speedLimit * 36 / 10);
		System.out.println(this.currentSpeed * 36 / 10);
	}

	// TODO redundancy..
	/**
	 * calculates the minimal distance to stop
	 *
	 * 
	 * @param speed
	 *            -speed of the car in the front
	 * @return
	 */
	public double minDist(double speed) {
		return (this.currentSpeed / (2 * this.MAX_BREAKING_FORCE)) * (this.currentSpeed - speed) + 6;
	}

	/**
	 * calculates the minimal distance to stop
	 * 
	 * @param carUpFront
	 *            - car ahead
	 * @return
	 */
	public double minDist(Car carUpFront) {

		double speedCarUpFront = 130.0;
		if (carUpFront != null) {
			speedCarUpFront = carUpFront.getCurrentSpeed();
		}
		return (this.currentSpeed / (2 * this.MAX_BREAKING_FORCE)) * (this.currentSpeed - speedCarUpFront) + 10;
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
	 * returns safety distance to the car ahead
	 * 
	 * @param minDistance
	 *            - minimal distance to stop
	 * @return
	 */
	public double safetyDist(double minDistance) {
		return minDistance + this.currentSpeed / 2;
	}

	public double regulateTo(double goal, double current, float importance) {
		double error = current - goal;
		if (error < 0.001 && error > -0.001) {
			this.currentAcc = 0;
			return 0;
		}
		double newacc;
		if (error < 0) {
			newacc = Math.max(importance * error, -MAX_BREAKING_FORCE);
		} else {
			newacc = Math.min(0.8 * importance * error, MAX_ACC);
		}
		this.currentAcc = newacc;
		return newacc;
	}

	double getSpeedLimit(Game game) {
		double speedLimit = 100.0;
		for (Sign sign : game.getSigns()) {
			if (sign instanceof SpeedLimitSign && this.getDistance(sign) < 50.0 && sign.getValue() < speedLimit)
				speedLimit = sign.getValue();
		}
		return speedLimit;
	}
	
	/**
	 * 
	 * @param game
	 * @return [car ahead; car ahead on the other lane; car behind on the other lane]
	 */
	Car[] getCarUpFront(Game game) {
		Car[] carUpFront = new Car[2];
		double smallestDistance0 = Game.TOTAL_SIMULATION_DISTANCE;
		double currentDistance0 = 0;
		double smallestDistance1 = Game.TOTAL_SIMULATION_DISTANCE;
		// TODO
		for (Car car : game.getCarsLeft()) {
			currentDistance0 = this.getDistance(car);
			// for all cars are in front of this car
			if (currentDistance0 > 0) {
				// find the closest car on the same lane
				if (car.isRightLane() == this.isRightLane() || car.isChangingLane) {
					if (currentDistance0 < smallestDistance0) {
						smallestDistance0 = currentDistance0;
						carUpFront[0] = car;
					}
				} else {
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
