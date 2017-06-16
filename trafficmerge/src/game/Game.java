package game;

import java.util.*;

import org.newdawn.slick.*;

//import game.Punkte;
import car.Car;
import car.BasicCar;

public class Game extends BasicGame { 

	public static int LEFT_LANE_TOP = 370;
	public static int RIGHT_LANE_TOP = 375;
	public static int LEFT_LANE_BOTTOM = 560;
	public static int RIGHT_LANE_BOTTOM = 565;
	public static int height = 786;
	public static int width = 1024;
	
	public static double halfMeterMax = 600; // in meter
	public static double meterToPixel = 1.706;
	
	private Image hintergrund;
	private Image auto;
//	private Punkte punkte;
	private List<Car> cars = new ArrayList<Car>();

	public Game() {
		super("Traffic Merge Simulation");
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
		auto = new Image("res/basicCar/car.png");
		Car car = new BasicCar(300.0, false, auto, 2.0, 0.005);
		cars.add(car);
/*		Font fontPunkte = new AngelCodeFont("res/fonts/score_numer_font.fnt", new Image(
				"res/fonts/score_numer_font.png"));
		punkte = new Punkte(container.getWidth() - 180, 10, fontPunkte);
*/	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		Input input = container.getInput();

			int mausX = input.getMouseX();
			int mausY = input.getMouseY();

			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				moreCars(mausX,mausY);
			}
			
		for(int j=0; j < cars.size() ; j++ ){
			Car car = cars.get(j);
			car.update(delta);}	
		
		
		// Fenster mit ESC scließen
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}
	}

	
	private void moreCars(int mausX, int mausY) {
		Car car = new BasicCar(605.0, true, auto, 2.0, 0.005);
		cars.add(car);
	}
}


