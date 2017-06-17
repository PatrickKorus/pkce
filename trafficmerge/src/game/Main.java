package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class Main {


public static void main(String[] args) throws SlickException {
	AppGameContainer container = new AppGameContainer(new Game());
	container.setDisplayMode(Game.width, Game.height, false);
	container.setClearEachFrame(false);
	container.setMinimumLogicUpdateInterval(25);
	container.start();
}

}