package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;

import car.Car;
import game.spawner.EntitySpawner;

public class GameUI {
	public boolean carData = false;
	
	private TextField scaler;
	private TextField timeControler;
	private TextField trafficDensity;
	private EntitySpawner spawner;
	private Game game;
	private GameContainer container;
	private int time = 0;
	//for average in-/output:
	private double outgoingTraffic = 0;
	private double incomingTraffic = 0;	
	private int time2 = 0;
	private int totalCountStart = 0;
	private int totalCarsStart = 0;
	
	
	
	public GameUI(Game game , GameContainer container, EntitySpawner spawn){
		this.spawner = spawn;
		this.game = game;
		this.container = container;
		scaler = new TextField(container, container.getDefaultFont(), 50, 50, 100, 20);
		timeControler = new TextField(container, container.getDefaultFont(), 50, 100, 100, 20);
		trafficDensity = new TextField(container, container.getDefaultFont(), 50, 150, 100, 20);
	}
	
	public void render(GameContainer container, Graphics g){
		g.drawString("Skalierung: " + Math.round(Game.SCALE*100)/100.0, scaler.getX(), scaler.getY()-20);
		scaler.render(container, g);
		g.drawString("Zeitraffer: " + Math.round(Game.timeFactor*100)/100.0, timeControler.getX(), timeControler.getY()-20);
		timeControler.render(container, g);
		g.drawString("Verkehrsdichte: " + Math.round(spawner.getTrafficDensity()*100)/100.0, trafficDensity.getX(), trafficDensity.getY()-20);
		trafficDensity.render(container, g);
		g.drawString("D -> Zeige Autoinformationen",50, 175);
		
		//Data:
		//Counting cars:
		g.drawString("Autos:" + game.carsEndCounter, container.getWidth()-350, 25);
		
		//Average lane speed:
		g.drawString("Durchschnittsgeschwindigkeit:", container.getWidth()-350 , 50);
		g.drawString("~>Linke Bahn: " + Math.round(game.averageLaneSpeed[0]*100)/100.0 + " km/h", container.getWidth()-300 , 70);
		g.drawString("~>Rechte Bahn:" + Math.round(game.averageLaneSpeed[1]*100)/100.0 + " km/h", container.getWidth()-300 , 90);

		//average In-/Output
		g.drawString("Eingangsverkehrsdichte: " + Math.round(incomingTraffic*100)/100.0 + " Autos/s", container.getWidth()-350 , 115);
		g.drawString("Ausgangsverkehrsdichte: " + Math.round(outgoingTraffic*100)/100.0 + " Autos/s", container.getWidth()-350 , 140);

		
	}
	
	public void draw(){
		
	}
	
	public void update(int delta) throws SlickException{
		// TODO: In einen Parser auskoppeln?
		// rescaling
		try {
			String value = scaler.getText();
			float newscale = Float.parseFloat(value);
			if (newscale > 0.01)
				game.rescale(newscale);
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
			//TODO: Handle exceptions
		}

		
		// End simulation via KEY_ESCAPE
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}
		// Toggle CarData vid KEY_D
		if (container.getInput().isKeyPressed(Input.KEY_D)) {
			carData = !carData;
		}
		
		//update average speed:
		if(time >= 1000){
			game.averageLaneSpeed = averageSpeed();
			time = 0;
		}else
			time += delta;
		
		//update average in-/output
		if(time2 >= 10000){
			outgoingTraffic = (double)(game.carsEndCounter - totalCountStart) / 10.0;
			totalCountStart = game.carsEndCounter;
			
			int totalCars = game.carsEndCounter + game.getCarsLeft().size() + game.getCarsRight().size();
			incomingTraffic = (double)(totalCars - totalCarsStart) / 10.0;
			totalCarsStart = totalCars;
			time2 = 0;
		}
		else
			time2 += delta;
		
		//update average input
	}
	
	private double[] averageSpeed(){
		double[] avSpd = new double[2];
		double totalSpd = 0;
		for(Car car : game.getCarsLeft()){
			totalSpd += car.getCurrentSpeed();
		}
		avSpd[0] = totalSpd / game.getCarsLeft().size();
		totalSpd = 0;
		for(Car car : game.getCarsRight()){
			totalSpd += car.getCurrentSpeed();
		}
		avSpd[1] = totalSpd / game.getCarsRight().size();
		return avSpd;
	}
}

