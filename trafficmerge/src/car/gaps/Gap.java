package car.gaps;

import car.Car;

public class Gap {

	private Car frontCar, tailCar;
	private double start, end, middle;
//	headSpeed, tailSpeed;
//	private final double STANDARD_MIN_GAP_SIZE = 10;
//	private boolean valid;
//	private boolean validTimeLeft;

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

	public double getSpeed() {
		return frontCar == null ? 180 : frontCar.getCurrentSpeed();
	}
	
	public double getAcc() {
		return Math.max(frontCar == null ? 0 : frontCar.getCurrentAcc(), tailCar == null ? 0 : tailCar.getCurrentAcc());
	}

}
