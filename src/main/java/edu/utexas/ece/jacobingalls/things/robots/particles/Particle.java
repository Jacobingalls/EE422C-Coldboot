package edu.utexas.ece.jacobingalls.things.robots.particles;

import edu.utexas.ece.jacobingalls.player.Team;
import edu.utexas.ece.jacobingalls.things.MovingThing;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Created by jacobingalls on 11/20/15.
 */
public class Particle extends MovingThing {

	private Color sourceColor = Color.WHITE;
	private  static Random r = new Random();

	public Particle(Team team) {
		super(team);
	}

	@Override
	public double getWidth() {
		return 1;
	}

	@Override
	public double getHeight() {
		return 1;
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.setFill(sourceColor.deriveColor(10*r.nextDouble(),getHealthPercentage(), getHealthPercentage()+.5, getHealthPercentage()));
		gc.fillRect(getXViewport(), getYViewport(), getWidth(), getHeight());
	}

	@Override
	public void tick(long time_elapsed){
		super.tick(time_elapsed);
		setCurrentVelocityX((r.nextDouble()/5+.8) * getCurrentVelocityX());
		setCurrentVelocityY((r.nextDouble()/5+.8) * getCurrentVelocityY());
		setHealth(getHealth() - (getMaxHeath() / 200.0) * time_elapsed);
	}

	@Override
	public boolean isColliding(double x, double y) {
		return false;
	}

	@Override
	protected boolean clicked(double x, double y) {
		return false;
	}

	public Color getSourceColor() {
		return sourceColor;
	}

	public Particle setSourceColor(Color sourceColor) {
		this.sourceColor = sourceColor;
		return this;
	}
}
