package edu.utexas.ece.jacobingalls.robots.blocks;

import edu.utexas.ece.jacobingalls.Team;
import edu.utexas.ece.jacobingalls.Thing;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Block extends Thing {

	public long damageMillis = 0;
	public static double size = 20;

	private int robotX;
	private int robotY;

	private Color alarmColor;

	public Block(Team team) {
		super(team);
	}

	public Block() {
		this(Team.NO_TEAM);
	}

	public int getRobotX(){return robotX;}
	public int getRobotY(){return robotY;}

	public Block setRobotX(int robotX){this.robotX = robotX; return this;}
	public Block setRobotY(int robotY){this.robotY = robotY; return this;}
	public Block setRobotXY(int robotX, int robotY){return this.setRobotX(robotX).setRobotY(robotY);}


//	private Color color = Color.TRANSPARENT;
//	private Color outline = Color.TRANSPARENT;
//
//	public Block setColor(Color color){this.color = color; return this;}
//	public Block setOutline(Color outline){this.outline = outline; return this;}
//
//	public Color getColor(){return color;}
//	public Color getOutline(){return outline;}

	@Override
	public double getWidth() {
		return size;
	}

	@Override
	public double getHeight() {
		return size;
	}

	@Override
	public void render(GraphicsContext gc) {



		if(getHealth() > getMaxHeath()/2 || (int)(damageMillis / 100) % 2 != 0) {
			gc.setStroke(team.getTeamColor());
			gc.setFill(team.getTeamBackgroundColor());
		} else {
			if(alarmColor == null){
				alarmColor = team.getTeamAlternate2Color().darker().darker();
			}
			gc.setStroke(team.getTeamAlternate2Color());
			gc.setFill(alarmColor);
		}


		gc.fillRect(getXViewport(), getYViewport(), getWidth(), getHeight());
		gc.strokeRect(getXViewport(), getYViewport(), getWidth(), getHeight());



	}

	@Override
	public void tick(long time_elapsed) {
		damageMillis+=time_elapsed;
		damageMillis %= 1000;
	}

	@Override
	public boolean isColliding(double x, double y) {
		return  (x >= getX() && x <= (getX() + getWidth())) &&
				(y >= getY() && y <= (getY() + getHeight()));
	}

	@Override
	protected boolean clicked(double x, double y) {
		return false;
	}
}
