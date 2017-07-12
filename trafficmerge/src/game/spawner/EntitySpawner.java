package game.spawner;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import game.Game;

public interface EntitySpawner {
	
	void init(Game game) throws SlickException;
	
	void spawn(int delta, Input input, Game game) throws SlickException;
	
	void setTrafficDensity ( double Density);
	
	double getTrafficDensity();
}
