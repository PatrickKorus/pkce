package car;

import org.newdawn.slick.SlickException;

import game.Game;

public class CMpassiveCar extends CMcorrectCar{

	
	/**
	 * 
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
	public CMpassiveCar(double meter, boolean isRightLane, double initSpeed, double initGoalSpeed, Game game) throws SlickException {
		super(meter, isRightLane, initSpeed, initGoalSpeed, game);
		// TODO: Implement passive car
	}

}
