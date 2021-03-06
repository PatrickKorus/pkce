package game.spawner;

import java.util.Random;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import car.CMaggressiveCar;
import car.CMcorrectCar;
import car.CMpassiveCar;
import car.Car;
import car.NMCorrectCar;
import game.Game;
import game.GameUI;
import sign.Delineator;
import sign.LaneEndsSign;
import sign.Sign_Type;
import sign.SpeedLimitSign;

public class CMSpawner implements EntitySpawner  {
	//variables for calculations:
	/**
	 * generates random numbers 
	 */
	private Random randomGenerator = new Random();
	private Game game;
	
	//phantomCars for the calculation
	private CMcorrectCar phantomCarR;
	private CMcorrectCar phantomCarL;
	private CMcorrectCar startPos;
		
	//variables for spawning:
	private double trafficDensity = 0.333;  
	private double sigma = (1.0/trafficDensity);	
	/**
	 * [left , right] - in km/h
	 */
	private double[] maxSpd = new double[2];
	/**
	 * [left , right] - in km/h
	 */
	private double[] laneSpd = new double[2];	
	
	//left:
	private int leftTime = 0;	
	private double leftTTrigger = 0.0;
	private boolean leftSpawnFree = true;
	
	//right:
	private int rightTime = 0;
	private double rightTTrigger = 0.0;
	private boolean rightSpawnFree = true;	
	

	
	@Override
	public void init(Game game) throws SlickException {
		initSigns(game);
		this.game = game;
		phantomCarR = new CMcorrectCar(0,true,0,0, game);
		phantomCarL = new CMcorrectCar(0,false,0,0, game);
		startPos = new CMcorrectCar(0,true,0,0,game);
		spawnRandomCar(150, 130, true, game);
		spawnRandomCar(300, 250, false, game);
		leftTTrigger = calcTrigger(false);
		rightTTrigger = calcTrigger(true);
		}
	
	@Override
	public void spawn(int delta, Input input, Game game) throws SlickException {

			Car[] lastCars = lastCarOnLane(game);
			
			maxSpd[0] = 150.0;
			maxSpd[1] = 0.75 * maxSpd[0];
			laneSpd[0] = 170.0;
			laneSpd[1] = 0.75 * laneSpd[0];
			
//===left lane==============================================================================================					
			if(leftTime >= leftTTrigger){	
				// spd-calc:
				if(lastCars[0] !=  null){
					laneSpd[0] = lastCars[0].getCurrentSpeed();
					//maxSpd[0]:
					double actDist = startPos.getDistance(lastCars[0]);
					phantomCarL.setSpeed(lastCars[0].getCurrentSpeed());
					double minDist = phantomCarL.getSafetyDistance(lastCars[0]);
					if(actDist >= minDist+12){
						// convert laneSpd to mps for the calculation
						maxSpd[0] = mpsTOkmh((0.5 * kmhTOmps(laneSpd[0])) 
								+ Math.sqrt((Math.pow(0.5 * kmhTOmps(laneSpd[0]) , 2)/4)+ 2* phantomCarL.getMaxBreak() * (actDist - 6)));
						if(maxSpd[0] > 200)
							maxSpd[0] = 0;
						leftSpawnFree = true;
					}else{
						//not enough space -> no cars spawning
						leftSpawnFree = false;
					}
				}
				else{//no car on the lane
					maxSpd[0] = 0;
					leftSpawnFree = true;
				}
				//spawn if enough space and reset timer
				if(leftSpawnFree){
					if(rightTime <= 0.75 * rightTTrigger || laneSpd[1] <= 60){
						spawnRandomCar(maxSpd[0], laneSpd[0], false, game);
						leftSpawnFree = false;
						leftTime = 0;
						leftTTrigger = calcTrigger(false);
					}
					else{
						rightTime = (int) rightTTrigger + 1;
					}
				}
			}
			else{
				leftTime += delta;			
			}
//===right lane==============================================================================================
			if(rightTime >= rightTTrigger){	
				// spd-calc:
				if(lastCars[1] !=  null){
					laneSpd[1] = lastCars[1].getCurrentSpeed();
					//maxSpd[1]:
					double actDist = startPos.getDistance(lastCars[1]);
					phantomCarR.setSpeed(lastCars[1].getCurrentSpeed());
					double safetyDist = phantomCarR.getSafetyDistance(lastCars[1]);
					if(actDist >= safetyDist+12){
						// convert laneSpd to mps for the calculation
						maxSpd[1] = mpsTOkmh((0.5 * kmhTOmps(laneSpd[1])) 
								+ Math.sqrt((Math.pow(0.5 * kmhTOmps(laneSpd[1]) , 2)/4)+ 2* phantomCarR.getMaxBreak() * (actDist - 6)));
						rightSpawnFree = true;
					}else{
						//not enough space -> no cars spawning
						rightSpawnFree = false;
					}
				}
				else{//no car on the lane
					maxSpd[1] = 0;
					rightSpawnFree = true;
				}
				//limit right speed to left speed
				if(laneSpd[0] <= maxSpd[1]){
					maxSpd[1] = laneSpd[0];
					if(laneSpd[1] >= maxSpd[1]*0.75){
						laneSpd[1] = maxSpd[1];
					}
				}
				//spawn if enough space and reset timer
				if(rightSpawnFree){
					spawnRandomCar(maxSpd[1], laneSpd[1], true, game);
					rightSpawnFree = false;
					rightTime = 0;
					rightTTrigger = calcTrigger(true);
				}
			}
			else{
				rightTime += delta;			
				}
//===========================================================================================================		
		}
	
