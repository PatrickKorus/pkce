package car;

import org.newdawn.slick.SlickException;

import game.Game;

public class CMaggressiveCar extends CMcorrectCar {

	/**
	 * 
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
	public CMaggressiveCar(double meter, boolean isRightLane, double initSpeed, Game game) throws SlickException {
		super(meter, isRightLane, initSpeed, game);
		//TODO: Implement aggressive car
	}

}
