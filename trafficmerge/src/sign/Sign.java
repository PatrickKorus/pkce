package sign;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import game.GameObject;

public class Sign extends GameObject {

	private Sign_Type SIGNTYPE;
	private double value;

	public Sign(double meter, Sign_Type type) throws SlickException {
		super(meter, false);
		SIGNTYPE = type;
		switch (SIGNTYPE) {
		case LINE_END_0:
			setImage(new Image("res/signs/laneends.png"));
			value = 0;
			break;
		case LINE_END_200:
			value = 200;
			break;
		case SPD_60:
			value = 60;
			setImage(new Image("res/signs/60.png"));
			break;

		case SPD_80:
			value = 80;
			setImage(new Image("res/signs/80.png"));
			break;
		case SPD_100:
			value = 100;
			setImage(new Image("res/signs/100.png"));
			break;

		default:
			break;
		}
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
