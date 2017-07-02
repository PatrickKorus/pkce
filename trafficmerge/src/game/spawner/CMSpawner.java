package game.spawner;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import car.CMaggressiveCar;
import car.CMcorrectCar;
import car.CMpassiveCar;
import car.Car;

import java.util.Random;

import game.Game;
import sign.Delineator;
import sign.LaneEndsSign;
import sign.Sign_Type;
import sign.SpeedLimitSign;

public class CMSpawner implements EntitySpawner  {
	
	private Random randomGenerator = new Random();
	
	@Override
	public void init(Game game) throws SlickException {
		initSigns(game);
		
	}

	@Override
	public void spawn(int delta, Input input, Game game) throws SlickException {
		// TODO Auto-generated method stub
		
	}
	/**
	 * spawns a random car with various behaviour and initial spd. 
	 * @param maxSpdLane highest possible spd, so that the car wont crash if the car in the front is slowing down.
	 *  		In case theres no car, the max allowed spd is taken
	 * @param rightLane True -> car spawns right. False -> car spawns left
	 * @acrRightSpd current speed on the right lane. Needed to spawn cars in a close speedrange around this.
	 */
	private void spawnRandomCar(int maxSpdLane, boolean rightLane, int actRightSpd, Game game)throws SlickException{
		double minSpd = 60.0; //just some random values. Correct values will overwrite below
		double initSpd = 60.0;
		Car car;
		if(rightLane){
			minSpd = actRightSpd;
			initSpd = (int)(randomGenerator.nextFloat()*100)+ minSpd;

		}else{
			minSpd = actRightSpd+10;
		}
		
		//chooses a driver type. Right now with equal chances for each car-type.
		switch((int)randomGenerator.nextFloat()*2){
		case 0: //CMcorrectCar
			car = new CMcorrectCar(0, rightLane, initSpd, game);
			break;
		case 1: //CMaggressiveCar
			car = new CMaggressiveCar(0, rightLane, initSpd, game);
			break;
		default: //CMpassiveCar
			car = new CMpassiveCar(0, rightLane, initSpd, game);
			break;
		}
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
}
