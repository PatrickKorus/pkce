package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;

import car.Car;
import game.spawner.EntitySpawner;

public class GameUI {

	private Game game;
	private GameContainer container;
	private EntitySpawner spawner;
	private boolean isPaused;

	public static boolean carData = false;
	public static double aggressivePers = 0.0;
	public static double passivePers = 0.0;

	public TextField scaler;
	public TextField timeControler;
	public TextField trafficDensity;
	public TextField aggressiveDriver;
	public TextField passiveDriver;
	public TextField pastObstacleDistance;

	public static long systemTimer = 0;
	private int AverageSpeedTimer = 0;
	public static float scalingFactor = 1;

	// average in-/output:
	public static double outgoingTraffic = 0;
	public static double incomingTraffic = 0;
	private int inOutTimer = 0;
	private int totalCountStart = 0;
	private int totalCarsStart = 0;
	public static double averageCarSpeed = 0;

	public GameUI(Game game, GameContainer container, EntitySpawner spawn) {
		this.spawner = spawn;
		this.game = game;
		this.container = container;
		scaler = new TextField(container, container.getDefaultFont(), 50, 155, 100, 20);
		timeControler = new TextField(container, container.getDefaultFont(), 50, 205, 100, 20);
		trafficDensity = new TextField(container, container.getDefaultFont(), 50, 255, 100, 20);
		aggressiveDriver = new TextField(container, container.getDefaultFont(), 300, 155, 100, 20);
		passiveDriver = new TextField(container, container.getDefaultFont(), 300, 205, 100, 20);
		pastObstacleDistance = new TextField(container, container.getDefaultFont(), 300, 255, 100, 20);
		isPaused = false;
	}

	public void render(GameContainer container, Graphics g) {// TODO:
																// Beschreibungen
																// checken
		// Input:
		g.drawString("Skalierung: " + Math.round(scalingFactor * 100) / 100.0, scaler.getX(), scaler.getY() - 20);
		scaler.render(container, g);
		g.drawString("Zeitraffer: " + Math.round(Game.timeFactor * 100) / 100.0, timeControler.getX(),
				timeControler.getY() - 20);
		timeControler.render(container, g);
		g.drawString("Verkehrsdichte: " + Math.round(spawner.getTrafficDensity() * 100) / 100.0, trafficDensity.getX(),
				trafficDensity.getY() - 20);
		trafficDensity.render(container, g);
		g.drawString("Anteil an aggressiven Fahrern: " + Math.round(aggressivePers * 100) / 100.0,
				aggressiveDriver.getX(), aggressiveDriver.getY() - 20);
		aggressiveDriver.render(container, g);
		g.drawString("Anteil an passiven Fahrern: " + Math.round(passivePers * 100) / 100.0, passiveDriver.getX(),
				passiveDriver.getY() - 20);
		passiveDriver.render(container, g);
		g.drawString("Strecke hinter Engstelle: " + (Game.TOTAL_SIMULATION_DISTANCE - Game.END_OF_LANE) + " m",
				pastObstacleDistance.getX(), pastObstacleDistance.getY() - 20);
		pastObstacleDistance.render(container, g);

		// Data-Output:
		// general data:
		if (Game.classicMerge)
			g.drawString("Aktiv: Reissverschlussverfahren", container.getWidth() - 350, 25);
		else
			g.drawString("Aktiv: Alternatives Verfahren", container.getWidth() - 350, 25);
		g.drawString("Simulationsdauer: " + game.time / 3600 + " h " + (game.time / 60) % 60 + " min "
				+ (game.time % 60) + " sek", container.getWidth() - 350, 50);
		g.drawString("Autos:" + game.carsEndCounter, container.getWidth() - 350, 75);

		// Average lane speed:
		g.drawString("Durchschnittsgeschwindigkeit:", container.getWidth() - 350, 100);
		g.drawString("~>Linke Bahn: " + Math.round(game.averageLaneSpeed[0] * 100) / 100.0 + " km/h",
				container.getWidth() - 300, 120);
		g.drawString("~>Rechte Bahn:" + Math.round(game.averageLaneSpeed[1] * 100) / 100.0 + " km/h",
				container.getWidth() - 300, 140);

		// average In-/Output
		g.drawString("Eingangsverkehrsdichte: " + Math.round(60 * incomingTraffic * 100) / 100.0 + " Autos/min",
				container.getWidth() - 350, 165);
		g.drawString("~>Total:" + Math.round((60 * game.carsSpawnedCounter / (float) game.time) * 100) / 100.0
				+ " Autos/min", container.getWidth() - 300, 185);
		g.drawString("Ausgangsverkehrsdichte: " + Math.round(60 * outgoingTraffic * 100) / 100.0 + " Autos/min",
				container.getWidth() - 350, 210);
		g.drawString(
				"~>Total:" + Math.round((60 * game.carsEndCounter / (float) game.time) * 100) / 100.0 + " Autos/min",
				container.getWidth() - 300, 230);
		g.drawString(
				"\u00D8-Geschwindigkeit(Auto): "
						+ Math.round(100 * (averageCarSpeed) / ((double) game.carsEndCounter)) / 100.0 + " km/h",
				container.getWidth() - 350, 255);

		// Shortcuts:
		// first column
		g.drawString("    -> Zeige Autoinformationen", 50, container.getHeight() - 75);
		if (Game.classicMerge) {
			g.drawString("    -> Alternatives Verfahren", 50, container.getHeight() - 50);
		} else {
			g.drawString("    -> Reissverschlussverfahren", 50, container.getHeight() - 50);
		}

		// second column
		g.drawString("    -> Reset der Anzeigeeinstellungen", (float) (container.getWidth() * 0.33),
				container.getHeight() - 75);
		g.drawString("    -> Reset der Simulation", (float) (container.getWidth() * 0.33), container.getHeight() - 50);

		// third column
		g.drawString("    -> Pausiere Simulation", (float) (container.getWidth() * 0.66), container.getHeight() - 75);

	}

