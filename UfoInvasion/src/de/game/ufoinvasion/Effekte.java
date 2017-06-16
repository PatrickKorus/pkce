package de.game.ufoinvasion;

import java.io.IOException;
import org.newdawn.slick.*;
import org.newdawn.slick.particles.*;

public class Effekte extends SpielObjekt {

	private ParticleSystem particleSystem;
	private ConfigurableEmitter raketenRauch;
	private ConfigurableEmitter ufoExplosion;
	private ConfigurableEmitter schussPartikel;

	public Effekte() throws SlickException {
		try {
			particleSystem = ParticleIO.loadConfiguredSystem("res/particles/leeres_system.xml");
			schussPartikel = ParticleIO.loadEmitter("res/particles/schuss_schweif_emitter.xml");
			raketenRauch = ParticleIO.loadEmitter("res/particles/raketen_rauch.xml");
			particleSystem.addEmitter(raketenRauch);
			ufoExplosion = ParticleIO.loadEmitter("res/particles/ufo_explosion.xml");
		} catch (IOException e) {
			throw new SlickException("Partikel System konnte nicht geladen werden", e);
		}
	}

	@Override
	public void update(int delta) {
		particleSystem.update(delta);
	}

	@Override
	public void draw(Graphics g) {
		particleSystem.render();
	}

	public ConfigurableEmitter getSchussEmitter() {
		ConfigurableEmitter emitter = schussPartikel.duplicate();
		particleSystem.addEmitter(emitter);
		return emitter;
	}

	public ConfigurableEmitter getRaketenRauchEmitter() {
		return raketenRauch;
	}

	public void ufoExpolsion(int x, int y) {
		ConfigurableEmitter explosion = ufoExplosion.duplicate();
		explosion.setPosition(x, y);
		particleSystem.addEmitter(explosion);
	}
}
