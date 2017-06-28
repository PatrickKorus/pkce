package sign;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Delineator extends Sign {

	public Delineator(double meter) throws SlickException {
		super(meter, Sign_Type.DELINEATOR);
		value = meter;
		setImage(new Image("res/signs/delineator.png"));
	}

}