	public void printToConsole(Game game) {
		System.out.println(Game.classicMerge ? "Classic Merge Solutions for:" : "New Merge solution for:");
		System.out.println("Verkehrsdichte (prozent): \t Anteil St�rer: \t SpeedFactor:");
		System.out
				.println("\t \t " + spawner.getTrafficDensity() + " \t \t " + Math.round(60 * (aggressivePers + passivePers) * 100)/100.0 + " \t \t \t " + Game.timeFactor);
		System.out.println("Results are :");
		System.out.println(
				"Autos: \t Eingangsverkehrsdichte: \t Ausgangsverkehrsdichte: \t Durchschnittsgeschwindigkeit");
		System.out.println(game.carsEndCounter + " \t \t " + Math.round(60 * incomingTraffic * 100) / 100.0 + " Autos/min"
				+ " \t \t " + Math.round(60 * outgoingTraffic * 100) / 100.0 + " Autos/min" + " \t \t "
				+ Math.round(100 * (averageCarSpeed) / ((double) game.carsEndCounter)) / 100.0 + " km/h");

	}

	public void update(int delta) throws SlickException {
		boolean enterPressed = container.getInput().isKeyPressed(Input.KEY_ENTER);
		// rescaling
		try {// TODO: 2 equals you could show 200% or you show the thing 2 times
				// as big -> only 50% ?
			String value = scaler.getText();
			float newscale = Float.parseFloat(value);
			if (newscale > 0.01 && enterPressed) {
				if (newscale != scalingFactor) {
					scalingFactor = newscale;
					scaleToFit();
				}
				scaler.setText("");
			}
		} catch (NumberFormatException e) {
			scaler.setText("");
		}

		// change timeLapse
		try {
			String value = timeControler.getText();
			float newFactor = Float.parseFloat(value);
			if (newFactor > 0.1 && enterPressed) {
				Game.timeFactor = newFactor;
				timeControler.setText("");
			}
		} catch (NumberFormatException e) {
			timeControler.setText("");
		}

		// change traffic density
		try {
			String value = trafficDensity.getText();
			float newDensity = Float.parseFloat(value);
			if (newDensity <= 1.0 && newDensity >= 0.01 && enterPressed) {
				spawner.setTrafficDensity(newDensity);
				trafficDensity.setText("");
			}
		} catch (NumberFormatException e) {
			trafficDensity.setText("");
		}

		// change aggressive percentage
		try {
			String value = aggressiveDriver.getText();
			float newPercentage = Float.parseFloat(value);
			if (newPercentage + passivePers <= 1.0 && newPercentage >= 0 && enterPressed) {
				aggressivePers = newPercentage;
				aggressiveDriver.setText("");
			}
		} catch (NumberFormatException e) {
			aggressiveDriver.setText("");
		}

		// change passive percentage
		try {
			String value = passiveDriver.getText();
			float newPercentage = Float.parseFloat(value);
			if (newPercentage + aggressivePers <= 1.0 && newPercentage >= 0 && enterPressed) {
				passivePers = newPercentage;
				passiveDriver.setText("");
			}
		} catch (NumberFormatException e) {
			passiveDriver.setText("");
		}

		// change distance shown after obstacle
		try {// TODO -update the lanemarkings too!
			String value = pastObstacleDistance.getText();
			float newObstacleDist = Float.parseFloat(value);
			if (enterPressed) {
				if (newObstacleDist >= 50 && newObstacleDist <= 700
						&& newObstacleDist != (Game.TOTAL_SIMULATION_DISTANCE - Game.END_OF_LANE)) {
					Game.TOTAL_SIMULATION_DISTANCE = Game.END_OF_LANE + newObstacleDist;
					int i = 2;
					// TODO: I don't know why but big "jumps" only work coreect
					// after a second scaling -> everything gets scaled twice to
					// be safe
					do {
						game.setConstants(Game.SCALE);
						game.setObstacle(new Obstacle(Game.END_OF_LANE + 100));
						spawner.init(game);
						scaleToFit();
						i--;
					} while (i >= 1);
				}
				pastObstacleDistance.setText("");
			}

		} catch (NumberFormatException e) {
			pastObstacleDistance.setText("");
		}

		// End simulation via KEY_ESCAPE
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}
		// Toggle CarData vid KEY_D
		if (container.getInput().isKeyPressed(Input.KEY_D)) {
			carData = !carData;
		}

