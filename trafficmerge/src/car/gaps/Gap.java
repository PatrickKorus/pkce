package car.gaps;

import car.Car;

public class Gap {

	private Car frontCar, tailCar;
	private double start, end, middle;
	// headSpeed, tailSpeed;
	// private final double STANDARD_MIN_GAP_SIZE = 10;
	// private boolean valid;
	// private boolean validTimeLeft;

	public Gap(Car frontCar, Car tailCar) {
		this.frontCar = frontCar;
		this.tailCar = tailCar;

	}

	public void update() {
		this.start = frontCar.getMeterDistance();
		this.end = tailCar.getMeterDistance();
		// this.headSpeed = frontCar.getCurrentSpeed();
		// this.tailSpeed = tailCar.getCurrentSpeed();
		middle = (start + end) / 2;
	}

	public boolean isSafe(Car passingCar, double safeSpace) {

		boolean tailCarDoesntCrash = tailCar == null ? true
				: tailCar.getDistance(passingCar) > tailCar.getSafetyDistance(passingCar) + safeSpace;
		boolean passingCarDoesntCrash = frontCar == null ? true
				: passingCar.getDistance(frontCar) > passingCar.getSafetyDistance(frontCar) + safeSpace;
		return tailCarDoesntCrash && passingCarDoesntCrash;
	}

	public double getPosition() {
		return middle;
	}

	public boolean isValid(double validSize) {
		return tailCar.getDistance(frontCar) > validSize;
	}

	public boolean isReachable(double position, double maxSpeedMpS, double reachableBefore, double safe_space) {
		double time = (reachableBefore - this.getPosition()) / this.getSpeedMpS();
		boolean stillExists = true;
		if (tailCar != null && frontCar != null) {
			stillExists = tailCar.getMeterDistance() + time * tailCar.getCurrentSpeed()
					+ safe_space < frontCar.getMeterDistance() + time * frontCar.getCurrentSpeed();
		}
		return stillExists && position + time*maxSpeedMpS > this.getPosition() + time*getSpeedMpS() + 100;

	}

	/**
	 * 
	 * @return Average Speed of front and tail car in m/s
	 */
	public double getSpeedMpS() {
		if (tailCar == null) {
			return frontCar.getCurrentSpeed() * 10.0 / 36.0;
		} else if (frontCar == null) {
			return tailCar.getCurrentSpeed() * 10.0 / 36.0;
		} else {
			return (frontCar.getCurrentSpeed() + tailCar.getCurrentSpeed()) / 7.2;
		}
	}

	public double getAcc() {
		return Math.max(frontCar == null ? 0 : frontCar.getCurrentAcc(), tailCar == null ? 0 : tailCar.getCurrentAcc());
	}

}
