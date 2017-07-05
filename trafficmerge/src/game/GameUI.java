package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;

import game.spawner.EntitySpawner;

public class GameUI {
	
	private TextField scaler;
	private TextField timeControler;
	private TextField trafficDensity;
	private EntitySpawner spawner;
	GameContainer container;
	Game game;
	
	public GameUI(Game game ,GameContainer container, EntitySpawner spawn){
		this.spawner = spawn;
		this.container = container;
		this.game = game;
		scaler = new TextField(container, container.getDefaultFont(), 50, 50, 100, 20);
		timeControler = new TextField(container, container.getDefaultFont(), 50, 100, 100, 20);
		trafficDensity = new TextField(container, container.getDefaultFont(), 50, 150, 100, 20);
	}
	
	public void render(GameContainer container, Graphics g){
		g.drawString("Skalierung:", scaler.getX(), scaler.getY()-20);
		scaler.render(container, g);
		g.drawString("Zeitraffer:", timeControler.getX(), timeControler.getY()-20);
		timeControler.render(container, g);
		g.drawString("Verkehrsdichte:", trafficDensity.getX(), trafficDensity.getY()-20);
		trafficDensity.render(container, g);
		g.drawString("Autos:" + game.carsEndCounter, 50, 200);

	}
	
	public void draw(){
		
	}
	
	public void update() throws SlickException{
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

		}


	}
}

