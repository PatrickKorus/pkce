package car;

import game.Game;

public class CMcorrectCar extends Car {
	
	/**
	 * Car that behaves correctly w.r.t. classic merge (Reissverschlussverfahren)
	 * 
	 * @param meter
	 * @param isRightLane
	 * @param initSpeed
	 * @param game
	 */

	public CMcorrectCar(double meter, boolean isRightLane, double initSpeed, Game game) {
		super(meter, isRightLane, initSpeed, game);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void regulate(Game game) {
		// TODO Auto-generated method stub

	}

}
