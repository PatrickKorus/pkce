package game;

import java.util.*;
import org.newdawn.slick.*;

import game.Game;

public class Main {


public static void main(String[] args) throws SlickException {
	AppGameContainer container = new AppGameContainer(new Game());
	container.setDisplayMode(1024, 768, false);
	container.setClearEachFrame(false);
	container.setMinimumLogicUpdateInterval(25);
	container.start();
}

}