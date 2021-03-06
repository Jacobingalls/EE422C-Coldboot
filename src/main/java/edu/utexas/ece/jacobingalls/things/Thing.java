package edu.utexas.ece.jacobingalls.things;

import edu.utexas.ece.jacobingalls.ColdBootGui;
import edu.utexas.ece.jacobingalls.Game;
import edu.utexas.ece.jacobingalls.player.Team;
import edu.utexas.ece.jacobingalls.things.buildings.Building;
import edu.utexas.ece.jacobingalls.things.robots.Robot;
import edu.utexas.ece.jacobingalls.utils.Tuple;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public abstract class Thing {

	protected Team team;

	private double health = 100;
	private double maxHeath = 100;

	private boolean beingHealed = false;

	private double x;
	private double y;

	private boolean hovering = false;
	private boolean selected = false;

	private double energyInitialCost = 100;

	private double energyRunningCost = 1;
	private double mass = 10;

	private boolean powered = false;

	public Thing(Team team) {
		super();
		this.team = team;
	}

	public boolean isHovered() { return hovering; }
	public Thing setHovering(boolean hovering) { this.hovering = hovering; return this; }

	public boolean isSelected() { return selected; }
	public Thing setSelected(boolean selected) { this.selected = selected; return this; }

	public double getXCenter(){return getWidth()/2 + getX();}
	public double getYCenter(){return getHeight()/2 + getY();}

	public double getXCenterViewport(){return getXCenter() + ColdBootGui.viewportX;}
	public double getYCenterViewport(){return getYCenter() + ColdBootGui.viewportY;}

	public Thing setXCenter(double x){ this.x = x - getWidth()/2; return this;}
	public Thing setYCenter(double y){ this.y = y - getHeight()/2; return this;}

	public Thing setX(double x){this.x = x; return this; };
	public Thing setY(double y){this.y = y; return this; };

	public double getX(){return x;}
	public double getY(){return y;}

	public double getXViewport(){return x + ColdBootGui.viewportX;}
	public double getYViewport(){return y + ColdBootGui.viewportY;}

	public abstract double getWidth();
	public abstract double getHeight();

	public abstract void render(GraphicsContext gc);
	public abstract void tick(long time_elapsed);

	public abstract boolean isColliding(double x, double y);
	public boolean isCollidingRoughBox(double x, double y){
		return  (x >= getX() && x <= (getX() + getWidth())) &&
				(y >= getY() && y <= (getY() + getHeight()));
	};

	protected abstract boolean clicked(double x, double y);

	public boolean click(double x, double y) {
		return isCollidingRoughBox(x, y) && clicked(x, y);
	}

	public void damage(double x, double y,double damage){setHealth(getHealth() - damage);}

	private boolean hasDied = false;
	public double getHealth() {
		if(health <= 0 && !hasDied){ died(); }
		return health;
	}

	public Thing setHealth(double health) {
		this.health = health;
		if(health <= 0 && !hasDied){ died(); }
		return this;
	}

	public double getHealthPercentage() {
		return health/maxHeath;
	}


	protected void died(){
		hasDied = true;
	}

	protected double lineLength(double x1, double y1, double x2, double y2){
		return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}

	protected Point2D radialToCartesian(double angle, double magnitude){
		double x = magnitude * Math.cos(angle * Math.PI / 180.0);
		double y = -magnitude * Math.sin(angle * Math.PI / 180.0);
		return new Point2D(x,y);
	}

	public Thing getClosestEnemy(){
		Queue<Thing> al = new ConcurrentLinkedQueue<>();
		Game.game.getThings().parallelStream()
				.filter(thing -> thing instanceof Robot)
				.filter(thing -> !team.equals(thing.team))
				.map(thing -> ((Robot) thing).getBlocks())
				.collect(Collectors.toList())
				.forEach(al::addAll);

		Game.game.getThings().parallelStream()
				.filter(thing -> thing instanceof Building)
				.filter(thing -> !team.equals(thing.team))
				.forEach(al::add);

		if(!al.isEmpty()){
			Point2D p = getPoint2D();
			return al.parallelStream()
					.filter(thing -> thing != null)
					.map(thing -> new Tuple<>(thing.getPoint2D().distance(p), thing))
//					.collect(Collectors.toList())
//					.parallelStream()
					.sorted((a, b) -> a.a.compareTo(b.a))
					.findFirst()
					.get().b;
		} else
		return null;
	}

	public Point2D getPoint2D(){return new Point2D(getXCenter(), getYCenter()); }
	public double getMaxHeath() {return maxHeath;}
	public Thing setMaxHeath(double maxHeath) {this.maxHeath = maxHeath; return this; }
	public Team getTeam() { return team; }
	public Thing setTeam(Team team) { this.team = team; return this; }
	public boolean isHovering() {return hovering; }

	public double getEnergyInitialCost() {
		return energyInitialCost;
	}

	public Thing setEnergyInitialCost(double energyInitialCost) {
		this.energyInitialCost = energyInitialCost;
		return this;
	}

	public boolean isBeingHealed() {
		return beingHealed;
	}

	public Thing setBeingHealed(boolean beingHealed) {
		this.beingHealed = beingHealed;
		return this;
	}

	public double getEnergyRunningCost() {
		return energyRunningCost;
	}

	public Thing setEnergyRunningCost(double energyRunningCost) {
		this.energyRunningCost = energyRunningCost;
		return this;
	}

	public boolean isPowered() {
		return powered;
	}

	public Thing setPowered(boolean powered) {
		this.powered = powered;
		return this;
	}

	public double getMass() {
		return mass;
	}

	public Thing setMass(double mass) {
		this.mass = mass;
		return this;
	}
}
