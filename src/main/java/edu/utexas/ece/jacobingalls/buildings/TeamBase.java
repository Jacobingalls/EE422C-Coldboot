package edu.utexas.ece.jacobingalls.buildings;

import edu.utexas.ece.jacobingalls.Team;
import javafx.scene.canvas.GraphicsContext;

public class TeamBase extends Building {
    public TeamBase(Team team) {
        super(team);
    }

    @Override
    public  void render(GraphicsContext gc){
        super.render(gc);
        gc.setFill(team.getTeamColor());
        gc.fillText("Base", getXViewport()+10, getYViewport()+20);
    }
}
