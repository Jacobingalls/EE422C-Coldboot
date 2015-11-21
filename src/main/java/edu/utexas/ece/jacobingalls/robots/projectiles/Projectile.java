package edu.utexas.ece.jacobingalls.robots.projectiles;

import edu.utexas.ece.jacobingalls.App;
import edu.utexas.ece.jacobingalls.robots.MovingThing;
import edu.utexas.ece.jacobingalls.Team;
import edu.utexas.ece.jacobingalls.robots.particles.Particle;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.Random;


public class Projectile extends MovingThing {
	public Projectile(Team team) {
		super(team);
		setDesiredVelocityX(1000);
		setDesiredVelocityY(1000);
	}

	@Override
	public double getWidth() {
		return 2;
	}

	@Override
	public double getHeight() {return 2; }

	@Override
	public void render(GraphicsContext gc) {
		gc.setFill(team.getTeamColor());
		gc.fillRect(getXViewport(), getYViewport(), getWidth(), getHeight());

//		gc.setStroke(teamColor);
//		gc.strokeRect(getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public void tick(long time_elapsed) {
		super.tick(time_elapsed);

		if(time_elapsed > 0)
			setHealth(getHealth() -20/time_elapsed);
	}

	@Override
	public boolean isColliding(double x, double y) {
		return  (x >= getX() && x <= (getX() + getWidth())) &&
				(y >= getY() && y <= (getY() + getHeight()));
	}

	@Override
	protected boolean clicked(double x, double y) {
		return false;
	}

	public double getDamage(){
		return new Point2D(getCurrentVelocityX(), getCurrentVelocityY()).magnitude() / 400;
	}

	@Override
	protected void died(){
		Random r = new Random();
		if(r.nextBoolean()) {

			App.getGame().getThingsWaiting().add(new Particle(getTeam())
							.setCurrentVelocityX(-getCurrentVelocityX() * 10 * (r.nextDouble()  - .5))
							.setCurrentVelocityY(-getCurrentVelocityY() * 10 * (r.nextDouble()  - .5))
							.setDesiredVelocityX(0)
							.setDesiredVelocityY(0)
							.setAcceleration(.01)
							.setXCenter(getXCenter())
							.setYCenter(getYCenter())
			);
		}
	}
}
