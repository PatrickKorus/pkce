package de.game.ufoinvasion;

import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class Punkte extends SpielObjekt {

	private Font font;
	private int punkte;

	public Punkte(int x, int y, Font font) {
		super(x, y);
		this.font = font;
	}

	@Override
	public void draw(Graphics g) {
		g.setFont(font);
		String punkteMitNullen = String.format("%04d", punkte);
		g.drawString(punkteMitNullen, x, y);
	}

	public void punkte() {
		punkte++;
	}
}
