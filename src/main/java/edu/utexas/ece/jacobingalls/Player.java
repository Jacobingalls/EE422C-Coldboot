package edu.utexas.ece.jacobingalls;

/**
 * Created by Jacob on 11/15/2015.
 */
public class Player {
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    private Team team;
    public Player(Team team){
        this.team = team;
    }
}
