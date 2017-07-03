package sign;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class SpeedLimitSign extends Sign {

	public SpeedLimitSign(double meter, Sign_Type type) throws SlickException {
		super(meter, type);
		switch (SIGNTYPE) {
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
			System.err.println(type + " is not a valid Speed Limit Sign Type!");
		}
	}
}
