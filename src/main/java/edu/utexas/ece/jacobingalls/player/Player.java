package edu.utexas.ece.jacobingalls.player;

import edu.utexas.ece.jacobingalls.ColdBootGui;
import edu.utexas.ece.jacobingalls.Game;
import edu.utexas.ece.jacobingalls.buildings.Factory;
import edu.utexas.ece.jacobingalls.buildings.TeamBase;
import edu.utexas.ece.jacobingalls.robots.Robot;
import edu.utexas.ece.jacobingalls.robots.Thing;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Player extends Team{
    int lastBaseSelected = -1;
    int lastFactorySelected = -1;
    int lastRobotSelected = -1;




    public Player(Color teamColor){
        super(teamColor);
    }

    public Stream<Thing> getThings(){
        return Game.game.getThings().parallelStream().filter(thing -> thing.getTeam().equals(this));
    }

    public Stream<Thing> getThingsWhere(Predicate<? super Thing> predicate){
        return getThings().filter(predicate);
    }

    public Stream<TeamBase> getBases(){
        return getThingsWhere(thing -> thing instanceof TeamBase).map(thing -> (TeamBase) thing);
    }

    public Stream<TeamBase> getBasesWhere(Predicate<? super TeamBase> predicate){
        return getBases().filter(predicate);
    }

    public void selectNextBase(){
        List<TeamBase> teamBaseList =getBases().collect(Collectors.toList());

        if(teamBaseList.size() <= 0) {
            System.err.println("No home base!");
            return;
        }

        lastBaseSelected++;
        lastBaseSelected %= teamBaseList.size();
        ColdBootGui.viewpointOverride = false;
        Game.game.getThings().parallelStream().forEach(thing -> thing.setSelected(false));
        teamBaseList.get(lastBaseSelected).setSelected(true);
    }

    public Stream<Factory> getFactories(){
        return getThingsWhere(thing -> thing instanceof Factory).map(thing -> (Factory) thing);
    }

    public Stream<Factory> getFactoriesWhere(Predicate<? super Factory> predicate){
        return getFactories().filter(predicate);
    }

    public void selectNextFactory(){
        List<Factory> factoryList =getFactories().collect(Collectors.toList());

        if(factoryList.size() <= 0) {
            System.err.println("No factories!");
            return;
        }

        lastFactorySelected++;
        lastFactorySelected %= factoryList.size();
        ColdBootGui.viewpointOverride = false;
        Game.game.getThings().parallelStream().forEach(thing -> thing.setSelected(false));
        factoryList.get(lastFactorySelected).setSelected(true);
    }

    public Stream<Thing> getSelectedThings(){
        return Game.game.getThings().parallelStream().filter(Thing::isSelected);
    }

    public Stream<Thing> getSelectedThingsWhere(Predicate<Thing> predicate){
        return getSelectedThings().filter(predicate);
    }

    public Stream<Robot> getRobots(){
        return getThingsWhere(thing -> thing instanceof Robot).map(thing -> (Robot) thing);
    }

    public Stream<Robot> getRobotsWhere(Predicate<? super Robot> predicate){
        return getRobots().filter(predicate);
    }

    public void selectNextRobot(){
        List<Robot> robotList =getRobots().collect(Collectors.toList());

        if(robotList.size() <= 0) {
            System.err.println("No robots!");
            return;
        }

        lastRobotSelected++;
        lastRobotSelected %= robotList.size();
        ColdBootGui.viewpointOverride = false;
        Game.game.getThings().parallelStream().forEach(thing -> thing.setSelected(false));
        robotList.get(lastRobotSelected).setSelected(true);
    }


}
