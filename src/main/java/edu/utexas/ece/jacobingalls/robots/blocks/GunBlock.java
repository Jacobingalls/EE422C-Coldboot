package edu.utexas.ece.jacobingalls.robots.blocks;

import edu.utexas.ece.jacobingalls.App;
import edu.utexas.ece.jacobingalls.Team;
import edu.utexas.ece.jacobingalls.robots.projectiles.Projectile;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


/**
 * Created by jacobingalls on 11/12/15.
 */
public class GunBlock extends Block {
	double seconds = 0;
	Color innerRingColor = Color.TRANSPARENT;
	Color gunColor = Color.TRANSPARENT;
	double gunAngle = 0;
	double power = 1000;

	double fireSpeed = 2;
	boolean shouldFire = false;

	double heat = 0;
	double maxHeat = 100;
	boolean coolingDown = false;

	Point2D targetPoint;

	@Override
	public void render(GraphicsContext gc) {
		super.render(gc);

		double detailLevel = getWidth() / 10;
		gc.setStroke(innerRingColor);
		gc.strokeRect(getXViewport() + detailLevel, getYViewport() + detailLevel, getWidth() - (detailLevel * 2), getHeight() - (detailLevel * 2));

		gc.setStroke(gunColor);
		Point2D gunEndXY = radialToCartesian(gunAngle, (8 * detailLevel));
		gc.strokeLine(getXCenterViewport(), getYCenterViewport(), getXCenterViewport() + gunEndXY.getX(), getYCenterViewport() + gunEndXY.getY());
	}

	@Override
	public void tick(long time_elapsed) {
		super.tick(time_elapsed);

		if(seconds == 0){
			//get next fire location
			targetPoint = getClosestEnemy();
			if(targetPoint != null) {

				gunAngle = (180/Math.PI)*Math.atan2(-(targetPoint.getY() - getYCenter()),targetPoint.getX() - getXCenter());
				shouldFire = true;
			} else {
				shouldFire = false;
//				gunAngle = 0;
			}
		}

		seconds += time_elapsed / 1000.0;

		if(seconds > .1/fireSpeed) {
			innerRingColor = team.getTeamColor();
			gunColor = team.getTeamColor();
		}

		if(coolingDown && seconds > 1/fireSpeed) {

			heat -= 1;
			if(heat == 0)
				coolingDown = false;
			innerRingColor = team.getTeamAlternate2Color();
			gunColor = team.getTeamAlternate2Color();
		} else if(seconds > 1/fireSpeed) {
			if(shouldFire) {
				heat++;
				innerRingColor = team.getTeamAlternate1Color();
				gunColor = team.getTeamAlternate1Color();
				Point2D speedXY = radialToCartesian(gunAngle, power);
				App.thingsWaiting.add(new Projectile(team)
						.setCurrentVelocityX(speedXY.getX())
						.setCurrentVelocityY(speedXY.getY())
						.setDesiredVelocityX(0)
						.setDesiredVelocityY(0)
						.setMaxVelocity(speedXY.magnitude())
						.setAcceleration(.001)
						.setHealth(100)
						.setXCenter(getXCenter())
						.setYCenter(getYCenter()));
			} else {
				heat--;
				if(heat <= 0)
					heat = 0;
			}
			seconds = 0;

			if(heat > maxHeat)
				coolingDown = true;
		}
	}
}
