package car;

import org.newdawn.slick.SlickException;

import car.CMcorrectCar;
import game.Game;

public class NMCorrectCar extends CMcorrectCar {

	public NMCorrectCar(double meter, boolean isRightLane, double initSpeed, double initGoalSpeed, Game game)
			throws SlickException {
		super(meter, isRightLane, initSpeed, initGoalSpeed, game);
		this.areaI = Game.END_OF_LANE - 1000;
		this.areaII = Game.END_OF_LANE - 800;
		this.isBlockingBothLanes = true;
	}

	@Override
	protected double reactAreaIII(Game game, Car[] surroundingCars, double currentErr) {
		if (this.meter > Game.END_OF_LANE - 100) {
			this.isBlockingBothLanes = false;
			this.stopInidicating();
		}
		return super.reactAreaIII(game, surroundingCars, currentErr);
	}

}
