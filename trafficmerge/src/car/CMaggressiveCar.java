package car;

import java.util.Random;

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

		this.setColor(Color.RED, Game.SCALE);
		Random r = new Random();
		MAX_ACC = acc(6);
		MAX_BREAKING_FORCE = acc(1.5);
		areaI = Game.END_OF_LANE - 300 + Math.round(r.nextGaussian()*50.0);
		areaII = Game.END_OF_LANE - 100 + Math.round(r.nextGaussian()*30.0);
		PANIC_FACTOR = 1.1 + r.nextGaussian()*0.5;
		SPEEDING = 1.2 + r.nextGaussian()*0.1;
		SAFE_SPACE = Math.max(5 + r.nextGaussian()*3, 0);
		speedImprovementFactor = 1.3 + r.nextGaussian()*0.2;
	}

}
