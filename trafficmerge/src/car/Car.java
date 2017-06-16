package car;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import game.GameObject;

public abstract class Car extends GameObject {

	boolean isOnGoalLine;
//	Image ViewField;
	double currentSpeed;
	double currentAcc;
	Image basicImage;
	Image indicateImage;
	Image breakImage;
	boolean isIndicating;
	boolean isBreaking;
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param basicImage
	 * @param indicateImage
	 * @param breakImage
	 * @param isOnGoalLine
	 * @param initSpeed
	 */
	public Car(int x, int y, Image basicImage, Image indicateImage, Image breakImage, boolean isOnGoalLine, double initSpeed) {
		super(x, y, basicImage);
		this.isOnGoalLine = isOnGoalLine;
		this.currentSpeed = initSpeed;
		this.currentAcc = 0.0;
		this.basicImage = basicImage;
		this.breakImage = breakImage;
		this.indicateImage = indicateImage;
	}
	
	public Car(int x, int y, Image basicImage, boolean isOnGoalLine, double initSpeed) {
		super(x, y, basicImage);
		this.isOnGoalLine = isOnGoalLine;
		this.currentSpeed = initSpeed;
		this.currentAcc = 0.0;
		this.basicImage = basicImage;
		this.breakImage = basicImage;
		this.indicateImage = basicImage;
	}

	@Override
	public void draw(Graphics g) {
		image.drawCentered(x, y);
	}

	@Override
	public void update(int delta) {
		super.update(delta);
	}
	
	
	
	

	
	
}
