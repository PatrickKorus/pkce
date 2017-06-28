package sign;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class LaneEndsSign extends Sign {

	public LaneEndsSign(double meter, Sign_Type type) throws SlickException {
		super(meter, type);
		switch (SIGNTYPE) {
		case LINE_END_0:
			setImage(new Image("res/signs/laneends.png"));
			value = 0;
			break;
		case LINE_END_200:
			setImage(new Image("res/signs/laneends.png"));
			value = 200;
			break;
		default:
			System.err.println(type + " is not a valid Road Ends Sign!");
		}
	}
}