	@Override
	/**
	 * Set the traffic density to another value
	 * @param Density - 0 < Density <= 1
	 */
	public void setTrafficDensity ( double Density){
		if(Density <= 1.0 && Density > 0){
			if(trafficDensity != Density){
				leftTime = 0;
				rightTime = 0;
				leftTTrigger = calcTrigger(false);
				rightTTrigger = calcTrigger(true);
			}
			trafficDensity = Density;
			sigma = (1.0/trafficDensity);
		}
	}
	
	@Override
	/**
	 * returns current traffic density
	 * @return - 0 < Density <= 1
	 */
	public double getTrafficDensity() {
		return trafficDensity;
	}
	
	/**
	 * spawns a random car with various behaviour and initial spd. 
	 * @param maxSpd - in km/h  ->  highest possible spd, so that the car wont crash if the car in the front is slowing down.
	 *  		In case that there is no car set: maxSpd = 0
	 * @param LaneSpd - in km/h  ->  actual speed driven on the lane
	 * @param rightLane True -> car spawns right. False -> car spawns left
	 * @acrRightSpd current speed on the right lane. Needed to spawn cars in a close speedrange around this.
	 */
	private void spawnRandomCar(double maxSpd, double LaneSpd, boolean rightLane, Game game)throws SlickException{
		double initSpd = 60.0;
		double sigma = 6; //random value. gets replaced
		double goalSpd = 130; //random value. gets replaced
		Car car;
		int type;
		//choose a driver type. Right now with equal chances for each type.
		double typeVal = randomGenerator.nextDouble();
		
		if(typeVal <= GameUI.aggressivePers){
			type = 0;
		}else if(typeVal <= 1-GameUI.passivePers){
			type = 1;
		}else{
			type = 2;
		}
		
		if(LaneSpd <= 0.5 * maxSpd)
			LaneSpd = 2 * maxSpd / 3;
		LaneSpd += Math.abs((maxSpd-LaneSpd)/3.0);
		
		/*
		 * Standard deviation:
		 * (maxSpdLane-actLaneSpd)/1 -> 1 Sigma for aggressive driver. a lot drive more aggressive, with evtl to small distance
		 * (maxSpdLane-actLaneSpd)/2 -> 2 Sigma for standard driver. most drive ok
		 * (maxSpdLane-actLaneSpd)/3 -> 3 Sigma for careful driver. nearly anyone drives in a good range
		 */	
		
		//maxSpd gr��er als LaneSpeed -> Erwartungswert ist LaneSpd. Je nach Typ liegen 1-3 Sigma zwischen LaneSpd und maxSpd
		if(maxSpd >= LaneSpd){
			sigma = (maxSpd-LaneSpd)/(2*(type+1));
		}
		//maxSpd == 0 -> entweder kein Auto da, oder Verkehr steht, dann ist auch LaneSpd == 0 => Regelung alleine �ber LaneSpd 
		//1-3 Sigma sind 10% der LaneSpd
		else if(maxSpd == 0){
			sigma = (LaneSpd)/(10*(type+1));	
		}
		//maxSpd < LaneSpd Regelung geht nicht, da Verkehr min so schnell wie max Spd, 
		// da ansonsten Verkehr zu schnell zum bremsen(Widerspruch zur implementierung)
		
		
		
		initSpd = (randomGenerator.nextGaussian()*sigma)+ LaneSpd+(maxSpd-LaneSpd)/3.0;
		
		if(rightLane){
			if(initSpd <= 0.6 * laneSpd[1]){
				initSpd = 0.6 * laneSpd[1];
			}
		}
		else{
			if (initSpd <= 0.8 * laneSpd[0]){
				initSpd = 0.8 * laneSpd[0];
			}
		}
		
		//calculate goal spd
		if(rightLane){
//			goalSpd = (randomGenerator.nextGaussian()*sigma) + 110;
			goalSpd = (randomGenerator.nextGaussian()*10) + 110;
		}
		else{
//			goalSpd = (randomGenerator.nextGaussian()*sigma) + 140;
			goalSpd = (randomGenerator.nextGaussian()*20) + 140;
		}
		
		switch(type){
		case 0: //aggressiveCar	
			car = new CMaggressiveCar(0, rightLane, initSpd, goalSpd, game);
			break;
		case 1: //correctCar
			if(Game.classicMerge)
				car = new CMcorrectCar(0, rightLane, initSpd, goalSpd, game);
			else
				car = new NMCorrectCar(0, rightLane, initSpd, goalSpd, game);
			break;
		default: //CMpassiveCar	
			car = new CMpassiveCar(0, rightLane, initSpd, goalSpd, game);
			break;
		}
		car.setSpawnTime(game.time);
		game.addCar(car);
		game.carsSpawnedCounter++;
	}

