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
	public CMaggressiveCar(double meter, boolean isRightLane, double initSpeed, double initGoalSpeed, Game game) throws SlickException {
		super(meter, isRightLane, initSpeed, initGoalSpeed, game);

		this.setColor(Color.AGGRESSIVE, Game.SCALE);
		MAX_ACC = acc(6);
		MAX_BREAKING_FORCE = acc(1.5);
		areaI = Game.END_OF_LANE - 300;
		areaII = Game.END_OF_LANE - 100;
		PANIC_FACTOR = 1.1;
		SAFE_SPACE = 0;
		SPEEDING = 1.2;
//		areaI = Game.END_OF_LANE - 500;
//		areaII = Game.END_OF_LANE - 200;
		//TODO: Implement aggressive car
	}

}
