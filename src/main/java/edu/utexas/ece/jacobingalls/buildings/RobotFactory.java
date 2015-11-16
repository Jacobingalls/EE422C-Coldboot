package edu.utexas.ece.jacobingalls.buildings;

import edu.utexas.ece.jacobingalls.App;
import edu.utexas.ece.jacobingalls.Team;
import edu.utexas.ece.jacobingalls.robots.Robot;
import edu.utexas.ece.jacobingalls.robots.blocks.Block;
import edu.utexas.ece.jacobingalls.robots.blocks.CPUBlock;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

//TODO Add fancy graphics
//TODO Add Spawn Location
public class RobotFactory extends Factory{
    double robotMaxVelocity = 500;
    double robotAcceleration = 200;

    private Function<Robot, Robot> robotFunction;

    public RobotFactory(Team team, Function<Robot, Robot> robotFunction) {
        super(team);

        this.robotFunction = robotFunction;
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);

        gc.setFill(team.getTeamColor());
        gc.fillText("Robot\nFactory", getXViewport()+10, getYViewport()+20);
    }

    @Override
    public void done() {
         Robot rob = (Robot)robotFunction.apply(new Robot(getTeam()))
            .setTargetLocation(getXCenter() + getWidth(), getYCenter())
            .setMaxVelocity(robotMaxVelocity)
            .setAcceleration(robotAcceleration)
            .setX(getXCenter()).setY(getYCenter());

        if(rob.getBlocks().parallelStream().filter(block -> block instanceof CPUBlock).collect(Collectors.toList()).isEmpty()) {
            rob.getBlocks().parallelStream().filter(block -> block.getRobotX() == 0 && block.getRobotY() == 0).forEach(block -> block.setHealth(-1));
            rob.addBlock(new CPUBlock().setRobotXY(0, 0));
            rob.damage(0,0,0);
        }

        App.thingsWaiting.add(rob);
    }
}
