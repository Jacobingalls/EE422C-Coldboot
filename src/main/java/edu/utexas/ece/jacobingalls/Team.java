package edu.utexas.ece.jacobingalls;

import javafx.scene.paint.Color;

/**
 * Created by jacobingalls on 11/14/15.
 */
public class Team {

	public static Team GREEN = new Team(Color.GREEN);
	public static Team RED = new Team(Color.RED);
	public static Team CYAN = new Team(Color.rgb(0, 128, 255));

	public static Team NO_TEAM = new Team(Color.GRAY);


	private Color teamColor;
	private Color teamAlternate1Color;
	private Color teamAlternate2Color;
	private Color teamBackgroundColor;

	public Team(Color teamColor){
		super();
		this.teamColor = teamColor;

		int spit = 40;
		int offset = -120;

		teamAlternate1Color = Color.hsb(teamColor.getHue() + offset + spit, 1, 1);
		teamAlternate2Color = Color.hsb(teamColor.getHue() + offset - spit, 1, 1);
		teamBackgroundColor = teamColor.deriveColor(0,1,.3,1);
	}

	public Color getTeamAlternate1Color() {
		return teamAlternate1Color;
	}

	public Team setTeamAlternate1Color(Color teamAlternate1Color) {
		this.teamAlternate1Color = teamAlternate1Color;
		return this;
	}

	public Color getTeamColor() {
		return teamColor;
	}

	public Team setTeamColor(Color teamColor) {
		this.teamColor = teamColor;
		return this;
	}

	public Color getTeamBackgroundColor() {
		return teamBackgroundColor;
	}

	public Team setTeamBackgroundColor(Color teamBackgroundColor) {
		this.teamBackgroundColor = teamBackgroundColor;
		return this;
	}

	public Color getTeamAlternate2Color() {
		return teamAlternate2Color;
	}

	public Team setTeamAlternate2Color(Color teamAlternate2Color) {
		this.teamAlternate2Color = teamAlternate2Color;
		return this;
	}
}
