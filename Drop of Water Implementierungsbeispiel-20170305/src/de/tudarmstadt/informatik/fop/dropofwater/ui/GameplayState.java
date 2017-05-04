package de.tudarmstadt.informatik.fop.dropofwater.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import eea.engine.action.Action;
import eea.engine.action.basicactions.ChangeStateAction;
import eea.engine.action.basicactions.DestroyEntityAction;
import eea.engine.action.basicactions.MoveDownAction;
import eea.engine.component.Component;
import eea.engine.component.render.ImageRenderComponent;
import eea.engine.entity.DestructibleImageEntity;
import eea.engine.entity.Entity;
import eea.engine.entity.StateBasedEntityManager;
import eea.engine.event.Event;
import eea.engine.event.basicevents.CollisionEvent;
import eea.engine.event.basicevents.KeyPressedEvent;
import eea.engine.event.basicevents.LeavingScreenEvent;
import eea.engine.event.basicevents.LoopEvent;
import eea.engine.event.basicevents.MouseClickedEvent;
import eea.engine.interfaces.IDestructible;

/**
 * @author Timo Bähr
 *
 *         Diese Klasse repraesentiert das Spielfenster, indem ein Wassertropfen
 *         erscheint und nach unten faellt.
 */
public class GameplayState extends BasicGameState {

  protected int                     stateID;      // Identifier dieses
                                                   // BasicGameState
  protected StateBasedEntityManager entityManager; // zugehoeriger entityManager

  GameplayState(int sid) {
    stateID = sid;
    entityManager = StateBasedEntityManager.getInstance();
  }

  /**
   * Wird vor dem (erstmaligen) Starten dieses States ausgefuehrt
   */
  @Override
  public void init(GameContainer container, StateBasedGame game)
      throws SlickException {

    // Hintergrund laden
    Entity background = new Entity("background"); // Entitaet fuer Hintergrund
    background.setPosition(new Vector2f(400, 300)); // Startposition des
                                                    // Hintergrunds
    background.addComponent(new ImageRenderComponent(new Image(
        "/assets/background.png"))); // Bildkomponente

    // Hintergrund-Entitaet an StateBasedEntityManager uebergeben
    StateBasedEntityManager.getInstance().addEntity(stateID, background);

    // Bei Drücken der ESC-Taste zurueck ins Hauptmenue wechseln
    Entity escListener = new Entity("ESC_Listener");
    KeyPressedEvent escPressed = new KeyPressedEvent(Input.KEY_ESCAPE);
    escPressed.addAction(new ChangeStateAction(Launch.MAINMENU_STATE));
    escListener.addComponent(escPressed);
    entityManager.addEntity(stateID, escListener);

    // "Auffangbecken"
    // erstelle ein Bild der Breite 500 und der Hoehe 200
    BufferedImage image = new BufferedImage(500, 200,
        BufferedImage.TYPE_INT_ARGB);
    // mit Graphics2D laesst sich das Bild bemalen
    Graphics2D graphic = image.createGraphics();
    // die folgende Zeile bewirkt, dass sich auch wieder "ausradieren" laesst
    graphic.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
    // bemale das vollstaendige Bild weiss
    graphic.setColor(new Color(255, 255, 255));
    graphic.fillRect(0, 0, 500, 200);
    // radiere in der Mitte wieder ein Rechteck aus, indem die Farbe komplett
    // transparent ist
    graphic.setColor(new Color(255, 255, 255, 0));
    graphic.fillRect(100, 50, 300, 100);

    // erstelle eine DestructibleImageEntity mit dem gerade gemalten Bild
    // als Image, das durch das Zerstoerungs-Pattern destruction.png zerstoert
    // werden kann
    DestructibleImageEntity obstacle = new DestructibleImageEntity(
        "obstacle", image, "assets/destruction.png", false);
    obstacle.setPosition(new Vector2f(game.getContainer().getWidth() / 2,
        game.getContainer().getHeight() - 100));

    entityManager.addEntity(stateID, obstacle);    
    
    
    // Bei Mausklick soll Wassertropfen erscheinen
    Entity mouseClickedListener = new Entity("Mouse_Clicked_Listener");
    MouseClickedEvent mouseClicked = new MouseClickedEvent();

    mouseClicked.addAction(new Action() {
      @Override
      public void update(GameContainer gc, StateBasedGame sb, int delta,
          Component event) {
        // Wassertropfen wird erzeugt
        Entity drop = new Entity("drop of water");
        drop.setPosition(new Vector2f(gc.getInput().getMouseX(), 
            gc.getInput().getMouseY()));

        try {
          // Bild laden und zuweisen
          drop.addComponent(new ImageRenderComponent(new Image(
              "assets/drop.png")));
        } catch (SlickException e) {
          System.err.println("Cannot find file assets/drop.png!");
          e.printStackTrace();
        }

        // Wassertropfen faellt nach unten
        LoopEvent loop = new LoopEvent();
        loop.addAction(new MoveDownAction(0.5f));
        drop.addComponent(loop);

        // Wenn der Bildschirm verlassen wird, dann ...
        LeavingScreenEvent lse = new LeavingScreenEvent();

        // ... zerstoere den Wassertropfen
        lse.addAction(new DestroyEntityAction());
        // ... und wechsle ins Hauptmenue
        lse.addAction(new ChangeStateAction(Launch.MAINMENU_STATE));

        drop.addComponent(lse);

        Event collisionEvent = new CollisionEvent();
        collisionEvent.addAction(new Action() {
          @Override
          public void update(GameContainer gc, StateBasedGame sb, int delta, Component event) {

            // hole die Entity, mit der kollidiert wurde
            CollisionEvent collider = (CollisionEvent) event;
            Entity entity = collider.getCollidedEntity();

            // wenn diese durch ein Pattern zerstört werden kann, dann caste
            // zu IDestructible, ansonsten passiert bei der Kollision nichts
            IDestructible destructible = null;
            if (entity instanceof IDestructible) {
              destructible = (IDestructible) entity;
            } else {
              return;
            }

            // zerstöre die Entität (dabei wird das der Entität zugewiesene Zerstörungs-Pattern benutzt)
            destructible.impactAt(event.getOwnerEntity().getPosition());
          }
        });
        collisionEvent.addAction(new DestroyEntityAction());
        drop.addComponent(collisionEvent);

        
        entityManager.addEntity(stateID, drop);
      }
    });
    mouseClickedListener.addComponent(mouseClicked);

    entityManager.addEntity(stateID, mouseClickedListener);

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
    // StatedBasedEntityManager soll alle Entities rendern
    entityManager.renderEntities(container, game, g);
  }

  @Override
  public int getID() {
    return stateID;
  }
}
