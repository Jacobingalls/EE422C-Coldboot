package edu.utexas.ece.jacobingalls.robots.blocks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;

/**
 * Created by jacobingalls on 11/16/15.
 */
public class ReactorBlock extends Block{
	double reactorTick = 0;

	double energyProduction = 10;

	@Override
	public void render(GraphicsContext gc) {
		super.render(gc);

		double detailLevel = getWidth() / 10;
		if(isPowered())gc.setStroke(team.getTeamColor()); else gc.setStroke(team.getTeamColor().grayscale());
		gc.strokeRect(getXViewport() + detailLevel, getYViewport() + detailLevel, getWidth() - (detailLevel * 2), getHeight() - (detailLevel * 2));
		gc.strokeOval(getXViewport() + (3 * detailLevel), getYViewport() + (3 * detailLevel), getWidth() - (detailLevel * 6), getHeight() - (detailLevel * 6));

		if(isPowered())gc.setStroke(team.getTeamAlternate1Color()); else gc.setStroke(team.getTeamAlternate1Color().grayscale());
		gc.strokeArc(getXViewport()+(3*detailLevel),getYViewport()+(3*detailLevel),getWidth() - (detailLevel * 6),getHeight() - (detailLevel * 6),reactorTick, 90, ArcType.OPEN);

	}

	@Override
	public void tick(long time_elapsed){
		super.tick(time_elapsed);

		if(!isPowered())
			return;

		reactorTick+=time_elapsed;
		if(reactorTick > 360){
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
