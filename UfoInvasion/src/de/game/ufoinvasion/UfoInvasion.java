package de.game.ufoinvasion;

import java.util.*;
import org.newdawn.slick.*;

public class UfoInvasion extends BasicGame {

	private Image hintergrund;
	private Image auto;
	private Sound soundBlaster;
	private Sound soundExplosion;
	private Raumschiff raumschiff;
	private Effekte effekte;
	private Punkte punkte;
	private List<Schuss> schuesse = new ArrayList<Schuss>();
	private List<Ufo> ufos = new ArrayList<Ufo>();
	//private Ufo ufo;
	private SpielEnde gameOver;

	public UfoInvasion() {
		super("UFO Invasion");
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer container = new AppGameContainer(new UfoInvasion());
		container.setDisplayMode(1024, 768, false);
		container.setClearEachFrame(false);
		container.setMinimumLogicUpdateInterval(25);
		container.start();
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		hintergrund.draw();
		raumschiff.draw(g);
		effekte.draw(g);
		for (Schuss schuss : schuesse) {
			schuss.draw(g);
		}
		for (Ufo ufo : ufos) {
			ufo.draw(g);
		}
		
/*		ufo.draw(g);
		if (gameOver.isGameOver()) {
			gameOver.draw(g);
		}
		punkte.draw(g);*/
	}
	
	@Override
	public void init(GameContainer container) throws SlickException {
		hintergrund = new Image("res/Strasse.jpg");
		auto = new Image("res/auto.png");
		effekte = new Effekte();
		raumschiff = new Raumschiff(new Image("res/raumschiff.png"), container.getInput(), effekte.getRaketenRauchEmitter());
		Ufo ufo = new Ufo(300, 150, auto);
		ufos.add(ufo);
		Font fontPunkte = new AngelCodeFont("res/fonts/score_numer_font.fnt", new Image(
				"res/fonts/score_numer_font.png"));
		punkte = new Punkte(container.getWidth() - 180, 10, fontPunkte);
		soundExplosion = new Sound("res/sounds/explosion.wav");
		soundBlaster = new Sound("res/sounds/schuss.wav");
		Font fontGameOver = new AngelCodeFont("res/fonts/game_over_font.fnt", new Image("res/fonts/game_over_font.png"));
		gameOver = new SpielEnde(container.getHeight(), container.getWidth(), fontGameOver);
		new Music("res/sounds/music.mod").loop();
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		System.out.println(delta);
		Input input = container.getInput();
		if (!gameOver.isGameOver()) {
			raumschiff.update(delta);
			effekte.update(delta);

			int mausX = input.getMouseX();
			int mausY = input.getMouseY();

			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				mehrUfo(mausX,mausY);
				//neuerSchuss(mausX, mausY);
			}
			
			for(int i =0; i < schuesse.size() ; i++ ){
				Schuss schuss = schuesse.get(i);
				schuss.update(delta);
				for(int j=0; j < ufos.size() ; j++ ){
				Ufo ufo = ufos.get(j);
				if (ufo.pruefeKollsion(schuss)) {
					neuesUfo(container, schuss);
				}
		}
		}
		for(int j=0; j < ufos.size() ; j++ ){
			Ufo ufo = ufos.get(j);
			ufo.update(delta);}	
		
		}
		// Fenster mit ESC scließen
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}
/*		if (ufo.getY() > container.getHeight()) {
			container.setPaused(true);
			gameOver.setGameOver(true);
		}*/
	}

	private void neuerSchuss(int mausX, int mausY) {
		Schuss schuss = new Schuss(mausX, mausY -20, soundBlaster, effekte.getSchussEmitter());
		schuesse.add(schuss);
	}
	
	private void mehrUfo(int mausX, int mausY) {
		Ufo ufo = new Ufo(mausX, mausY,auto);
		ufos.add(ufo);
	}

	private void neuesUfo(GameContainer container, Schuss schuss) {
		schuesse.remove(schuss);
		schuss.verschwinde();
		for(int j=0; j < ufos.size() ; j++ ){
			Ufo ufo = ufos.get(j);
		effekte.ufoExpolsion(ufo.getX(), ufo.getY());
		Random random = new Random();
		ufo.setX(random.nextInt(container.getWidth()));
		ufo.setY(random.nextInt((int) (container.getHeight() * 0.7)));
		soundExplosion.play();
		punkte.punkte();}
	}

}
