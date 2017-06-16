package game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Sign extends GameObject{

	
	Sign_Type SIGNTYPE;
	public Sign(int x, int y, Sign_Type type) throws SlickException {
		super(x, y);
		SIGNTYPE = type;
		switch(SIGNTYPE){
		case LINE_END_0:
			setImage(new Image("res/Spur_endet.png"));
			break;
		case LINE_END_200:
			break;
		case SPD_60:
			setImage(new Image("res/Speed_60.png"));
			break;
		case SPD_80:
			setImage(new Image("res/Speed_80.png"));
			break;
		case SPD_100:
			setImage(new Image("res/Speed_100.png"));
			break;

		default:
			break;
		}
	}
	
	@Override
	public void draw(Graphics g) {
		image.drawCentered(x, y);
		
	}

}
