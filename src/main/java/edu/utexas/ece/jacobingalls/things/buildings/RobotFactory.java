package edu.utexas.ece.jacobingalls.things.buildings;

import edu.utexas.ece.jacobingalls.Game;
import edu.utexas.ece.jacobingalls.player.Team;
import edu.utexas.ece.jacobingalls.things.robots.Robot;
import edu.utexas.ece.jacobingalls.things.blocks.CPUBlock;
import javafx.scene.canvas.GraphicsContext;

import java.util.function.Function;
import java.util.stream.Collectors;

//TODO Add fancy graphics
//TODO Add Spawn Location
public class RobotFactory extends Factory{
    double robotMaxVelocity = 500;
    double robotAcceleration = 200;

    double energySpeed = 200;

    double destinationX;
    double destinationY;

    private Function<Team, Robot> robotFunction;

    public RobotFactory(Team team, double destinationX, double destinationY,Function<Team, Robot> robotFunction) {
        super(team);

        this.robotFunction = robotFunction;
        setBuildTime(makeRobot().getEnergyInitialCost()/energySpeed);

        this.destinationX = destinationX;
        this.destinationY = destinationY;
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);

        gc.setFill(team.getTeamColor());
        gc.fillText("Robot\nFactory", getXViewport()+10, getYViewport()+20);
    }

    @Override
    public void done() {
        Game.game.getThingsWaiting().add(makeRobot());
    }

    private Robot makeRobot(){
        Robot rob = (Robot)robotFunction.apply(getTeam())
            .setTargetLocation(destinationX,destinationY)
            .setMaxVelocity(robotMaxVelocity)
            .setAcceleration(robotAcceleration)
            .setXCenter(getXCenter())
            .setYCenter(getYCenter());

        if(rob.getBlocks().parallelStream().filter(block -> block instanceof CPUBlock).collect(Collectors.toList()).isEmpty()) {
            rob.getBlocks().parallelStream().filter(block -> block.getRobotX() == 0 && block.getRobotY() == 0).forEach(block -> block.setHealth(-1));
            rob.addBlock(new CPUBlock().setRobotXY(0, 0));
            rob.damage(0,0,0);
        }

        return rob;
    }
}
