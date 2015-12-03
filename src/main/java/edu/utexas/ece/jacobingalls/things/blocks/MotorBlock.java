package edu.utexas.ece.jacobingalls.things.blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;

/**
 * Created by jacobingalls on 11/17/15.
 */
public class MotorBlock extends Block{
	private double strength = 100;
	private int motorTick = 0;

	boolean moving = false;

	@Override
	public void render(GraphicsContext gc) {
		super.render(gc);

		double detailLevel = getWidth() / 10;
		if(isPowered())gc.setStroke(team.getTeamColor()); else gc.setStroke(team.getTeamColor().grayscale());
		gc.strokeRect(getXViewport() + detailLevel, getYViewport() + detailLevel, getWidth() - (detailLevel * 2), getHeight() - (detailLevel * 2));

		if(isPowered() && getRobot().getTorqueUtilization() > 1)
			gc.setStroke(team.getTeamColor().grayscale());

		gc.strokeOval(getXViewport() + (3 * detailLevel), getYViewport() + (3 * detailLevel), getWidth() - (detailLevel * 6), getHeight() - (detailLevel * 6));

		gc.setStroke(team.getTeamAlternate1Color());
		gc.strokeArc(getXViewport()+(3*detailLevel),getYViewport()+(3*detailLevel),getWidth() - (detailLevel * 6),getHeight() - (detailLevel * 6),motorTick, 90, ArcType.OPEN);

	}

	@Override
	public void tick(long time_elapsed){
		super.tick(time_elapsed);

		if(!isPowered()) {
			setEnergyRunningCost(1);
			return;
		}

		if(Math.abs(getRobot().getCurrentVelocityX()) > 10 || Math.abs(getRobot().getCurrentVelocityY()) > 10){
			setEnergyRunningCost(strength/50);
			moving = true;
		} else {
			setEnergyRunningCost(1);
			moving = false;
		}


		if(moving)
			motorTick+=time_elapsed * getRobot().getTorqueUtilization();

		if(motorTick > 360){
			motorTick = 0;
		}
	}

	public double getStrength() {
		if(!isPowered())
			return 0;
		else
			return strength;
	}

	public MotorBlock setStrength(double strength) {
		this.strength = strength;
		return this;
	}
}
