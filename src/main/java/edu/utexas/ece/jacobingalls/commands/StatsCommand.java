package edu.utexas.ece.jacobingalls.commands;

import edu.utexas.ece.jacobingalls.ColdBootRepl;
import edu.utexas.ece.jacobingalls.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by jacobingalls on 11/26/15.
 */
public class StatsCommand implements Command {
	public void run(String[] args) {

		Scanner scanner = new Scanner(System.in);

		StatsThread statsThread = new StatsThread();
		statsThread.running = true;
		statsThread.start();

		if(scanner.hasNext())
			statsThread.running = false;
	}

	public String getName() {
		return "stats";
	}

	public List<String> getArgs() {
		return new ArrayList<>();
	}

	public String getDescription() {
		return "Runs the game and view stats for which side is winning.";
	}
}

class StatsThread extends Thread{
	public boolean running = true;

	@Override
	public void run(){
		while (running){

			for (int i = 0; i < 60; i++) {
				Game.game.tick(16);
			}
			ColdBootRepl.printStats();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			}
		}
	}
}
