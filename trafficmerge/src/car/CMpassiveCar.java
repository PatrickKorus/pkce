package car;

import java.util.Random;

import org.newdawn.slick.SlickException;

import game.Game;

public class CMpassiveCar extends CMcorrectCar {

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
	public CMpassiveCar(double meter, boolean isRightLane, double initSpeed, double initGoalSpeed, Game game)
			throws SlickException {
		super(meter, isRightLane, initSpeed, initGoalSpeed, game);
		// super(meter, isRightLane, initSpeed, initGoalSpeed, game,
		// Color.PASSIVE);

		this.setColor(Color.PASSIVE, Game.SCALE);
		Random r = new Random();
		MAX_ACC = acc(12);
		MAX_BREAKING_FORCE = acc(2.5);
		areaI = Game.END_OF_LANE - 800 + Math.round(r.nextGaussian() * 50.0);
		areaII = Game.END_OF_LANE - 500 + Math.round(r.nextGaussian() * 50.0);
		PANIC_FACTOR = 2.0 + r.nextGaussian() * 0.5;
		SPEEDING = 0.9 + r.nextGaussian() * 0.1;
		SAFE_SPACE = Math.max(10 + r.nextGaussian() * 3, 0);
		speedImprovementFactor = 2 + r.nextGaussian() * 0.2;
	}

}
