package edu.utexas.ece.jacobingalls;

import edu.utexas.ece.jacobingalls.buildings.Building;
import edu.utexas.ece.jacobingalls.robots.Robot;
import edu.utexas.ece.jacobingalls.robots.blocks.Block;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import sun.awt.windows.ThemeReader;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jacobingalls on 11/12/15.
 */
public abstract class Thing {
	protected Team team;

	private double health = 100;
	private double maxHeath = 100;

	private double x;
	private double y;

	private boolean hovering = false;
	private boolean selected = false;

	public Thing(Team team){
		super();
		this.team = team;
	}

	public boolean isHovered() { return hovering; }
	public Thing setHovering(boolean hovering) { this.hovering = hovering; return this; }

	public boolean isSelected() { return selected; }
	public Thing setSelected(boolean selected) { this.selected = selected; return this; }

	public double getXCenter(){return getWidth()/2 + getX();}
	public double getYCenter(){return getHeight()/2 + getY();}

	public double getXCenterViewport(){return getXCenter() + App.viewportX;}
	public double getYCenterViewport(){return getYCenter() + App.viewportY;}

	public Thing setXCenter(double x){ this.x = x - getWidth()/2; return this;}
	public Thing setYCenter(double y){ this.y = y - getHeight()/2; return this;}

	public Thing setX(double x){this.x = x; return this; };
	public Thing setY(double y){this.y = y; return this; };

	public double getX(){return x;}
	public double getY(){return y;}

	public double getXViewport(){return x + App.viewportX;}
	public double getYViewport(){return y + App.viewportY;}

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

	public double getHealth() {
		return health;
	}

	public Thing setHealth(double health) {
		this.health = health;
		return this;
	}

	protected double lineLength(double x1, double y1, double x2, double y2){
		return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}

	protected Point2D radialToCartesian(double angle, double magnitude){
		double x = magnitude * Math.cos(angle * Math.PI / 180.0);
		double y = -magnitude * Math.sin(angle * Math.PI / 180.0);
		return new Point2D(x,y);
	}

	public Point2D getClosestEnemy(){
		ArrayList<Thing> al = new ArrayList<>();
		App.things.parallelStream()
				.filter(thing -> thing instanceof Robot)
				.filter(thing -> !team.equals(thing.team))
				.map(thing -> ((Robot) thing).getBlocks())
				.collect(Collectors.toList())
				.forEach(al::addAll);

		App.things.parallelStream()
				.filter(thing -> thing instanceof Building)
				.filter(thing -> !team.equals(thing.team))
				.forEach(al::add);

		if(!al.isEmpty()){
			Point2D p = getPoint2D();
			return al.parallelStream()
					.map(Thing::getPoint2D)
					.sorted((a, b) -> (int) (a.distance(p) - b.distance(p)))
					.findFirst()
					.get();
		} else
		return null;
	}

	public Point2D getPoint2D(){return new Point2D(getXCenter(), getYCenter()); }
	public double getMaxHeath() {return maxHeath;}
	public Thing setMaxHeath(double maxHeath) {this.maxHeath = maxHeath; return this; }
	public Team getTeam() { return team; }
	public Thing setTeam(Team team) { this.team = team; return this; }
	public boolean isHovering() {return hovering; }

}
