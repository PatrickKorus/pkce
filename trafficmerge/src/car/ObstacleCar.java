package car;

import org.newdawn.slick.SlickException;

import game.Game;

public class ObstacleCar extends Car {

	public ObstacleCar(Game game)
			throws SlickException {
		super(Game.END_OF_LANE + 5, false, 0, game, Color.OBSTACLE);
		this.currentAcc = -1;
	}
	@Override
	public void regulate(Game game, int delta) {}

}