		// reset simulation
		if (container.getInput().isKeyPressed(Input.KEY_R)) {
			game.reset();
		}
		// reset visual params
		if (container.getInput().isKeyPressed(Input.KEY_E)) {
			game.resetParams();
		}

		// change Merge method: (reset before changing)
		if (container.getInput().isKeyPressed(Input.KEY_T)) {
			Game.classicMerge = !Game.classicMerge;
			game.getSigns().clear();
			spawner.init(game);
			game.reset();
		}

		// pause simulation
		if (container.getInput().isKeyPressed(Input.KEY_P)) {

			if (isPaused) {
				container.resume();
				isPaused = false;
			} else {
				container.pause();
				isPaused = true;
			}
		}

		// update average speed:
		if (AverageSpeedTimer >= 1000) {
			game.averageLaneSpeed = averageSpeed();
			AverageSpeedTimer = 0;
		} else
			AverageSpeedTimer += delta;

		// update average in-/output
		if (inOutTimer >= 10000) {
			outgoingTraffic = (double) (game.carsEndCounter - totalCountStart) / 10.0;
			totalCountStart = game.carsEndCounter;

			int totalCars = game.carsEndCounter + game.getCarsLeft().size() + game.getCarsRight().size();
			incomingTraffic = (double) (totalCars - totalCarsStart) / 10.0;
			totalCarsStart = totalCars;
			inOutTimer = 0;
		} else
			inOutTimer += delta;

		// update System Time
		if (systemTimer >= 1000.0) {
			game.time += systemTimer / 1000;
			systemTimer = systemTimer % 1000;
		} else {
			systemTimer += delta;
		}
		enterPressed = false;
	}

	/**
	 * updates Game.SCALE to the new TOTAL_SIMULATIUON_DISTANCE while keeping
	 * the old zoom
	 * 
	 * @throws SlickException
	 */
	public void scaleToFit() throws SlickException {
		Game.SCALE = 2 * Game.width * Game.VEHICLE_LENGTH_M
				/ (Game.TOTAL_SIMULATION_DISTANCE * Game.VEHICLE_LENGTH_PIX);// 0.09253012048192771;
		game.rescale((float) (Game.SCALE * scalingFactor));
	}

	private double[] averageSpeed() {
		double[] avSpd = new double[] { 0.0, 0.0 };
		double totalSpd = 0;
		int leftCars = 0;
		for (Car car : game.getCarsLeft()) {
			if (car.meter < Game.TOTAL_SIMULATION_DISTANCE) {
				totalSpd += car.getCurrentSpeed();
				leftCars++;
			}
		}
		if (leftCars != 0)
			avSpd[0] = totalSpd / leftCars;
		else
			avSpd[0] = 0;
		totalSpd = 0;
		leftCars = 0;
		for (Car car : game.getCarsRight()) {
			totalSpd += car.getCurrentSpeed();
		}
		avSpd[1] = totalSpd / game.getCarsRight().size();
		return avSpd;
	}

}
