package sign;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import game.GameObject;

public class Sign extends GameObject {

	protected Sign_Type SIGNTYPE;
	protected double value;

	public Sign(double meter, Sign_Type type) throws SlickException {
		super(meter, false);
		SIGNTYPE = type;
	}

	@Override
	public void draw(Graphics g) {
		image.drawCentered(x, y);
	}

	public double getValue() {
		return this.value;
	}

	public Sign_Type getSingType() {
		return this.SIGNTYPE;
	}

}
