package de.tudarmstadt.informatik.fop.dropofwater.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import eea.engine.action.Action;
import eea.engine.action.basicactions.ChangeStateInitAction;
import eea.engine.action.basicactions.QuitAction;
import eea.engine.component.render.ImageRenderComponent;
import eea.engine.entity.Entity;
import eea.engine.entity.StateBasedEntityManager;
import eea.engine.event.ANDEvent;
import eea.engine.event.basicevents.MouseClickedEvent;
import eea.engine.event.basicevents.MouseEnteredEvent;

/**
 * @author Timo Bähr
 *
 *         Diese Klasse repraesentiert das Menuefenster, indem ein neues Spiel
 *         gestartet werden kann und das gesamte Spiel beendet werden kann.
 */
public class MainMenuState extends BasicGameState {

  private int                     stateID;             // Identifier von diesem
                                                        // BasicGameState
  private StateBasedEntityManager entityManager;       // zugehoeriger
                                                        // entityManager

  private final int               distance       = 100;
  private final int               start_Position = 180;

  MainMenuState(int sid) {
    stateID = sid;
    entityManager = StateBasedEntityManager.getInstance();
  }

  /**
   * Wird vor dem (erstmaligen) Starten dieses State's ausgefuehrt
   */
  @Override
  public void init(GameContainer container, StateBasedGame game)
      throws SlickException {

    // Entitaet fuzer Hintergrund
    Entity background = new Entity("menu");

    // Startposition des Hintergrunds
    background.setPosition(new Vector2f(400, 300));

    // Bildkomponente zur Entitaet hinzufuegen
    background.addComponent(new ImageRenderComponent(new Image(
        "/assets/menu.png")));

    // Hintergrund-Entitaet an StateBasedEntityManager uebergeben
    entityManager.addEntity(stateID, background);

    /* Neues Spiel starten-Entitaet */
    String newGame = "Neues Spiel starten";
    Entity newGameEntity = new Entity(newGame);

    // Setze Position und Bildkomponente
    newGameEntity.setPosition(new Vector2f(218, 190));
    newGameEntity.setScale(0.28f);
    newGameEntity.addComponent(new ImageRenderComponent(new Image(
        "assets/entry.png")));

    // Erstelle das Ausloese-Event und die zugehoerige Action
    ANDEvent mainEvents = new ANDEvent(
        new MouseEnteredEvent(), // Maus muss "drauf" sein
        new MouseClickedEvent()); // UND geklickt werden

    Action newGameAction = new ChangeStateInitAction(
        Launch.GAMEPLAY_STATE);
    mainEvents.addAction(newGameAction);
    newGameEntity.addComponent(mainEvents);

    // Fuege die Entity zum StateBasedEntityManager hinzu
    entityManager.addEntity(this.stateID, newGameEntity);

    /* Beenden-Entitaet */
    Entity quitEntity = new Entity("Beenden");

    // Setze Position und Bildkomponente
    quitEntity.setPosition(new Vector2f(218, 290));
    quitEntity.setScale(0.28f);
    quitEntity.addComponent(new ImageRenderComponent(new Image(
        "assets/entry.png")));

    // Erstelle das Ausloese-Event und die zugehoerige Action
    ANDEvent mainEventsQuit = new ANDEvent(
        new MouseEnteredEvent(), // Maus muss "drauf" sein
        new MouseClickedEvent()); // UND geklickt werden

    Action quitAction = new QuitAction();
    mainEventsQuit.addAction(quitAction);
    quitEntity.addComponent(mainEventsQuit);

    // Fuege die Entity zum StateBasedEntityManager hinzu
    entityManager.addEntity(this.stateID, quitEntity);
  }

  /**
   * Wird vor dem Frame ausgefuehrt
   */
  @Override
  public void update(GameContainer container, StateBasedGame game, int delta)
      throws SlickException {
    // StatedBasedEntityManager soll alle Entities aktualisieren
    entityManager.updateEntities(container, game, delta);
  }

  /**
   * Wird mit dem Frame ausgefuehrt
   */
  @Override
  public void render(GameContainer container, StateBasedGame game, Graphics g)
      throws SlickException {
    entityManager.renderEntities(container, game, g);

    // Hier eigentlich “Overkill”, aber bei mehreren Elementen hilfreich
    int counter = 0;

    g.drawString("Neues Spiel", 110, start_Position + counter * distance);
    counter++;
    g.drawString("Beenden", 110, start_Position + counter * distance);
    counter++;
  }

  @Override
  public int getID() {
    return stateID;
  }

}
