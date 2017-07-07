package car;

import org.newdawn.slick.SlickException;

import game.Game;

public class CMpassiveCar extends CMcorrectCar{

	private final double areaI;
	private final double areaII;
	
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
//		super(meter, isRightLane, initSpeed, initGoalSpeed, game, Color.PASSIVE);

		MAX_ACC = acc(12);
		MAX_BREAKING_FORCE = acc(3);
		areaI = Game.END_OF_LANE - 500;
		areaII = Game.END_OF_LANE - 200;
	}


}
