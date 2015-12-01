package edu.utexas.ece.jacobingalls.things;

import edu.utexas.ece.jacobingalls.player.Team;

/**
 * Created by jacobingalls on 11/13/15.
 */
public abstract class TargetBasedMovingThing extends MovingThing {

	double targetLocationX = 0;
	double targetLocationY = 0;

	public double getTargetLocationX(){return targetLocationX;}
	public double getTargetLocationY(){return targetLocationY;}

	public TargetBasedMovingThing setTargetLocationX(double x){this.targetLocationX = x; return this;}
	public TargetBasedMovingThing setTargetLocationY(double y){this.targetLocationY = y; return this;}

	public TargetBasedMovingThing setTargetLocation(double x, double y){
		return this.setTargetLocationX(x).setTargetLocationY(y);
	}

	public TargetBasedMovingThing(Team team) {
		super(team);
	}

	@Override
	public void tick(long time_elapsed){
		super.tick(time_elapsed);

		/*

			We need to prevent overshooting.
			So we need to figure out if we can stop in time/ what distance we need to kill the engines.
		 */
		double stop_distance_x = (getCurrentVelocityX()*getCurrentVelocityX())/(2 * getAcceleration());
		double stop_distance_y = (getCurrentVelocityY()*getCurrentVelocityY())/(2 * getAcceleration());

		if(getCurrentVelocityX() > 0)
			stop_distance_x = getXCenter() + stop_distance_x;
		else
			stop_distance_x = getXCenter() - stop_distance_x;

		if(getCurrentVelocityY() > 0)
			stop_distance_y = getYCenter() + stop_distance_y;
		else
			stop_distance_y = getYCenter() - stop_distance_y;

		if(targetLocationX > getXCenter() && stop_distance_x > targetLocationX)
			setDesiredVelocityX(0);
		else if(targetLocationX < getXCenter() && stop_distance_x < targetLocationX)
			setDesiredVelocityX(0);
		else if(targetLocationX > getXCenter())
			setDesiredVelocityX(getMaxVelocity());
		else if(targetLocationX < getXCenter())
			setDesiredVelocityX(-getMaxVelocity());

		if(targetLocationY > getYCenter() && stop_distance_y > targetLocationY)
		setDesiredVelocityY(0);
		else if(targetLocationY < getYCenter() && stop_distance_y < targetLocationY)
			setDesiredVelocityY(0);
		else if(targetLocationY > getYCenter())
			setDesiredVelocityY(getMaxVelocity());
		else if(targetLocationY < getYCenter())
			setDesiredVelocityY(-getMaxVelocity());
	}

}
