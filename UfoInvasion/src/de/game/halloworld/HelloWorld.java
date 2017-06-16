/*
 * @version 0.0 06.01.2011
 * @author Tobse F
 */
package de.game.halloworld;

// Import der nötigen Slick Klassen
import org.newdawn.slick.*;

/**
 * Ein einfaches HelloWorld Beispiel. Zeigt ein Fenster in dem ein sich rotierendes Bild gezeichnet
 * wird und der Schriftzug "HelloWorld" steht. Mit ESC kann das Fenster wieder geschlossen werden.
 */
public class HelloWorld extends BasicGame {

	private Image image;

	public HelloWorld() {
		// Setzen des Fenstertitels
		super("Hello World");
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer container = new AppGameContainer(new HelloWorld());
		// Fenster mit 1024 x 786 im Fenstermodus (false)
		container.setDisplayMode(800, 600, false);
		container.start();
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		// Bild logo.png aus dem Verzeichnis restest laden
		image = new Image("testdata/logo.png");
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		// Text und Bild zeichnen
		g.drawString("Hello World", 100, 100);
		g.drawImage(image, 300, 300);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		// Animation: Bild rotieren
		image.rotate(0.05f);
		// Tastenabfrage: Mit Esc-Taste das Spiel beenden
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}
	}
}
