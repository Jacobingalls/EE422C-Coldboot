package edu.utexas.ece.jacobingalls.robots;

import edu.utexas.ece.jacobingalls.Team;

/**
 * Created by jacobingalls on 11/13/15.
 */
public abstract class MovingThing extends Thing {
	private double currentVelocityX = 0;
	private double currentVelocityY = 0;

	private double desiredVelocityX = 0;
	private double desiredVelocityY = 0;

	private double maxVelocity = 100.0;
	private double acceleration = 100;

	public MovingThing(Team team){
		super(team);
	}

	@Override
	public void tick(long time_elapsed) {
		double acc = time_elapsed/1000.0;

		if(currentVelocityX < desiredVelocityX)
			currentVelocityX += acceleration * acc;
		else if(currentVelocityX > desiredVelocityX)
			currentVelocityX -= acceleration * acc;

		if(currentVelocityX > maxVelocity)
			currentVelocityX = maxVelocity;
		else if(currentVelocityX < -maxVelocity)
			currentVelocityX = -maxVelocity;

		if(desiredVelocityY > currentVelocityY)
			currentVelocityY += acceleration * acc;
		else if(desiredVelocityY < currentVelocityY)
			currentVelocityY -= acceleration * acc;

		if(currentVelocityY > maxVelocity)
			currentVelocityY = maxVelocity;
		else if(currentVelocityY < -maxVelocity)
			currentVelocityY = -maxVelocity;

		setX(getX() + currentVelocityX * acc);
		setY(getY() + currentVelocityY * acc);
	}

	public double getCurrentVelocityX() { return currentVelocityX; }

	public MovingThing setCurrentVelocityX(double currentVelocityX) { this.currentVelocityX = currentVelocityX; return this; }

	public double getCurrentVelocityY() { return currentVelocityY; }

	public MovingThing setCurrentVelocityY(double currentVelocityY) { this.currentVelocityY = currentVelocityY; return this; }

	public double getAcceleration() { return acceleration; }

	public MovingThing setAcceleration(double acceleration) { this.acceleration = acceleration; return this; }

	public double getDesiredVelocityX() { return desiredVelocityX; }

	public MovingThing setDesiredVelocityX(double desiredVelocityX) { this.desiredVelocityX = desiredVelocityX; return this; }

	public double getDesiredVelocityY() { return desiredVelocityY; }

	public MovingThing setDesiredVelocityY(double desiredVelocityY) { this.desiredVelocityY = desiredVelocityY; return this; }

	public double getMaxVelocity() { return maxVelocity; }

	public MovingThing setMaxVelocity(double maxVelocity) { this.maxVelocity = maxVelocity; return this; }

	public double getVelocityUtilization() {
		if(maxVelocity <= 0)
			return -1;
		else
			return (Math.abs(currentVelocityX) + Math.abs(currentVelocityY)) / (2 * maxVelocity);
	}

}
