package game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;

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

	// TODO: Make these variable during runtime
	public static float SCALE = 0.09f;
	public static float timeFactor = 1.0f;
	public static double TOTAL_SIMULATION_DISTANCE = 1200; // in meter

	public static double END_OF_LANE = TOTAL_SIMULATION_DISTANCE - 100; // in
																		// meter

	/* ---------------- END PRESET ---------------- */

	private static double meterToPixel;
	public static double meter_out_of_window;
	public static double meter_per_width;

	private Image background;
	private ArrayList<Sign> signs;
	private PriorityQueue<Car> carsLeft;
	private PriorityQueue<Car> carsRight;
	private ArrayList<Sign> delineators;
	private Obstacle obstacle;
	private LinkedList<Car> carsToRemoveRight;
	private LinkedList<Car> carsToRemoveLeft;
	
	EntitySpawner spawner;
	TextField scaler;
	TextField timeControler;
	TextField trafficDensity;

	// TODO: This already counts passing cars but is unused so far
	@SuppressWarnings("unused")
	private int carsEndCounter;

	public Game() {
		super("Traffic Merge Simulation");
		setConstants(SCALE);
		System.out.println(meter_out_of_window + "m not visible");
	}

	private void setConstants(float scale) {
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

		scaler.render(container, g);
		timeControler.render(container, g);
		trafficDensity.render(container, g);
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		signs = new ArrayList<>();
		carsLeft = new PriorityQueue<>();
		carsRight = new PriorityQueue<>();
		delineators = new ArrayList<>(50);
		carsToRemoveLeft = new LinkedList<>();
//		carsToRemoveRight = new LinkedList<>();
		background = new Image("res/background_stripes.jpg");
		obstacle = new Obstacle(END_OF_LANE);
		// spawner = new manualSpawner();
		spawner = new CMSpawner();
		spawner.init(this);
		/*
		 * Font fontPunkte = new AngelCodeFont("res/fonts/score_numer_font.fnt",
		 * new Image( "res/fonts/score_numer_font.png")); punkte = new
		 * Punkte(container.getWidth() - 180, 10, fontPunkte);
		 */
		scaler = new TextField(container, container.getDefaultFont(), 50, 50, 100, 20);
		timeControler = new TextField(container, container.getDefaultFont(), 50, 100, 100, 20);
		trafficDensity = new TextField(container, container.getDefaultFont(), 50, 150, 100, 20);
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

		if (carsRight.peek() != null && carsRight.peek().meter > Game.TOTAL_SIMULATION_DISTANCE + 10) {
			carsRight.poll();
		}

		for (Sign sign : signs) {
			sign.update(newDelta);
		}

		for (Sign delineator : delineators) {
			delineator.update(newDelta);
		}
		

		carsLeft.removeAll(carsToRemoveLeft);
		carsToRemoveLeft.clear();
		
		
		

		// TODO: In einen Parser auskoppeln?
		// rescaling
		try {
			String value = scaler.getText();
			float newscale = Float.parseFloat(value);
			if (newscale > 0.01)
				this.rescale(newscale);
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			// TODO: handle exception
		}

		// change timeLapse
		try {
			String value = timeControler.getText();
			float newFactor = Float.parseFloat(value);
			if (newFactor > 0.1)
				Game.timeFactor = newFactor;
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			// TODO: handle exception
		}

		// change traffic density
		try {
			String value = trafficDensity.getText();
			float newDensity = Float.parseFloat(value);
			if (newDensity <= 1.0 && newDensity > 0)
				spawner.setTrafficDensity(newDensity);
		} catch (NumberFormatException e) {

		}

		// Fenster mit ESC sclie�en
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}
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

	public PriorityQueue<Car> getCarsLeft() {
		return carsLeft;
	}

	public PriorityQueue<Car> getCarsRight() {
		return carsRight;
	}

	public void addCarLeft(Car car) {
		this.carsLeft.add(car);
	}

	public void addCarRight(Car car) {
		this.carsRight.add(car);
	}

	public void removeCarLeft(Car car) {
		this.carsToRemoveLeft.add(car);
	}
	
//	public void removeCarRight(Car car){
//		this.carsToRemoveRight.add(car);
//	}

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

	public void addCar(Car car) {
		if (car.isRightLane) {
			carsRight.add(car);
		} else {
			carsLeft.add(car);
		}
	}

}
