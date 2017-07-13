package car.gaps;

import car.Car;
import game.Game;

public class Gap {

	private Car headCar, tailCar;
	private double start, end, middle;

	/**
	 * A Gap between front car and tail car.
	 * @param headCar
	 * @param tailCar
	 */
	public Gap(Car headCar, Car tailCar) {
		this.headCar = headCar;
		this.tailCar = tailCar;
		update();
	}

	
	public void update() {
		this.start = headCar == null ? Game.END_OF_LANE : headCar.getMeterDistance();
		this.end = tailCar == null ? 0 : tailCar.getMeterDistance();
		middle = (start + end) / 2;
	}

	/**
	 * 
	 * @param passingCar
	 * @param safeSpace
	 * @return
	 */
	public boolean isSafe(Car passingCar, double safeSpace) {
		update();
		boolean tailCarDoesntCrash = tailCar == null ? true
				: tailCar.getDistance(passingCar) > tailCar.getSafetyDistance(passingCar) + safeSpace;
		boolean passingCarDoesntCrash = headCar == null ? true
				: passingCar.getDistance(headCar) > passingCar.getSafetyDistance(headCar) + safeSpace;
		return tailCarDoesntCrash && passingCarDoesntCrash;
	}

	public double getPosition() {
		update();
		return middle;
	}

	/**
	 * Returns if the Gap is of valid size.
	 * @param validSize
	 * @return
	 */
	public boolean isValid(double validSize) {
		update();
		return tailCar.getDistance(headCar) > validSize;
	}

	/**
	 * Returns if a car with given position and allowed maxspeedMpS reaches this gap before the given parameter.
	 * @param position
	 * @param maxSpeedMpS
	 * @param reachableBefore
	 * @param safe_space
	 * @return
	 */
	public boolean isReachable(double position, double maxSpeedMpS, double reachableBefore, double safe_space) {
		update();
		double time = (reachableBefore - this.getPosition()) / this.getSpeedMpS();
		boolean stillExists = true;
		if (tailCar != null && headCar != null) {
			stillExists = tailCar.getMeterDistance() + time * tailCar.getCurrentSpeed()
					+ safe_space < headCar.getMeterDistance() + time * headCar.getCurrentSpeed();
		}
		return stillExists && position + time*maxSpeedMpS > this.getPosition() + time*getSpeedMpS() + 100;

	}

	/**
	 * 
	 * @return Average Speed of front and tail car in m/s
	 */
	public double getSpeedMpS() {
		update();
		if (tailCar == null) {
			return headCar.getCurrentSpeed() * 10.0 / 36.0;
		} else if (headCar == null) {
			return tailCar.getCurrentSpeed() * 10.0 / 36.0;
		} else {
			return (headCar.getCurrentSpeed() + tailCar.getCurrentSpeed()) / 7.2;
		}
	}

	/**
	 * Gives the max acceleration of front and back car.
	 * @return
	 */
	public double getAcc() {
		update();
		return Math.max(headCar == null ? 0 : headCar.getCurrentAcc(), tailCar == null ? 0 : tailCar.getCurrentAcc());
	}

}
