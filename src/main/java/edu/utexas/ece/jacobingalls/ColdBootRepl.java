package edu.utexas.ece.jacobingalls;

import edu.utexas.ece.jacobingalls.things.buildings.Building;
import edu.utexas.ece.jacobingalls.things.buildings.Factory;
import edu.utexas.ece.jacobingalls.commands.*;
import edu.utexas.ece.jacobingalls.player.AIPlayer;
import edu.utexas.ece.jacobingalls.things.robots.Robot;
import edu.utexas.ece.jacobingalls.things.Thing;
import edu.utexas.ece.jacobingalls.things.robots.particles.Particle;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jacobingalls on 11/26/15.
 */
public class ColdBootRepl {

	//from http://stackoverflow.com/questions/5923436/change-color-of-java-console-output#5923462
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static boolean running = false;

	public static List<Command> commands = Arrays.asList(
			new HelpCommand(),
			new WatchCommand(),
			new StatsCommand(),
			new ListCommand(),
			new TickCommand());

	public static void main(String[] args){

		Game.game = new Game( new AIPlayer(Color.GREEN, AIPlayer.PlayStyle.SIMPLE),
				new AIPlayer(Color.RED, AIPlayer.PlayStyle.SIMPLE));
		Game.game.setWorldWidth(800);
		Game.game.setWorldHeight(600);
		Game.game.configureWorld();
		Game.game.setWorldWidth(800);
		Game.game.setWorldHeight(20);

		motd();
		Scanner scanner = new Scanner(System.in);

		running = true;
		do{
			//read
			System.out.print("$ ");
			String[] str = scanner.nextLine().toLowerCase().trim().split("\\s");

			//execute and print
			if(str.length <= 0 || str[0].trim().isEmpty()){
				System.out.print("");
			} else {

				Optional<Command> commandOptional = commands.parallelStream().filter(command -> command.getName().equals(str[0])).findFirst();
				if(commandOptional.isPresent()){
					commandOptional.get().run(Arrays.copyOfRange(str,1,str.length));
				} else {
					if(str[0].equals("exit") || str[0].equals("quit")) {
						running = false;
						System.out.println(str[0]+"ing...");
					} else
						System.out.println("Command not found.");
				}
			}

			//loop
		} while (running);

	}

	public static void motd(){
		System.out.println("Welcome to ColdBoot!\n\n" +
				"There is an interactive gui available, run ColdBootGui instead.\n\n" +
				"For help run `help`.");
	}

	public static void print(){
		List<Thing> thingsWeCareToPrint = Game.game.getThings().parallelStream()
				.filter(thing -> !(thing instanceof Particle))
//				.filter(thing -> thing.getX() + thing.getWidth() >= ColdBootGui.viewportX)
//				.filter(thing -> thing.getX() <= ColdBootGui.viewportX + game.getWorldWidth())
//				.filter(thing -> thing.getY() + thing.getHealth() >= ColdBootGui.viewportY)
//				.filter(thing -> thing.getY() <= ColdBootGui.viewportY + game.getWorldHeight())
				.collect(Collectors.toList());


		String[][] strings = new String[Game.game.getWorldHeight()][Game.game.getWorldWidth()];

		thingsWeCareToPrint.parallelStream()
				.filter(thing -> thing instanceof Building)
				.map(thing -> (Building)thing)
				.forEach(building -> {
					int x = (int)(building.getXViewport()/20);
					int y = (int)(building.getYViewport()/20);
					int x2 = (int)((x + building.getWidth())/20);
					int y2 = (int)((y + building.getHeight())/20);

					String colorStr = "";
					if(building.getTeam().getTeamColor().equals(Color.GREEN))
						colorStr = ANSI_GREEN;
					else if(building.getTeam().getTeamColor().equals(Color.RED))
						colorStr = ANSI_RED;

					for (int i = y; i < y2; i++) {
						for (int j = x; j < x2; j++) {
							if (i >= ColdBootGui.viewportY && i < ColdBootGui.viewportY + Game.game.getWorldHeight()){
								if (j >= ColdBootGui.viewportX && j < ColdBootGui.viewportX + Game.game.getWorldWidth()) {
									if(i==y || i+1 ==y2 || j == x || j+1 == x2)
										strings[i][j] = colorStr + "B" + ANSI_RESET;

									if(building instanceof Factory){
										Factory factory = (Factory)building;
										if((i==y2-3)){
											if((j-x) <= factory.getBuildPercentage() * (x2 - x))
												strings[i][j] = colorStr + "=" + ANSI_RESET;
										}
									}
								}
							}
						}
					}

				});


		thingsWeCareToPrint.parallelStream()
				.filter(thing -> thing instanceof Robot)
				.map(thing -> (Robot)thing)
				.flatMap(robot -> robot.getBlocks().parallelStream())
				.forEach(block -> {
					int x = (int)block.getXViewport()/20;
					int y = (int)block.getYViewport()/20;

					String colorStr = "";
					if(block.getTeam().getTeamColor().equals(Color.GREEN))
						colorStr = ANSI_GREEN;
					else if(block.getTeam().getTeamColor().equals(Color.RED))
						colorStr = ANSI_RED;


					if (y >= ColdBootGui.viewportY && y < ColdBootGui.viewportY + Game.game.getWorldHeight()) {
						if (x >= ColdBootGui.viewportX && x < ColdBootGui.viewportX + Game.game.getWorldWidth()) {
							strings[y][x] = colorStr + "R" + ANSI_RESET;
						}
					}
				});

		System.out.println("---"+thingsWeCareToPrint.size());
		for (int i = 0; i < strings.length; i++) {
			for (int j = 0; j < strings[i].length; j++) {
				if(strings[i][j] != null)
					System.out.print(strings[i][j]);
				else
					System.out.print(" ");
			}
			System.out.println();
		}
	}

	public static void printStats() {
		String team1str = "";
		if(Game.game.getPlayer().getTeamColor().equals(Color.GREEN))
			team1str = ANSI_GREEN;
		else if(Game.game.getPlayer().getTeamColor().equals(Color.RED))
			team1str = ANSI_RED;

		int t1baseHeath = (int)(100 * Game.game.getPlayer().getBases().mapToDouble(Thing::getHealthPercentage).average().orElse(0));
		int t1robots = (int) Game.game.getPlayer().getRobots().count();
		int t1factories = (int)(Game.game.getPlayer().getFactories().count());
		team1str += "Heath: " + t1baseHeath + " Factories: "+ t1factories + " Robots: "+t1robots+ANSI_RESET;


		String team2str = "";
		if(Game.game.getAiplayer().getTeamColor().equals(Color.GREEN))
			team2str = ANSI_GREEN;
		else if(Game.game.getAiplayer().getTeamColor().equals(Color.RED))
			team2str = ANSI_RED;

		int t2baseHeath = (int)(100 * Game.game.getAiplayer().getBases().mapToDouble(Thing::getHealthPercentage).average().orElse(0));
		int t2robots = (int) Game.game.getAiplayer().getRobots().count();
		int t2factories = (int)(Game.game.getAiplayer().getFactories().count());
		team2str += "Heath: " + t2baseHeath + " Factories: "+ t2factories + " Robots: "+t2robots+ANSI_RESET;


		System.out.println(team1str + " | "+ team2str);
	}
}
