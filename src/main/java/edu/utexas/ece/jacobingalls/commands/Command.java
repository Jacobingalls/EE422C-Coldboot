package edu.utexas.ece.jacobingalls.commands;

import java.util.List;

public interface Command {
	void run(String[] args);
	String getName();
	List<String> getArgs();
	String getDescription();
}
