package edu.utexas.ece.jacobingalls.robots.gui;

import edu.utexas.ece.jacobingalls.Team;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class ProgressBar {

	private double progress;
	private double x;
	private double y;
	private double w;
	private double h;
	private  Team team;
	String text = "";

	private double trigger = -1;
	private boolean tiggerDirection = true;

	public ProgressBar(Team team, double progress, double x, double y, double w, double h){
		this.team = team;
		this.progress = progress;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void render(GraphicsContext gc){
		if(tiggerDirection && progress <= trigger){
			gc.setStroke(team.getTeamAlternate1Color());
		} else if(!tiggerDirection && progress >= trigger){
			gc.setStroke(team.getTeamAlternate2Color());
		} else gc.setStroke(team.getTeamColor());

		gc.strokeLine(x,y,x+10,y);
		gc.strokeLine(x,y+h,x+10,y+h);
		gc.strokeLine(x,y,x,y+h);

		if(progress > 1){
			gc.strokeLine(x+w,y+h/2,x+w-3,y);
			gc.strokeLine(x+w,y+h/2,x+w-3,y+h);
			text = text+" "+Math.round(progress * 100)+"% ";
		} else {
			gc.strokeLine(x+w,y,x+w-10,y);
			gc.strokeLine(x+w,y+h,x+w-10,y+h);
			gc.strokeLine(x+w,y,x+w,y+h);
		}


		double spacer = h/4;
		for(int i = 0; i < (w*progress)-2*spacer+1 && i < w -2*spacer + 1; i++){
			if(i % 2 == 0)
			gc.strokeLine(x+i+spacer,y+spacer,x+i+spacer,y+h-spacer);
		}

		if(!text.isEmpty()){

			TextAlignment textAlignment = gc.getTextAlign();
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setFill(team.getTeamBackgroundColor());
			gc.fillText("" + text, x + w/2 -1, y + h - 1);
			gc.fillText("" + text, x + w/2 +1, y + h - 1);
			gc.fillText("" + text, x + w/2 -1, y + h + 1);
			gc.fillText("" + text, x + w/2 +1, y + h + 1);


			if(tiggerDirection)
				gc.setFill(team.getTeamAlternate2Color());
			else
				gc.setFill(team.getTeamAlternate1Color());


			gc.fillText("" + text, x + w/2, y + h);

			gc.setTextAlign(textAlignment);
		}
	}



	public double getProgress() {
		return progress;
	}

	public ProgressBar setProgress(double progress) {
		this.progress = progress;
		return this;
	}

	public double getX() {
		return x;
	}

	public ProgressBar setX(double x) {
		this.x = x;
		return this;
	}

	public double getY() {
		return y;
	}

	public ProgressBar setY(double y) {
		this.y = y;
		return this;
	}

	public double getW() {
		return w;
	}

	public ProgressBar setW(double w) {
		this.w = w;
		return this;
	}

	public double getH() {
		return h;
	}

	public ProgressBar setH(double h) {
		this.h = h;
		return this;
	}
	public double getTrigger() {
		return trigger;
	}

	public ProgressBar setTrigger(double trigger) {
		this.trigger = trigger;
		return this;
	}

	public boolean isTiggerDirection() {
		return tiggerDirection;
	}

	public ProgressBar setTiggerDirection(boolean tiggerDirection) {
		this.tiggerDirection = tiggerDirection;
		return this;
	}

	public Team getTeam() {
		return team;
	}

	public ProgressBar setTeam(Team team) {
		this.team = team;
		return this;
	}

	public String getText() {
		return text;
	}

	public ProgressBar setText(String text) {
		this.text = text;
		return this;
	}
}
