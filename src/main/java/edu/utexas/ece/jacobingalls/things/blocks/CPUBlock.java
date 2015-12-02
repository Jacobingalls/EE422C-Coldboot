package edu.utexas.ece.jacobingalls.things.blocks;

import javafx.scene.canvas.GraphicsContext;


/**
 * Created by jacobingalls on 11/12/15.
 */
public class CPUBlock extends Block {
	@Override
	public void render(GraphicsContext gc) {
		super.render(gc);

		double detailLevel = getWidth() / 10;

		if(!isPowered()){
			gc.setStroke(team.getTeamColor().grayscale());
		} else {
			gc.setStroke(team.getTeamColor());
		}

		gc.strokeRect(getXViewport() + detailLevel, getYViewport() + detailLevel, getWidth() - (detailLevel * 2), getHeight() - (detailLevel * 2));
		gc.strokeLine(getXViewport() + (detailLevel * 3), getYViewport() + (detailLevel * 3), getXViewport() + (detailLevel * 3), getYViewport() + (detailLevel * 7));
		gc.strokeLine(getXViewport() + (detailLevel * 3), getYViewport() + (detailLevel * 7), getXViewport() + (detailLevel * 7), getYViewport() + (detailLevel * 7));
		gc.strokeLine(getXViewport() + (detailLevel * 3), getYViewport() + (detailLevel * 3), getXViewport() + (detailLevel * 7), getYViewport() + (detailLevel * 3));
	}

	@Override
	public void tick(long time_elapsed) {
		super.tick(time_elapsed);
	}
}
