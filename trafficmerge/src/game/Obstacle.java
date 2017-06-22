package game;

import org.newdawn.slick.Graphics;

public class Obstacle extends GameObject {

	public Obstacle(double meter) {
		super(meter, false);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(Graphics g) {
		image.drawCentered(x, y);
	}

}
