package de.game.ufoinvasion;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

public class Ufo extends SpielObjekt {

	private Shape kollisionsFlaeche;
	private double geschwindigkeit = 2;
	private double beschleunigung = 0.001;

	public Ufo(int x, int y, Image image) {
		super(x, y, image);
		kollisionsFlaeche = new Ellipse(x, y, 60, 30);
	}

	@Override
	public void update(int delta) {
		geschwindigkeit += beschleunigung;
		x += geschwindigkeit;
		if (x >= 1000) {
			x = 0;
		}
		kollisionsFlaeche.setCenterX(x);
		kollisionsFlaeche.setCenterY(y);
	}

	@Override
	public void draw(Graphics g) {
		image.drawCentered(x, y);
//		Color.red.a = 0.4f;
//		g.setColor(Color.red);
//		g.fill(kollisionsFlaeche);
	}

	public boolean pruefeKollsion(SpielObjekt spielObjekt) {
		return kollisionsFlaeche.contains(spielObjekt.getX(), spielObjekt.getY());
	}
}
