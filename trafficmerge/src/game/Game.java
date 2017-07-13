package game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

//import game.Punkte;
import car.Car;
import game.spawner.CMSpawner;
import game.spawner.EntitySpawner;
import sign.Sign;

public class Game extends BasicGame {

	/* ---------------- PRESET: ---------------- */

	public static final float VEHICLE_LENGTH_M = 4.5f;
	public static final float VEHICLE_LENGTH_PIX = 83; // at scale 1

	public static final int LEFT_LANE_TOP = 365;
	public static final int LEFT_LANE_BOTTOM = 575;
	public static final int SPACE_BETWEEN_LANES = 36;
	public static final int height = 768;
	public static final int width = 1024;

	// to switch between classic and normal merge
	public static boolean classicMerge = true;
	public static float timeFactor = 1.0f;
	public static double pastObstacleDist = 100;
	public static double END_OF_LANE = 2000;//TOTAL_SIMULATION_DISTANCE - pastObstacleDist; // in
	public static double TOTAL_SIMULATION_DISTANCE = END_OF_LANE + pastObstacleDist; // in meter
	public static double SCALE = 2*Game.width*Game.VEHICLE_LENGTH_M/(Game.TOTAL_SIMULATION_DISTANCE*Game.VEHICLE_LENGTH_PIX);//0.09f;  0.09253012048192771;// 

																		// meter

	/* ---------------- END PRESET ---------------- */

	private static double meterToPixel;
	public static double meter_out_of_window;
	public static double meter_per_width;

	private Image background;
	private GameUI gameUi;
	private ArrayList<Sign> signs;
	private TreeSet<Car> carsLeft;
	private TreeSet<Car> carsRight;
	private ArrayList<Sign> delineators;
	private Obstacle obstacle;
	private LinkedList<Car> carsToRemoveRight;
	private LinkedList<Car> carsToRemoveLeft;
	private LinkedList<Car> carsToAddRight;
	private LinkedList<Car> carsToAddLeft;

	EntitySpawner spawner;
	
	public int carsEndCounter = 0;
	public int carsSpawnedCounter = 0;
	public int time = 0;
	public double[] averageLaneSpeed = new double[] { 0.0, 0.0 };

	public Game() {
		super("Traffic Merge Simulation");
		setConstants(SCALE);
	}

	public void setConstants(double scale) {
		meterToPixel = scale * Game.VEHICLE_LENGTH_PIX / Game.VEHICLE_LENGTH_M;
		meter_per_width = Game.width / meterToPixel;
		meter_out_of_window = Game.TOTAL_SIMULATION_DISTANCE - (Game.meter_per_width * 2);
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		background.draw();
		obstacle.draw(g);

		for (Car car : carsLeft) {
			car.drawWithCulling(g);
		}
		for (Car car : carsRight) {
			car.drawWithCulling(g);
		}
		for (Sign delineator : delineators) {
			delineator.drawWithCulling(g);
		}
		for (Sign sign : signs) {
			sign.drawWithCulling(g);
		}
		gameUi.render(container, g);
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		signs = new ArrayList<>();
		carsLeft = new TreeSet<>();
		carsRight = new TreeSet<>();
		delineators = new ArrayList<>(50);
		carsToRemoveLeft = new LinkedList<>();
		carsToRemoveRight = new LinkedList<>();
		carsToAddLeft = new LinkedList<>();
		carsToAddRight = new LinkedList<>();

		background = new Image("res/background_stripes.jpg");
		obstacle = new Obstacle(END_OF_LANE+100);
		// spawner = new manualSpawner();
		spawner = new CMSpawner();
		spawner.init(this);
		gameUi = new GameUI(this, container, spawner);

		/*
		 * Font fontPunkte = new AngelCodeFont("res/fonts/score_numer_font.fnt",
		 * new Image( "res/fonts/score_numer_font.png")); punkte = new
		 * Punkte(container.getWidth() - 180, 10, fontPunkte);
		 */
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		int newDelta = (int) Math.round(delta * timeFactor);
		Input input = container.getInput();
		spawner.spawn(newDelta, input, this);

		for (Car car : carsLeft) {
			car.update(newDelta);
		}
		for (Car car : carsRight) {
			car.update(newDelta);
		}

		// update cars / remove them if they are past the obstacle

		Car firstCar = null;
		if (!carsRight.isEmpty())
			firstCar = carsRight.first();
		if (firstCar != null && firstCar.meter > Game.TOTAL_SIMULATION_DISTANCE + 10) {
			GameUI.averageCarSpeed += 3.6*TOTAL_SIMULATION_DISTANCE/((double)(time - (double)carsRight.first().getSpawnTime()));
			carsRight.pollFirst();
			carsEndCounter++;
		}

		Car firstLeftCar = null;
		if (!carsLeft.isEmpty())
			firstLeftCar = carsLeft.first();
		if (firstLeftCar != null && firstLeftCar.meter > Game.TOTAL_SIMULATION_DISTANCE + 10) {
			carsLeft.pollFirst();
		}
		
		for (Sign sign : signs) {
			sign.update(newDelta);
		}

		for (Sign delineator : delineators) {
			delineator.update(newDelta);
		}

		// update lists
		carsLeft.removeAll(carsToRemoveLeft);
		carsToRemoveLeft.clear();
		carsRight.removeAll(carsToRemoveRight);
		carsToRemoveRight.clear();
		carsLeft.addAll(carsToAddLeft);
		carsToAddLeft.clear();
		carsRight.addAll(carsToAddRight);
		carsToAddRight.clear();

		gameUi.update(newDelta);
	}

