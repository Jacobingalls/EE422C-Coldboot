package edu.utexas.ece.jacobingalls.robots.particles;

import edu.utexas.ece.jacobingalls.Team;
import edu.utexas.ece.jacobingalls.robots.MovingThing;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by jacobingalls on 11/20/15.
 */
public class Particle extends MovingThing {

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
		gc.setFill(team.getTeamColor().deriveColor(0,getHealthPercentage(), getHealthPercentage(), getHealthPercentage()));
		gc.fillRect(getXViewport(), getYViewport(), getWidth(), getHeight());
	}

	@Override
	public void tick(long time_elapsed){
		super.tick(time_elapsed);
		setHealth(getHealth() - (getMaxHeath() / 1000.0) * time_elapsed);
	}

	@Override
	public boolean isColliding(double x, double y) {
		return false;
	}

	@Override
	protected boolean clicked(double x, double y) {
		return false;
	}
}
