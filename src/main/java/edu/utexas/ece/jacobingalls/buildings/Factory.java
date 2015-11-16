package edu.utexas.ece.jacobingalls.buildings;

import edu.utexas.ece.jacobingalls.Team;
import edu.utexas.ece.jacobingalls.robots.gui.ProgressBar;
import javafx.scene.canvas.GraphicsContext;

/**
 * Created by Jacob on 11/15/2015.
 */
public abstract class Factory extends Building {
    double currentTime = 0;
    double buildTime = 1;
    boolean building = false;

    int numberToBuild = 10;

    public Factory(Team team) {
        super(team);
    }

    @Override
    public  void render(GraphicsContext gc){
        super.render(gc);
        if(building){
            new ProgressBar(this.getTeam(), currentTime/buildTime, getXViewport() + 5, getYViewport() + getHeight() - 15, getWidth() - 10, 10)
                    .setTiggerDirection(false)
                    .setTrigger(.9)
                    .render(gc);
            if(numberToBuild > 1) {
                gc.setFill(team.getTeamAlternate1Color());
                gc.fillText("" + numberToBuild, getXCenterViewport() - 5, getYViewport() + getHeight() - 5);
            }
        }
    }

    @Override
    public  void tick(long time_elapsed){
        super.tick(time_elapsed);
        if(building)
            currentTime += time_elapsed/1000.0;

        if(currentTime >= buildTime){
            currentTime = 0;
            numberToBuild--;
            building = false;
            done();
        }

        if(!building && numberToBuild > 0)
            building = true;
    }

    public abstract void done();

    public double getCurrentTime() {
        return currentTime;
    }

    public Factory setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
        return this;
    }

    public double getBuildTime() {
        return buildTime;
    }

    public Factory setBuildTime(double buildTime) {
        this.buildTime = buildTime;
        return this;
    }

    public boolean isBuilding() {
        return building;
    }

    public Factory setBuilding(boolean building) {
        this.building = building;
        return this;
    }
}
