package car;

import org.newdawn.slick.SlickException;

import game.Game;

public class CMcorrectCar extends Car {
	
	/**
	 * Car that behaves correctly w.r.t. classic merge (Reissverschlussverfahren)
	 * 
	 * @param meter
	 * @param isRightLane
	 * @param initSpeed
	 * @param game
	 * @throws SlickException 
	 */

	public CMcorrectCar(double meter, boolean isRightLane, double initSpeed, Game game) throws SlickException {
		super(meter, isRightLane, initSpeed, game, Color.BLUE);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void regulate(Game game) {
		// TODO Auto-generated method stub

	}

}
