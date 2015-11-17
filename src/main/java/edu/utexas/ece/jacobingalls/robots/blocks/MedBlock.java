package edu.utexas.ece.jacobingalls.robots.blocks;

import javafx.scene.canvas.GraphicsContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jacobingalls on 11/16/15.
 */
public class MedBlock extends Block{
	double healStrength = .05;

	boolean healing = false;
	double charge = 0;

	double notHealingRunningCost = 1;
	double healingRunningCost = 10;

	@Override
	public void render(GraphicsContext gc) {
		super.render(gc);

		double detailLevel = getWidth() / 10;
		if(!isPowered())
			gc.setStroke(team.getTeamColor().grayscale());
		else
			gc.setStroke(team.getTeamColor());
		gc.strokeRect(getXViewport() + detailLevel, getYViewport() + detailLevel, getWidth() - (detailLevel * 2), getHeight() - (detailLevel * 2));

		if(!isPowered())
			gc.setStroke(team.getTeamColor().grayscale());
		else if(healing)
			gc.setStroke(team.getTeamAlternate2Color());
		else
			gc.setStroke(team.getTeamColor());
		gc.strokeLine(getXViewport() + (detailLevel * 3), getYViewport() + (detailLevel * 3), getXViewport() + (detailLevel * 3), getYViewport() + (detailLevel * 7));
		gc.strokeLine(getXViewport() + (detailLevel * 7), getYViewport() + (detailLevel * 3), getXViewport() + (detailLevel * 7), getYViewport() + (detailLevel * 7));
		gc.strokeLine(getXViewport() + (detailLevel * 3), getYViewport() + (detailLevel * 5), getXViewport() + (detailLevel * 7), getYViewport() + (detailLevel * 5));
	}

	@Override
	public void tick(long time_elapsed) {
		super.tick(time_elapsed);
		healing = false;

		setEnergyRunningCost(notHealingRunningCost);

		if(!isPowered()) {
			charge = 0;
			return;
		}

//		charge ++;
//
//		if(charge < 100){
//			return;
//		}

		if(getRobot() != null) {
			List<Block> toHeal = getRobot().getBlocks()
					.parallelStream()
					.filter(block -> block.getHealthPercentage() < 1)
					.collect(Collectors.toList());

			healing = !toHeal.isEmpty();
			if(healing)
				setEnergyRunningCost(healingRunningCost);

			toHeal.parallelStream().forEach(block -> {
				double newHealth = block.getHealth() + healStrength;
				if (newHealth > block.getMaxHeath())
					newHealth = block.getMaxHeath();
				block.setHealth(newHealth);
				block.setBeingHealed(true);
			});
		}
	}


}
