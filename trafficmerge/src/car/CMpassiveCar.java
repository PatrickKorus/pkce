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
//		super(meter, isRightLane, initSpeed, initGoalSpeed, game, Color.PASSIVE);

		this.setColor(Color.PASSIVE, Game.SCALE);
		MAX_ACC = acc(12);
		MAX_BREAKING_FORCE = acc(2.5);
		areaI = Game.END_OF_LANE - 800;
		areaII = Game.END_OF_LANE - 500;
		PANIC_FACTOR = 2.5;
		SAFE_SPACE = 10;
		SPEEDING = 0.9;
	}


}
