package edu.utexas.ece.jacobingalls;

import edu.utexas.ece.jacobingalls.things.buildings.RobotFactory;
import edu.utexas.ece.jacobingalls.things.buildings.TeamBase;
import edu.utexas.ece.jacobingalls.gui.RightSideBar;
import edu.utexas.ece.jacobingalls.player.Player;
import edu.utexas.ece.jacobingalls.things.robots.AIRobot;
import edu.utexas.ece.jacobingalls.things.robots.Blueprint;
import edu.utexas.ece.jacobingalls.things.Thing;
import edu.utexas.ece.jacobingalls.things.robots.particles.Particle;
import edu.utexas.ece.jacobingalls.things.robots.projectiles.Projectile;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class Game {

	public static Game game;

	private int worldWidth = 800;
	private int worldHeight = 600;

	private Player player;
	private Player aiplayer;

	private List<Thing> things = new LinkedList<>();
	private Queue<Thing> thingsWaiting = new ConcurrentLinkedQueue<>();

	private RightSideBar rightSideBar = new RightSideBar();

	public Game(Player player, Player aiplayer){
		this.player = player;
		this.aiplayer = aiplayer;
	}

	public void tick(long time_elapsed){
		// If there are things waiting to be added the the game, add them.
		while (!thingsWaiting.isEmpty())
			things.add(thingsWaiting.poll());

		// Tick the players
		getPlayer().tick(time_elapsed);
		getAiplayer().tick(time_elapsed);

		// Run the tick method on all of the things
		things.forEach(thing -> thing.tick(time_elapsed));

		// Check to see if a projectile is damaging a robot, then damage it if required.
		things.parallelStream().filter(thing -> thing instanceof Projectile)
				.map(thing -> (Projectile)thing)
				.forEach(projectile -> {
					List<Thing> hitThings = things.parallelStream()
							.filter(thing -> !(thing instanceof Particle))
							.filter(thing -> thing.getHealth() > 0)
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

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getAiplayer() {
		return aiplayer;
	}

	public void setAiplayer(Player aiplayer) {
		this.aiplayer = aiplayer;
	}

	public void configureWorld() {
//		System.out.println("Configuring world.");
		Player player = getPlayer();
		double greenX = 10;
		getThings().add(new TeamBase(player).setX(greenX - 100));

		getThings().add(new RobotFactory(player, greenX + 100, 100, team ->
				Blueprint.SMALL_FIGHTER.build(AIRobot.class, team).get()
		).setNumberToBuild(3).setX(greenX).setY(10));

		getThings().add(new RobotFactory(player, greenX+100, 300, team ->
				Blueprint.MEDIUM_FIGHTER.build(AIRobot.class, team).get()
		).setNumberToBuild(5).setX(greenX).setY(300));

		getThings().add(new RobotFactory(player, greenX+100, 600, team ->
				Blueprint.BIG_GUN.build(AIRobot.class, team).get()
		).setNumberToBuild(3).setX(greenX).setY(600));


		Player aiplayer = getAiplayer();
		double redX = worldWidth -10;
		getThings().add(new TeamBase(aiplayer).setX(redX + 100));

		getThings().add(new RobotFactory(aiplayer, redX-100, 100, team ->
				Blueprint.SMALL_FIGHTER.build(AIRobot.class, team).get()
		).setNumberToBuild(3).setX(redX).setY(10));

		getThings().add(new RobotFactory(aiplayer, redX-100, 300, team ->
				Blueprint.MEDIUM_FIGHTER.build(AIRobot.class, team).get()
		).setNumberToBuild(5).setX(redX).setY(300));

		getThings().add(new RobotFactory(aiplayer, redX-100, 600, team ->
				Blueprint.BIG_GUN.build(AIRobot.class, team).get()
		).setNumberToBuild(3).setX(redX).setY(600));
	}

	public int getWorldWidth() {
		return worldWidth;
	}

	public void setWorldWidth(int worldWidth) {
		this.worldWidth = worldWidth;
	}

	public int getWorldHeight() {
		return worldHeight;
	}

	public void setWorldHeight(int worldHeight) {
		this.worldHeight = worldHeight;
	}
}
