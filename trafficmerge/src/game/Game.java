package game;

import java.util.ArrayList;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

//import game.Punkte;
import car.Car;
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
	public static float SCALE = 0.4f;
	public static double TOTAL_SIMULATION_DISTANCE = 1200; // in meter
	public static double END_OF_LANE = 1150; // in meter
	
	/* ---------------- END PRESET ---------------- */

	private static double meterToPixel;
	public static double meter_out_of_window;
	public static double meter_per_width;

	private Image background;
	// private Punkte punkte;
	private ArrayList<Sign> signs;
	private ArrayList<Car> cars;
	private Obstacle obstacle;
	
	EntitySpawner spawner;

	// TODO this already counts passing cars but is unused so far
	@SuppressWarnings("unused")
	private int carsEndCounter;

	public Game() {
		super("Traffic Merge Simulation");
		calculateConstants(SCALE);
		System.out.println(meter_out_of_window + "m not visible");
	}

	private void calculateConstants(float scale) {
		meterToPixel = scale * Game.VEHICLE_LENGTH_PIX / Game.VEHICLE_LENGTH_M;
		meter_per_width = Game.width / meterToPixel;
		meter_out_of_window = Game.TOTAL_SIMULATION_DISTANCE - (Game.meter_per_width * 2);

	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		background.draw();
		for (Car car : cars) {
			car.drawWithCulling(g);
		}
		for (Sign sign : signs) {
			sign.drawWithCulling(g);
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		signs = new ArrayList<>();
		cars = new ArrayList<>();
		background = new Image("res/background.png");
		obstacle = new Obstacle(END_OF_LANE);
		spawner = new manualSpawner();
		spawner.init(this);
		/*
		 * Font fontPunkte = new AngelCodeFont("res/fonts/score_numer_font.fnt",
		 * new Image( "res/fonts/score_numer_font.png")); punkte = new
		 * Punkte(container.getWidth() - 180, 10, fontPunkte);
		 */
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		Input input = container.getInput();
		spawner.spawn(delta, input, this);
		

		// update cars / remove them if they are past the obstacle
		if (cars.size() > 0) {
			for (int i = 0; i < cars.size(); i++) {
				if (cars.get(i).meter > Game.TOTAL_SIMULATION_DISTANCE + 50) {
					cars.remove(i);
					carsEndCounter++;
				} else {
					cars.get(i).update(delta);
				}
			}
		}

		// Fenster mit ESC sclieﬂen
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}
	}

	public static int meterToPixel(double meter) {
		return (int) Math.round(meter*meterToPixel);
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
	
}
