package car;

import org.newdawn.slick.SlickException;

import game.Game;
import sign.Sign;

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
	}

	@Override
	public void regulate(Game game) {
		
	}
	
	double getSpeedLimit(Game game) {
		 for (Sign sign : game.getSigns()) {
			 	if (sign.getDistance(this) < 200.0) {
			 		if (sign.getValue() > 1)
			 			return sign.getValue();
			 }
		 }
		 return this.goalSpeed;
	}

}
