package game.spawner;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import car.BasicCar;
import car.Car;
import game.Game;
import sign.Sign;
import sign.Sign_Type;

public class manualSpawner implements EntitySpawner {

	@Override
	public void init(Game game) throws SlickException {
		initSigns(game);
	}

	@Override
	public void spawn(int delta, Input input, Game game) throws SlickException {
		

		int mausX = input.getMouseX();
		int mausY = input.getMouseY();
		
		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			moreCars(mausX, mausY, game);
		}
	}
	
	boolean laneSetter = true;
	private void moreCars(int mausX, int mausY, Game game) throws SlickException {
		Car car = new BasicCar(0, laneSetter, 100, game);
		laneSetter = !laneSetter;
		game.addCar(car);
	}

	private void initSigns(Game game) throws SlickException {
		game.addSign(new Sign(Game.END_OF_LANE, Sign_Type.LINE_END_0));
		game.addSign(new Sign(Game.END_OF_LANE - 400, Sign_Type.LINE_END_0));
		game.addSign(new Sign(Game.END_OF_LANE - 750, Sign_Type.LINE_END_0));
		game.addSign(new Sign(Game.END_OF_LANE - 1000, Sign_Type.SPD_100));
		game.addSign(new Sign(Game.END_OF_LANE - 800, Sign_Type.SPD_80));
		game.addSign(new Sign(Game.END_OF_LANE - 200, Sign_Type.SPD_60));
	}

}
