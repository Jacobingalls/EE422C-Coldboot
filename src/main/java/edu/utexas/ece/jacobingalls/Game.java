package edu.utexas.ece.jacobingalls;

import edu.utexas.ece.jacobingalls.gui.RightSideBar;
import edu.utexas.ece.jacobingalls.robots.Thing;
import edu.utexas.ece.jacobingalls.robots.projectiles.Projectile;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class Game {

	private List<Thing> things = new LinkedList<>();
	private Queue<Thing> thingsWaiting = new ConcurrentLinkedQueue<>();

	private RightSideBar rightSideBar = new RightSideBar();

	public void tick(long time_elapsed){
		// If there are things waiting to be added the the game, add them.
		while (!thingsWaiting.isEmpty())
			things.add(thingsWaiting.poll());

		// Run the tick method on all of the things
		things.forEach(thing -> thing.tick(time_elapsed));

		// Check to see if a projectile is damaging a robot, then damage it if required.
		things.parallelStream().filter(thing -> thing instanceof Projectile)
				.map(thing -> (Projectile)thing)
				.forEach(projectile -> {
					List<Thing> hitThings = things.parallelStream().filter(thing -> thing.getHealth() > 0)
							.filter(thing -> !thing.getTeam().equals(projectile.getTeam()))
							.filter(thing -> thing.isColliding(projectile.getX(), projectile.getY()))
							.collect(Collectors.toList());
					if(!hitThings.isEmpty()){
						projectile.setHealth(-1);
						hitThings.forEach(thing -> thing.damage(projectile.getX(), projectile.getY(), projectile.getDamage()));
					}
				});

		// Remove all things that died this game tick
		things = things.parallelStream().filter(thing -> thing.getHealth() > 0).collect(Collectors.toList());

		// Update the sidebar
		rightSideBar.tick(time_elapsed);
	}

	public List<Thing> getThings() {
		return things;
	}

	public Game setThings(List<Thing> things) {
		this.things = things;
		return this;
	}

	public Queue<Thing> getThingsWaiting() {
		return thingsWaiting;
	}

	public Game setThingsWaiting(Queue<Thing> thingsWaiting) {
		this.thingsWaiting = thingsWaiting;
		return this;
	}

	public RightSideBar getRightSideBar() {
		return rightSideBar;
	}

	public Game setRightSideBar(RightSideBar rightSideBar) {
		this.rightSideBar = rightSideBar;
		return this;
	}
}