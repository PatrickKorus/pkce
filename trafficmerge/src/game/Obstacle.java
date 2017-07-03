package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Obstacle extends GameObject {

	public Obstacle(double meter) throws SlickException {
		super(meter, false);

		setImage(Game.SCALE);
	}

	public void setImage(float scale) throws SlickException {
		this.image = new Image("res/obstacle/obstacle.png");
		this.image = this.image.getScaledCopy((int) Math.round(image.getWidth()*Game.SCALE*3.61065), image.getHeight());
	
	}
	
	@Override
	public void draw(Graphics g) {
		image.drawCentered(x, y);
	}
	
	@Override
	public void rescale(float scale) throws SlickException {
		setImage(scale);
	}

}
