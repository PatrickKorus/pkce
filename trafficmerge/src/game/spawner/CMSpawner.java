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
	
	/**
	 * generates random numbers 
	 */
	private Random randomGenerator = new Random();
	
	@Override
	public void init(Game game) throws SlickException {
		initSigns(game);
		
	}

	private boolean spawncars = true;

	
	@Override
	public void spawn(int delta, Input input, Game game) throws SlickException {
		if (input.isMousePressed(Input.MOUSE_RIGHT_BUTTON)) {
			spawncars = !spawncars;
		}
		//Right Click turns car spawning on and off
		if(spawncars){

			
			
			
			
			
			
			
			
			
			
			
		}
	}
	/**
	 * spawns a random car with various behaviour and initial spd. 
	 * @param maxSpd highest possible spd, so that the car wont crash if the car in the front is slowing down.
	 *  		In case that there is no car set: maxSpd = 0
	 * @param rightLane True -> car spawns right. False -> car spawns left
	 * @acrRightSpd current speed on the right lane. Needed to spawn cars in a close speedrange around this.
	 */
	private void spawnRandomCar(double maxSpd, boolean rightLane, double LaneSpd, Game game)throws SlickException{
		double initSpd = 60.0;
		double sigma = 6;
		Car car;

		int type = ((int)randomGenerator.nextFloat()*2);
		
		//TODO: eventuellbessere Standardabweichung suchen
		
		//maxSpd größer als LaneSpeed -> Erwartungswert ist LaneSpd. Je nach Typ liegen 1-3 Sigma zwischen LaneSpd und maxSpd
		if(maxSpd >= LaneSpd){
			sigma = (maxSpd-LaneSpd)/(type+1);
		}
		//maxSpd == 0 -> entweder kein Auto da, oder Verkehr steht, dann ist auch LaneSpd == 0 => Regelung alleineüber LaneSpd 
		//1-3 Sigma sind 10% der LaneSpd
		else if(maxSpd == 0){
			sigma = (LaneSpd)/(10*(type+1));	
		}
		//maxSpd < LaneSpd Regelung geht nicht, da Verkehr min so schnell wie max Spd, 
		// da ansonsten Verkehr zu schnell zum bremsen(Widerspruch zur implementierung)


		//chooses a driver type. Right now with equal chances for each type.
		/*
		now: Erwartungswert ist actLaneSpd:
		
		Standardabweichung ist:
		(maxSpdLane-actLaneSpd)/1 -> 1 Sigma umgebung für aggressiven fahrer.
		(maxSpdLane-actLaneSpd)/2 -> 2 Sigma Umgebung für standard fahrer.
		(maxSpdLane-actLaneSpd)/3 -> 3 Sigma umgebung für vorsichtigen fahrer.
		*/
		switch(type){
		case 0: //CMaggressiveCar
			// Calculate spawn-speed -> 1Sigma(a lot drive more aggressive, with evtl to small distance)
			initSpd = (int)(randomGenerator.nextGaussian()*sigma)+ LaneSpd;
			
			car = new CMaggressiveCar(0, rightLane, initSpd, game);
			break;
		case 1: //CMcorrectCar
			// Calculate spawn-speed -> 2Sigma(most drive ok)
			initSpd = (int)(randomGenerator.nextGaussian()*sigma)+ LaneSpd;
			
			car = new CMcorrectCar(0, rightLane, initSpd, game);
			break;

		default: //CMpassiveCar
			// Calculate spawn-speed -> 3Sigma (nearly anyone drives in a good range)
			initSpd = (int)(randomGenerator.nextGaussian()*sigma)+ LaneSpd;
			
			car = new CMpassiveCar(0, rightLane, initSpd, game);
			break;
		}
		game.addCar(car);
		
	}

	/**
	 * gives back the last car on each lane
	 * @param game game
	 * @return last cars [left lane, right lane]
	 * @throws SlickException 
	 */
	private Car[] lastCarOnLane (Game game) throws SlickException{
		//==================
		// not spawning cars to get the distance of the driving cars. 
		// TODO: direct output of a cars distance to spawn? -> meter
		Car phantomCarR = new CMcorrectCar(0,true,0, game);
		Car phantomCarL = new CMcorrectCar(0,false,0, game);
		Car[] lastcar = new Car[2];
		//searches the last car on each lane
		for(Car car : game.getCars()){
			//checkout right lane
			if(car.isRightLane()){
				//check out for cars closer to spawn
				if(phantomCarR.getDistance(car) <= phantomCarR.getDistance(lastcar[1]) || lastcar[1] == null){
					lastcar[1] = car;
				}
			}
			//checkout left lane
			else{
				if(phantomCarL.getDistance(car) <= phantomCarL.getDistance(lastcar[0]) || lastcar[0] == null){
					lastcar[0] = car;
				}
			}
		}
		return lastcar;
		
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
