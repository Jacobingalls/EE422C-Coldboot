package edu.utexas.ece.jacobingalls.commands;

import edu.utexas.ece.jacobingalls.ColdBootRepl;
import edu.utexas.ece.jacobingalls.Game;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jacobingalls on 11/27/15.
 */
public class TickCommand implements Command {

	public void run(String[] args) {
		int tickTime = 16;
		int numTicks = 1;

		if(args.length > 0) {
			try {
				numTicks = Integer.parseInt(args[0]);
			} catch (Exception ignored){
				System.err.println(args[0]+" is not a valid number of ticks.");
				return;
			}
		}

		if(args.length > 1) {
			try {
				tickTime = Integer.parseInt(args[1]);

				if(tickTime <= 0){
					System.err.println(args[1]+" is not a valid amount of time for a tick.");
					return;
				}

			} catch (Exception ignored){
				System.err.println(args[1]+" is not a valid amount of time for a tick.");
				return;
			}
		}

		for (int i = 0; i < numTicks; i++) {
			Game.game.tick(tickTime);
		}
	}

	public String getName() {
		return "tick";
	}

	public List<String> getArgs() {
		return Arrays.asList("number", "time");
	}

	public String getDescription() {
		return "Number of ticks to tick the game.";
	}
}
