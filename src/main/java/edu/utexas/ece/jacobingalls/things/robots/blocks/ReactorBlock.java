package edu.utexas.ece.jacobingalls.things.robots.blocks;

import edu.utexas.ece.jacobingalls.player.Team;
import javafx.scene.canvas.GraphicsContext;

/**
 * Created by jacobingalls on 11/16/15.
 */
public class ReactorBlock extends Block{
	double reactorTick = 0;

	double energyProduction = 10;

	public ReactorBlock(){
		this(Team.NO_TEAM);
	}

	public ReactorBlock(Team team){
		super(team);
		setMass(5);
	}

	@Override
	public void render(GraphicsContext gc) {
		super.render(gc);

		double detailLevel = getWidth() / 10;
		gc.setStroke(team.getTeamColor());
		gc.strokeRect(getXViewport() + detailLevel, getYViewport() + detailLevel, getWidth() - (detailLevel * 2), getHeight() - (detailLevel * 2));

		gc.setStroke(team.getTeamAlternate1Color());
		double dl2 = (2*detailLevel);
		double dl3 = (3*detailLevel);
		double dl5 = (5*detailLevel);
		double dl7 = (7*detailLevel);
		double lx = getXViewport();
		double ly1 = getYCenterViewport() + dl2 + Math.sin((reactorTick*Math.PI)/600);
		double ly2 = getYCenterViewport() - dl2 - Math.sin((reactorTick*Math.PI)/600);

		if(getRobot().getEnergyUtilization() > 1.5)
			gc.setStroke(team.getTeamAlternate2Color());

		gc.strokeLine(lx + dl7, ly1, lx + dl7, ly2);

		if(getRobot().getEnergyUtilization() > 1.25)
			gc.setStroke(team.getTeamAlternate2Color());
		gc.strokeLine(lx + dl5, ly1, lx + dl5, ly2);

		if(getRobot().getEnergyUtilization() > 1)
			gc.setStroke(team.getTeamAlternate2Color());
		gc.strokeLine(lx + dl3, ly1, lx + dl3, ly2);
	}

	@Override
	public void tick(long time_elapsed){
		super.tick(time_elapsed);

		if(!isPowered())
			this.setPowered(true);

		reactorTick+=time_elapsed * (getRobot().getEnergyUtilization() * getRobot().getEnergyUtilization());
		if(reactorTick > 1800){
			reactorTick = 0;
		}
	}

	public double getEnergyProduction() {
		return energyProduction;
	}

	public ReactorBlock setEnergyProduction(double energyProduction) {
		this.energyProduction = energyProduction;
		return this;
	}
}
