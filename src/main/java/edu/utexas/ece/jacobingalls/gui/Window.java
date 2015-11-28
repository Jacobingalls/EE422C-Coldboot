package edu.utexas.ece.jacobingalls.gui;

import edu.utexas.ece.jacobingalls.player.Team;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by jacobingalls on 11/18/15.
 */
public class Window {
	private double x;
	private double y;
	private double w;
	private double h;
	private Team team;

	public Window(Team team, double x, double y, double w, double h){
		this.team = team;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void render(GraphicsContext gc) {
		gc.setFill(Color.rgb(0, 0, 0, .8));
		gc.setStroke(team.getTeamColor());

		gc.fillRect(x,y,w,h);
		gc.strokeRect(x,y,w,h);
	}
}
