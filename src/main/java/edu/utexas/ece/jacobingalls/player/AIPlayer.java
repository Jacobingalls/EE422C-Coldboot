package edu.utexas.ece.jacobingalls.player;

import javafx.scene.paint.Color;

/**
 * Created by jacobingalls on 11/24/15.
 */
public class AIPlayer extends Player {


	private PlayStyle playStyle;
	public enum PlayStyle{
		SIMPLE,
		DO_NOTHING
	}

	public AIPlayer(Color teamColor, PlayStyle playStyle) {
		super(teamColor);
		this.playStyle = playStyle;
	}

	@Override
	public void tick(long time_elapsed){
		super.tick(time_elapsed);
		if(playStyle == PlayStyle.SIMPLE)
			simpleTick(time_elapsed);
	}

	private void simpleTick(long time_elapsed){
		getFactoriesWhere(factory -> factory.getNumberToBuild() < 1).forEach(factory -> factory.setNumberToBuild(1));
	}
}
