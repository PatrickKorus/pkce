package game;

import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import car.BasicCar;
//import game.Punkte;
import car.Car;

public class Game extends BasicGame {

	public static float SCALE = 0.2f;

	// PRESET:
	public static float VEHICLE_LENGTH_M = 4.5f;
	public static float VEHICLE_LENGTH_PIX = 83; // at scale 1
	public static double TOTAL_SIMULATION_DISTANCE = 1200; // in meter
	public static double END_OF_LANE = 1100; // in meter

	public static int LEFT_LANE_TOP = 365;
	public static int LEFT_LANE_BOTTOM = 575;
	public static int SPACE_BETWEEN_LANES = 36;
	public static int height = 768;
	public static int width = 1024;

	public static double meterToPixel;
	public static double meter_out_of_window;
	public static double meter_per_width;

	private Image hintergrund;
	// private Punkte punkte;
	private List<Car> cars = new LinkedList<Car>();
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
		hintergrund.draw();
		for (Car car : cars) {
			car.draw(g);
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		hintergrund = new Image("res/Hintergrund.png");
		new Image("res/basicCar/normal.png");
		Car car = new BasicCar(900.0, false, 100);
		cars.add(car);
		/*
		 * Font fontPunkte = new AngelCodeFont("res/fonts/score_numer_font.fnt",
		 * new Image( "res/fonts/score_numer_font.png")); punkte = new
		 * Punkte(container.getWidth() - 180, 10, fontPunkte);
		 */ }

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		Input input = container.getInput();

		int mausX = input.getMouseX();
		int mausY = input.getMouseY();

		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			moreCars(mausX, mausY);
		}

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

	boolean laneSetter = true;

	private void moreCars(int mausX, int mausY) throws SlickException {
		Car car = new BasicCar(400, laneSetter, 100);
		laneSetter = !laneSetter;
		cars.add(car);
	}

	public static double getMeterOutOfWindow() {
		// TODO Auto-generated method stub
		return meter_out_of_window;
	}
}
