package edu.utexas.ece.jacobingalls.buildings;

import edu.utexas.ece.jacobingalls.player.Team;
import edu.utexas.ece.jacobingalls.robots.Thing;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by Jacob on 11/15/2015.
 */
public class Building extends Thing {
    double selectSeconds = 0;
    double selectSecondThreshold = .25;
    double selectMax = 10;
    double selectMin = 3;

    public long damageMillis = 0;
    private Color alarmColor;

    public Building(Team team) {
        super(team);
    }

    @Override
    public double getWidth() {
        return 60;
    }

    @Override
    public double getHeight() {
        return 60;
    }

    @Override
    public void render(GraphicsContext gc) {
        if(getHealth() > getMaxHeath()/2 || (int)(damageMillis / 100) % 2 != 0) {
            gc.setStroke(team.getTeamColor());
            gc.setFill(team.getTeamBackgroundColor());
        } else {
            if(alarmColor == null){
                alarmColor = team.getTeamAlternate2Color().darker().darker();
            }
            gc.setStroke(team.getTeamAlternate2Color());
            gc.setFill(alarmColor);
        }


        gc.fillRect(getXViewport(), getYViewport(), getWidth(), getHeight());
        gc.strokeRect(getXViewport(), getYViewport(), getWidth(), getHeight());

        double percent = selectSeconds / selectSecondThreshold;
        double spacing = selectMin + Math.abs((selectMax - selectMin) *  Math.cos(percent * Math.PI / 2));
        Color p1 = team.getTeamAlternate1Color();
        if(isSelected()){ p1 = team.getTeamAlternate2Color();}

        gc.setStroke(p1.deriveColor(0,1,1,percent));
        gc.strokeRect(getXViewport() - spacing, getYViewport() - spacing, getWidth() + (spacing * 2), getHeight() + (spacing * 2));
    }

    @Override
    public void tick(long time_elapsed) {
        damageMillis+=time_elapsed;
        damageMillis %= 1000;

        if((isHovered() || isSelected()) && selectSeconds >= selectSecondThreshold){
            selectSeconds = selectSecondThreshold;
        } else if(isHovered() || isSelected()) {
            selectSeconds += time_elapsed / 1000.0;
        } else if(selectSeconds < 0){
            selectSeconds = 0;
        } else if(selectSeconds > 0){
            selectSeconds -= time_elapsed/1000.0;
        }
    }

    @Override
    public boolean isColliding(double x, double y) {
        return  (x >= getX() && x <= (getX() + getWidth())) &&
                (y >= getY() && y <= (getY() + getHeight()));
    }

    @Override
    protected boolean clicked(double x, double y) {
        setSelected(!isSelected());return true;
    }
}