	public void resortLists() {
		TreeSet<Car> sortedSetLeft = new TreeSet<>();
		sortedSetLeft.addAll(carsLeft);
		TreeSet<Car> sortedSetRight = new TreeSet<>();
		sortedSetRight.addAll(carsRight);
		carsLeft = sortedSetLeft;
		carsRight = sortedSetRight;
	}

	public void reset() throws SlickException {
		// clear cars
		carsLeft.clear();
		carsRight.clear();

		// reset variables
		time = 0;
		carsEndCounter = 0;
		carsSpawnedCounter = 0;
		averageLaneSpeed = new double[] { 0.0, 0.0 };
		GameUI.incomingTraffic = 0;
		GameUI.outgoingTraffic = 0;
		GameUI.averageCarSpeed = 0;
	}

	public void resetParams() throws SlickException {
		TOTAL_SIMULATION_DISTANCE = END_OF_LANE + 100;
		END_OF_LANE = 1100;
		timeFactor = 1.0f;
		spawner.setTrafficDensity(0.6);
		GameUI.aggressivePers = 0.0;
		GameUI.passivePers = 0.0;
		GameUI.scalingFactor = 1;
		gameUi.scaleToFit();
		rescale((float) SCALE);
		}

	public void rescale(float scale) throws SlickException {
		this.setConstants(scale);
		for (Car car : carsRight) {
			car.rescale(scale);
		}
		for (Car car : carsLeft) {
			car.rescale(scale);
		}
		obstacle.rescale(scale);
		Game.SCALE = scale;
	}
	


	public static int meterToPixel(double meter) {
		return (int) Math.round(meter * meterToPixel);
	}

	public double getMeter_out_of_window() {
		return meter_out_of_window;
	}

	public double getMeter_per_width() {
		return meter_per_width;
	}

	public ArrayList<Sign> getSigns() {
		return signs;
	}

	public TreeSet<Car> getCarsLeft() {
		return carsLeft;
	}

	public TreeSet<Car> getCarsRight() {
		return carsRight;
	}

	public void addCarLeft(Car car) {
		this.carsToAddLeft.add(car);
	}

	public void addCarRight(Car car) {
		this.carsToAddRight.add(car);
	}

	public void removeCarLeft(Car car) {
		this.carsToRemoveLeft.add(car);
	}

	public void removeCarRight(Car car) {
		this.carsToRemoveRight.add(car);
	}

	public Collection<Car> getCars() {
		ArrayList<Car> result = new ArrayList<>(carsLeft);
		result.addAll(carsRight);
		return result;
	}

	public void addSign(Sign sign) {
		this.signs.add(sign);
	}

	public void addDelineator(Sign delineator) {
		this.delineators.add(delineator);
	}

	public GameObject getObstacle() {
		return this.obstacle;
	}
	
	public void setObstacle(Obstacle obstacle){
		this.obstacle = obstacle;		
	}

	public void addCar(Car car) {
		if (car.isRightLane) {
			carsRight.add(car);
		} else {
			carsLeft.add(car);
		}
	}

}
