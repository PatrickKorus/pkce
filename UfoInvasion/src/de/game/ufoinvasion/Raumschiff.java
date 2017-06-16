package de.game.ufoinvasion;

import org.newdawn.slick.*;
import org.newdawn.slick.particles.ConfigurableEmitter;

public class Raumschiff extends SpielObjekt {

	private ConfigurableEmitter emitter;
	private Input input;

	public Raumschiff(Image image, Input input, ConfigurableEmitter emitter) {
		super(image);
		this.input = input;
		this.emitter = emitter;
	}

	@Override
	public void update(int delta) {
		x = input.getMouseX();
		y = input.getMouseY();
		emitter.setPosition(x, y + 45, false);
	}

	@Override
	public void draw(Graphics g) {
		image.drawCentered(x, y);
	}

}