	/**
	 * gives back the last car on each lane
	 * @param game game
	 * @return last cars [left lane, right lane]
	 * @throws SlickException 
	 */
	private Car[] lastCarOnLane (Game game) throws SlickException{
		//==================
		// direct output of a cars distance to spawn? -> meter
		Car[] lastcar = new Car[2];
		//searches the last car on each lane
		if(!game.getCarsLeft().isEmpty())
			lastcar[0] = phantomCarL.getSurroundingCars(game)[0];
		if(!game.getCarsRight().isEmpty())
			lastcar[1] = phantomCarR.getSurroundingCars(game)[0];//game.getCarsRight().last();
		return lastcar;
		
	}
	
	/**
	 * convert speed from km/h to mps
	 * @param kmh - speed in km/h
	 * @return - speed in mps
	 */
	private double kmhTOmps(double kmh) {
		return kmh / 3.6;
	}
	
	/**
	 * convert speed from mps to km/h 
	 * @param mps - speed in mps
	 * @return - speed in km/h
	 */
	private double mpsTOkmh (double mps){
		return mps * 3.6;
	}
	
	/**
	 * Calculates the trigger times for new cars
	 * @return -sek
	 */
	private double calcTrigger(boolean lane){
		double trigger = Math.abs(((randomGenerator.nextGaussian()*sigma)+sigma))*1000;
		double[] carDens = carDensity();
		double diffDens = carDens[0] - carDens[1];
		if(lane){//&& laneSpd[0] <= laneSpd[1]){
			trigger *= (1-diffDens);
		}
		else{
			trigger *= (1+diffDens);
			trigger *= 1.5; //buffing left lane a little bit
		}			
		return trigger;
	}

	/**
	 * counts the cars on each lane
	 * @return [left , right]
	 */
	private double[] carDensity(){
		double countDist = 50 * Game.VEHICLE_LENGTH_M; 
		double[] carDens = new double[2];

		carDens[0] = (double)phantomCarL.countCars(0, countDist, false, game)/50.0;
		carDens[1] = (double)phantomCarR.countCars(0, countDist, true, game)/50.0;		
		
		return carDens;
	}
	

	private void initSigns(Game game) throws SlickException {
		if(Game.classicMerge){
			game.addSign(new LaneEndsSign(Game.END_OF_LANE, Sign_Type.LINE_END_0));
			game.addSign(new LaneEndsSign(Game.END_OF_LANE - 210, Sign_Type.LINE_END_0));
			game.addSign(new LaneEndsSign(Game.END_OF_LANE - 410, Sign_Type.LINE_END_0));
			game.addSign(new SpeedLimitSign(Game.END_OF_LANE - 800, Sign_Type.SPD_100));
			game.addSign(new SpeedLimitSign(Game.END_OF_LANE - 400, Sign_Type.SPD_80));
			game.addSign(new SpeedLimitSign(Game.END_OF_LANE - 200, Sign_Type.SPD_80));
		}
		else{
			game.addSign(new LaneEndsSign(Game.END_OF_LANE, Sign_Type.LINE_END_0));
			game.addSign(new LaneEndsSign(Game.END_OF_LANE - 210, Sign_Type.LINE_END_0));
			game.addSign(new LaneEndsSign(Game.END_OF_LANE - 810, Sign_Type.LINE_END_0));
			game.addSign(new SpeedLimitSign(Game.END_OF_LANE - 1400, Sign_Type.SPD_100));
			game.addSign(new SpeedLimitSign(Game.END_OF_LANE - 800, Sign_Type.SPD_80));
			game.addSign(new SpeedLimitSign(Game.END_OF_LANE - 200, Sign_Type.SPD_80));
		}
		
		for (double d = 5; d < Game.TOTAL_SIMULATION_DISTANCE; d += 50) {
			game.addDelineator(new Delineator(d));
		}
	}
}