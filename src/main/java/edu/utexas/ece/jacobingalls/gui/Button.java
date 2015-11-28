package edu.utexas.ece.jacobingalls.gui;

import edu.utexas.ece.jacobingalls.ColdBootGui;
import edu.utexas.ece.jacobingalls.player.Team;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.TextAlignment;

import java.util.function.Consumer;

/**
 * Created by jacobingalls on 11/16/15.
 */
public class Button {
	private double x;
	private double y;
	private double w;
	private double h;
	private Team team;
	String text = "";
	boolean wasClicked = false;

	public Button(Team team, String text, double x, double y, double w, double h){
		this.team = team;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.text = text;
	}

	public Button handlePossibleClick(boolean clicked, Consumer<Button> l){
		if(clicked && ColdBootGui.mouseX >= x && ColdBootGui.mouseX <= x+w && ColdBootGui.mouseY >= y && ColdBootGui.mouseY <= y+h) {
			l.accept(this);
			wasClicked = true;
		}
		return this;
	}

	public void render(GraphicsContext gc) {
		if(wasClicked){
			gc.setStroke(team.getTeamAlternate2Color());
			gc.setFill(team.getTeamAlternate2Color());
		}else if(ColdBootGui.mouseX >= x && ColdBootGui.mouseX <= x+w && ColdBootGui.mouseY >= y && ColdBootGui.mouseY <= y+h) {
			gc.setStroke(team.getTeamAlternate1Color());
			gc.setFill(team.getTeamAlternate1Color());
		} else {
			gc.setStroke(team.getTeamColor());
			gc.setFill(team.getTeamColor());
		}
		gc.strokeRect(x, y, w, h);
		TextAlignment textAlignment = gc.getTextAlign();
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText("" + text, x+w/2, y + h/2+5, w);
		gc.setTextAlign(textAlignment);
	}
}
