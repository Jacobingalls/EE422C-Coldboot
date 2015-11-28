package edu.utexas.ece.jacobingalls.commands;

import edu.utexas.ece.jacobingalls.ColdBootRepl;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements Command {
	public void run(String[] args) {
		System.out.println("Showing help for the game.\n");

		System.out.println("Commands:");
		ColdBootRepl.commands.parallelStream()
				.sorted((a, b) -> a.getName().compareTo(b.getName()))
				.forEach(command -> System.out.println("\t"+command.getName()+" "+
					command.getArgs().parallelStream()
							.map(s -> "<" + s + "> ")
							.reduce((a, b) -> a + b)
							.orElse("")
					+ " "
					+ command.getDescription()
				));
	}

	public String getName() {
		return "help";
	}

	public List<String> getArgs() {
		return new ArrayList<>();
	}

	@Override
	public String getDescription() {
		return "Shows how to play the game.";
	}
}
