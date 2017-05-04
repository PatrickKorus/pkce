package de.tudarmstadt.informatik.fop.dropofwater.ui;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import eea.engine.entity.StateBasedEntityManager;

/**
 * @author Timo Bähr
 *
 *         Diese Klasse startet das Spiel "Drop of Water". Es enthaelt zwei
 *         State's für das Menue und das eigentliche Spiel.
 */
public class Launch extends StateBasedGame {

  // Jeder State wird durch einen Integer-Wert gekennzeichnet
  public static final int MAINMENU_STATE = 0;
  public static final int GAMEPLAY_STATE = 1;

  public Launch() {
    super("Drop of Water"); // Name des Spiels
  }

  @Override
  public void initStatesList(GameContainer arg0) throws SlickException {

    // Fuege dem StateBasedGame die States hinzu
    // (der zuerst hinzugefuegte State wird als erster State gestartet)
    addState(new MainMenuState(MAINMENU_STATE));
    addState(new GameplayState(GAMEPLAY_STATE));

    // Fuege dem StateBasedEntityManager die States hinzu
    StateBasedEntityManager.getInstance().addState(MAINMENU_STATE);
    StateBasedEntityManager.getInstance().addState(GAMEPLAY_STATE);

  }

  public static void main(String[] args) throws SlickException {
    // Setze den Bibliothekenpfad abhaengig vom Betriebssystem
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
      System.setProperty("org.lwjgl.librarypath",
          System.getProperty("user.dir") + "/lwjgl-2.9.3/native/windows");
    } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
      System.setProperty("org.lwjgl.librarypath",
          System.getProperty("user.dir") + "/lwjgl-2.9.3/native/macosx");
    } else {
      System.setProperty("org.lwjgl.librarypath",
          System.getProperty("user.dir") + "/lwjgl-2.9.3/native/"
              + System.getProperty("os.name").toLowerCase());
    }

    // Setze dieses StateBasedGame in einen App Container (oder Fenster)
    AppGameContainer app = new AppGameContainer(new Launch());

    // Lege die Einstellungen des Fensters fest und starte das Fenster
    // (nicht aber im Vollbildmodus)
    app.setDisplayMode(800, 600, false);
    app.setTargetFrameRate(120); // gewünschte Framerate
    app.start();

  }
}