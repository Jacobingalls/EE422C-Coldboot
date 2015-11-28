package edu.utexas.ece.jacobingalls.commands;

import edu.utexas.ece.jacobingalls.ColdBootRepl;
import edu.utexas.ece.jacobingalls.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by jacobingalls on 11/26/15.
 */
public class WatchCommand implements Command {
	public void run(String[] args) {

		Scanner scanner = new Scanner(System.in);

		WatchThread watchThread = new WatchThread();
		watchThread.running = true;
		watchThread.start();

		if(scanner.hasNext())
			watchThread.running = false;
	}

	public String getName() {
		return "watch";
	}

	public List<String> getArgs() {
		return new ArrayList<>();
	}

	public String getDescription() {
		return "Runs the game.";
	}
}

class WatchThread extends Thread{
	public boolean running = true;

	@Override
	public void run(){
		while (running){

			for (int i = 0; i < 60; i++) {
				Game.game.tick(16);
			}
			ColdBootRepl.print();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			}
		}
	}
}