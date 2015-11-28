package edu.utexas.ece.jacobingalls.player;

import javafx.scene.paint.Color;

/**
 * Created by jacobingalls on 11/14/15.
 */
public class Team {

//	public static Team GREEN = new Team(Color.GREEN);
//	public static Team RED = new Team(Color.RED);
//	public static Team CYAN = new Team(Color.rgb(0, 128, 255));

	public static Team NO_TEAM = new Team(Color.GRAY);


	private Color teamColor;
	private Color teamAlternate1Color;
	private Color teamAlternate2Color;
	private Color teamBackgroundColor;

	private double iron = 0.0;
	private double ironReplenishRate = 1;  //per second

	private double energy = 0.0;
	private double energyReplenishRate = 1;  //per second

	private double gold = 0.0;
	private double goldReplenishRate = .1;  //per second



	public Team(Color teamColor){
		super();
		this.teamColor = teamColor;

		int spit = 40;
		int offset = -120;

		teamAlternate1Color = Color.hsb(teamColor.getHue() + offset + spit, 1, 1);
		teamAlternate2Color = Color.hsb(teamColor.getHue() + offset - spit, 1, 1);
		teamBackgroundColor = teamColor.deriveColor(0,1,.3,1);
	}

	public void tick(long elapsed_time){
		iron+=  ironReplenishRate   * ((double)elapsed_time/1000.0);
		energy+=energyReplenishRate * ((double)elapsed_time/1000.0);
		gold+=  goldReplenishRate   * ((double)elapsed_time/1000.0);
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


	public double getIronReplenishRate() {
		return ironReplenishRate;
	}

	public void setIronReplenishRate(double ironReplenishRate) {
		this.ironReplenishRate = ironReplenishRate;
	}

	public double getEnergy() {
		return energy;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public double getEnergyReplenishRate() {
		return energyReplenishRate;
	}

	public void setEnergyReplenishRate(double energyReplenishRate) {
		this.energyReplenishRate = energyReplenishRate;
	}

	public double getGold() {
		return gold;
	}

	public void setGold(double gold) {
		this.gold = gold;
	}

	public double getGoldReplenishRate() {
		return goldReplenishRate;
	}

	public void setGoldReplenishRate(double goldReplenishRate) {
		this.goldReplenishRate = goldReplenishRate;
	}

	public double getIron() {
		return iron;
	}

	public void setIron(double iron) {
		this.iron = iron;
	}
}
