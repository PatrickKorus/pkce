package car;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import game.GameObject;

public abstract class Car extends GameObject {

	boolean isOnGoalLine;
//	Image ViewField;
	double currentSpeed;
	double currentAcc;
	double CarAcc;        //Autospezifische Beschleunigung
	Image basicImage;
	Image indicateImage;
	Image breakImage;
	boolean isIndicating;
	boolean isBreaking;
	
	
	
	public Car(int x, int y, Image basicImage, Image indicateImage, Image breakImage, boolean isOnGoalLine, double initSpeed, double CarAcc) {
		super(x, y, basicImage);
		this.isOnGoalLine = isOnGoalLine;
		this.currentSpeed = initSpeed;
		this.currentAcc = 0.0;
		this.basicImage = basicImage;
		this.breakImage = breakImage;
		this.indicateImage = indicateImage;
		this.CarAcc = CarAcc;
	}
	
	public Car(int x, int y, Image basicImage, boolean isOnGoalLine, double initSpeed, double CarAcc) {
		super(x, y, basicImage);
		this.isOnGoalLine = isOnGoalLine;
		this.currentSpeed = initSpeed;
		this.currentAcc = 0.0;
		this.basicImage = basicImage;
		this.breakImage = basicImage;
		this.indicateImage = basicImage;
		this.CarAcc = CarAcc;
	}

	@Override
	public void draw(Graphics g) {
		image.drawCentered(x, y);
	}

	@Override
	public void update(int delta) {
		super.update(delta);
		currentAcc = accelerate(CarAcc);
	}
	
	public double accelerate(double CarAcc){
		if (!isBreaking){ //&& currentSpeed < Speedlimit){   //spaeter: Speedlimit durch Sign gegeben
			return 0.5*CarAcc;
		}
		if (isBreaking){ //|| currentSpeed > Speedlimit){
			return -CarAcc;}
		return 0.0;
	}
	
	

	
	
}
