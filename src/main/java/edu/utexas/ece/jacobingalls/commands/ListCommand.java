package edu.utexas.ece.jacobingalls.commands;

import com.sun.xml.internal.rngom.parse.host.Base;
import edu.utexas.ece.jacobingalls.ColdBootRepl;
import edu.utexas.ece.jacobingalls.Game;
import edu.utexas.ece.jacobingalls.buildings.Factory;
import edu.utexas.ece.jacobingalls.buildings.TeamBase;
import edu.utexas.ece.jacobingalls.robots.Robot;
import edu.utexas.ece.jacobingalls.robots.Thing;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by jacobingalls on 11/27/15.
 */
public class ListCommand implements Command {
	public void run(String[] args) {
		String type = "robot";
		String order = "name";
		String max = "-1";

		if(args.length > 0 ){
			type = args[0];
			if(args.length > 1){
				order = args[1];
				if(args.length > 2) {
					max = args[2];
				}
			}
		}

		Class<? extends Thing> typeOfThing;
		if(type.equals("robot")){
			typeOfThing = Robot.class;
		} else if(type.equals("factory")){
			typeOfThing = Factory.class;
		} else if(type.equals("base")){
			typeOfThing = TeamBase.class;
		} else if(type.equals("all")){
			typeOfThing = Thing.class;
		} else {
			System.err.println("'"+type+"' is not a type of thing. Use robot, factory, base, or all instead.");
			return;
		}

		Comparator<Thing> orderByThing;
		if(order.equals("health")){
			orderByThing = (a,b) -> Double.compare(a.getHealthPercentage(), b.getHealthPercentage());
		} else if(order.equals("x")){
			orderByThing = (a,b) -> Double.compare( a.getX(),  b.getX());
		} else if(order.equals("y")){
			orderByThing = (a,b) -> Double.compare( a.getY(),  b.getY());
		} else if(order.equals("name")){
			orderByThing = (a,b) -> a.getClass().getSimpleName().compareTo(b.getClass().getSimpleName());
		} else {
			System.err.println("'"+order+"' is not an order of things. Use health, x, y instead.");
			return;
		}

		int maxNumberOfThings;
		try{
			maxNumberOfThings = Integer.valueOf(max);
		} catch (Exception ignored){
			System.err.println("'"+max+"' is not an integer.");
			return;
		}


		Stream<Thing> thingStream = Game.game.getThings().parallelStream()
				.filter(typeOfThing::isInstance)
				.sorted(orderByThing);

		if(maxNumberOfThings >= 0)
			thingStream = thingStream.limit(maxNumberOfThings);

		System.out.println("Class\t\tx\ty\tHealth");
		thingStream.forEach(thing -> {
			String colorStr = "";
			if (thing.getTeam().getTeamColor().equals(Color.GREEN))
				colorStr = ColdBootRepl.ANSI_GREEN;
			else if (thing.getTeam().getTeamColor().equals(Color.RED))
				colorStr = ColdBootRepl.ANSI_RED;

			String name = colorStr+thing.getClass().getSimpleName()+ColdBootRepl.ANSI_RESET;

			if(thing.getHealthPercentage() < .5)
				colorStr = ColdBootRepl.ANSI_RED;
			else
				colorStr = "";
			String health = colorStr+thing.getHealthPercentage()+ColdBootRepl.ANSI_RESET;


			System.out.println(name + "\t" + "\t" + (int)thing.getX() + "\t" + (int)thing.getY() + "\t" + health);
		});


	}

	public String getName() {
		return "list";
	}

	public List<String> getArgs() {
		return Arrays.asList("type"
//				"order_by",
//				"max"
		);
	}

	public String getDescription() {
		return "list things";
	}
}
