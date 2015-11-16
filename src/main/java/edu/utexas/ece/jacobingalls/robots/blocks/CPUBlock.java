package edu.utexas.ece.jacobingalls.robots.blocks;

import edu.utexas.ece.jacobingalls.Team;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


/**
 * Created by jacobingalls on 11/12/15.
 */
public class CPUBlock extends Block {
	@Override
	public void render(GraphicsContext gc) {
		super.render(gc);

		double detailLevel = getWidth() / 10;
		gc.setStroke(team.getTeamColor());
		gc.strokeRect(getXViewport() + detailLevel, getYViewport() + detailLevel, getWidth() - (detailLevel * 2), getHeight() - (detailLevel * 2));

		gc.setStroke(team.getTeamColor());
		gc.strokeLine(getXViewport() + (detailLevel * 3), getYViewport() + (detailLevel * 3), getXViewport() + (detailLevel * 3), getYViewport() + (detailLevel * 7));
		gc.strokeLine(getXViewport() + (detailLevel * 3), getYViewport() + (detailLevel * 7), getXViewport() + (detailLevel * 7), getYViewport() + (detailLevel * 7));
		gc.strokeLine(getXViewport() + (detailLevel * 3), getYViewport() + (detailLevel * 3), getXViewport() + (detailLevel * 7), getYViewport() + (detailLevel * 3));
	}

	@Override
	public void tick(long time_elapsed) {
		super.tick(time_elapsed);
	}
}
