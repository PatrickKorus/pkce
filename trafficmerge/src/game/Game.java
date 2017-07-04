package game;

import java.util.ArrayList;

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
import game.spawner.manualSpawner;
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

	// TODO make these variable during runtime
	public static float SCALE = 0.09f;
	public static float timeFactor = 1.0f;
	public static double TOTAL_SIMULATION_DISTANCE = 1200; // in meter
	public static double END_OF_LANE = 1100; // in meter

	/* ---------------- END PRESET ---------------- */

	private static double meterToPixel;
	public static double meter_out_of_window;
	public static double meter_per_width;

	private Image background;
	// private Punkte punkte;
	private ArrayList<Sign> signs;
	private ArrayList<Car> cars;
	private ArrayList<Sign> delineators;
	private Obstacle obstacle;

	EntitySpawner spawner;
	TextField scaler;
	TextField timeControler;

	// TODO this already counts passing cars but is unused so far
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

		for (Car car : cars) {
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
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		signs = new ArrayList<>();
		cars = new ArrayList<>();
		delineators = new ArrayList<>();
		background = new Image("res/background_stripes.jpg");
		obstacle = new Obstacle(END_OF_LANE);
		//spawner = new manualSpawner();
		spawner = new CMSpawner();
		spawner.init(this);
		/*
		 * Font fontPunkte = new AngelCodeFont("res/fonts/score_numer_font.fnt",
		 * new Image( "res/fonts/score_numer_font.png")); punkte = new
		 * Punkte(container.getWidth() - 180, 10, fontPunkte);
		 */
		scaler = new TextField(container, container.getDefaultFont(), 50, 50, 100, 20);
		timeControler = new TextField(container, container.getDefaultFont(), 50, 100, 100, 20);

	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {

		int newDelta = (int) Math.round(delta * timeFactor);
		Input input = container.getInput();
		spawner.spawn(newDelta, input, this);

		// update cars / remove them if they are past the obstacle
		if (cars.size() > 0) {
			for (int i = 0; i < cars.size(); i++) {
				if (cars.get(i).meter > Game.TOTAL_SIMULATION_DISTANCE + 50) {
					cars.remove(i);
					carsEndCounter++;
				} else {
					cars.get(i).update(newDelta);
				}
			}
		}
		
		for (Sign sign : signs) {
			sign.update(newDelta);
		}
		
		for (Sign delineator : delineators) {
			delineator.update(newDelta);
		}

		// TODO in einen Parser auskoppeln?
		try {
			String value = scaler.getText();
			float newscale = Float.parseFloat(value);
			if (newscale > 0.01)
				this.rescale(newscale);
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			// TODO: handle exception
		}
		
		try {
			String value = timeControler.getText();
			float newFactor = Float.parseFloat(value);
			if (newFactor > 0.1)
				Game.timeFactor = newFactor;
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			// TODO: handle exception
		}

		// Fenster mit ESC sclieﬂen
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}
	}

	public void rescale(float scale) throws SlickException {
		this.setConstants(scale);
		for (Car car : cars) {
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

	public ArrayList<Car> getCars() {
		return cars;
	}

	public void addCar(Car car) {
		this.cars.add(car);
	}

	public void addSign(Sign sign) {
		this.signs.add(sign);
	}

	public void addDelineator(Sign delineator) {
		this.delineators.add(delineator);
	}

}
