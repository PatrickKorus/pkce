package game.spawner;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import car.CMcorrectCar;
import car.Car;
import game.Game;
import sign.Delineator;
import sign.LaneEndsSign;
import sign.Sign_Type;
import sign.SpeedLimitSign;

public class manualSpawner implements EntitySpawner {

	@Override
	public void init(Game game) throws SlickException {
		initSigns(game);

//		game.addCar(new BasicCar(0, true, 60, game));
		
		
	}
	
	private double trafficDensity = 0.6;  
	private int deltaCOunter = 0;
	private boolean spawncar = true;

	@Override
	public void spawn(int delta, Input input, Game game) throws SlickException {
		
		int mausX = input.getMouseX();
		int mausY = input.getMouseY();
		if(spawncar){
		deltaCOunter += delta;
		if (deltaCOunter > 1400) {
			deltaCOunter = 0;
			moreCars(mausX, mausY, game);
		}
		}
		if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
			spawncar = !spawncar;
		}
		
		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			moreCars(mausX, mausY, game);
		}
	}
	
	boolean laneSetter = true;
	
	private void moreCars(int mausX, int mausY, Game game) throws SlickException {
		Car car = new CMcorrectCar(0, laneSetter, 90.0, 90.0, game);
		laneSetter = !laneSetter;
		game.addCar(car);
	}

	private void initSigns(Game game) throws SlickException {
		game.addSign(new LaneEndsSign(Game.END_OF_LANE, Sign_Type.LINE_END_0));
		game.addSign(new LaneEndsSign(Game.END_OF_LANE - 210, Sign_Type.LINE_END_0));
		game.addSign(new LaneEndsSign(Game.END_OF_LANE - 410, Sign_Type.LINE_END_0));
		game.addSign(new SpeedLimitSign(Game.END_OF_LANE - 800, Sign_Type.SPD_100));
		game.addSign(new SpeedLimitSign(Game.END_OF_LANE - 400, Sign_Type.SPD_80));
		game.addSign(new SpeedLimitSign(Game.END_OF_LANE - 200, Sign_Type.SPD_80));
		
		for (double d = 5; d < Game.TOTAL_SIMULATION_DISTANCE; d += 50) {
			game.addDelineator(new Delineator(d));
		}
	}
	
	@Override
	/**
	 * Sets the traffic density to another value
	 * @param Density - 0 < Density <= 1
	 */
	public void setTrafficDensity ( double Density){
			trafficDensity = Density;
		}
	
	@Override
	/**
	 * returns current traffic density
	 * @return - 0 < Density <= 1
	 */
	public double getTrafficDensity() {
		return trafficDensity;
	}

}
