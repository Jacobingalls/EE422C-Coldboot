package edu.utexas.ece.jacobingalls.gui;

import edu.utexas.ece.jacobingalls.App;
import edu.utexas.ece.jacobingalls.robots.Thing;
import edu.utexas.ece.jacobingalls.buildings.Building;
import edu.utexas.ece.jacobingalls.buildings.Factory;
import edu.utexas.ece.jacobingalls.robots.Robot;
import edu.utexas.ece.jacobingalls.robots.blocks.Block;
import edu.utexas.ece.jacobingalls.utils.Tuple;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RightSideBar {

	private double width = 200;
	private double height = 400;

	private double x = 500;
	private double y = 0;

	public boolean wasClicked = false;

	private Thing selectedThing = null;

	private double offset = height;

	public void render(GraphicsContext gc) {
		double dis_x = x + offset;

		gc.setFill(Color.rgb(0, 0, 0, .8));
		gc.fillRect(dis_x, 0, width, height);

		setColor(gc);
		gc.strokeLine(dis_x, y, dis_x, y + height);

		if (selectedThing != null) {
			if (selectedThing instanceof Robot) {
				renderRobotGeneralList(gc, dis_x, 0);
				renderRobotDamagedList(gc, dis_x, 140);
			} else if(selectedThing instanceof Building){
				renderBuildingGeneralInfo(gc, dis_x, 0);

				if(selectedThing instanceof Factory){
					renderFactoryInfo(gc, dis_x, 40);
				}


			}
		} else {
			gc.fillText("Nothing Selected", dis_x + 10, y + 15);
		}
		wasClicked = false;
	}

	private void setColor(GraphicsContext gc){
		if (selectedThing == null) {
			gc.setStroke(Color.rgb(50, 50, 50));
			gc.setFill(Color.GRAY);
		} else {
			gc.setStroke(selectedThing.getTeam().getTeamColor());
			gc.setFill(selectedThing.getTeam().getTeamColor());
		}
	}

	private void renderRobotGeneralList(GraphicsContext gc, double dis_x, double dis_y){
		setColor(gc);
		gc.fillText("Overall", dis_x + 10, y + 15 + dis_y);
		gc.fillText("Heath", dis_x + 10, y + dis_y + 32);
		new ProgressBar(selectedThing.getTeam(), (((Robot)selectedThing).getRealHealth()) / 100, dis_x + width - 110, dis_y + 22, 100, 10)
				.setTrigger(.5)
				.render(gc);

		setColor(gc);
		gc.fillText("CPU Heath", dis_x + 10, y + dis_y + 32 + 20);
		new ProgressBar(selectedThing.getTeam(), (((Robot)selectedThing).getCPUHealth()) / 100, dis_x + width - 110, dis_y + 42, 100, 10)
				.setTrigger(.5)
				.render(gc);

		setColor(gc);
		gc.fillText("Power Util.", dis_x + 10, y + dis_y + 52 + 20);
		double util = ((Robot)selectedThing).getEnergyUtilization();
		new ProgressBar(selectedThing.getTeam(), util, dis_x + width - 110, dis_y + 62, 100, 10)
				.setTiggerDirection(false)
				.setTrigger(.75)
				.render(gc);

		setColor(gc);
		gc.fillText("Torque Util.", dis_x + 10, y + dis_y + 52 + 40);
		util = ((Robot)selectedThing).getTorqueUtilization();
		new ProgressBar(selectedThing.getTeam(), util, dis_x + width - 110, dis_y + 82, 100, 10)
				.setTiggerDirection(false)
				.setTrigger(.75)
				.render(gc);

		setColor(gc);
		gc.fillText("Velocity Util.", dis_x + 10, y + dis_y + 52 + 60);
		util = ((Robot)selectedThing).getVelocityUtilization();
		new ProgressBar(selectedThing.getTeam(), util, dis_x + width - 110, dis_y + 102, 100, 10)
				.setTiggerDirection(false)
				.setTrigger(1.01)
				.render(gc);




	}
	private void renderRobotDamagedList(GraphicsContext gc, double dis_x, double dis_y){
		setColor(gc);
		gc.fillText("Most Damaged", dis_x + 10, y + 15 + dis_y);

		Robot robot = (Robot)selectedThing;
		List<Block> blocks = robot.getBlocks().parallelStream()
				.map(block -> new Tuple<>(block.getHealth(), block))
				.sorted((a, b) -> a.a.compareTo(b.a)).limit(5)
				.map(tuple -> tuple.b)
				.collect(Collectors.toList());

		for (int i = 0; i < blocks.size(); i++) {
			Block b = blocks.get(i);
			setColor(gc);
			if(App.player.getTeam().equals(selectedThing.getTeam())) {
				if(b.isBeingHealed())
					gc.setFill(b.getTeam().getTeamAlternate2Color());

				gc.fillText(b.getRobotX() + "x" + blocks.get(i).getRobotY(), dis_x + 10, y + dis_y + 32 + 20 * i);
				gc.fillText(b.getClass().getSimpleName(), dis_x + 50, y + dis_y + 32 + 20 * i);

				new ProgressBar(selectedThing.getTeam(), (b.getHealth()) / 100, dis_x + width - 60, dis_y + 22 + 20 * i, 50, 10)
//						.setText(blocks.get(i).isBeingHealed() ? "H" : "")
						.setTrigger(.5)
						.render(gc);
			} else {
				Random r = new Random();
				gc.fillText(r.nextInt(9) + "x" + r.nextInt(9), dis_x + 10+5*r.nextDouble(), y + 5*r.nextDouble()+ dis_y + 32 + 20 * i);
				String s = "" + ((char)(r.nextInt(27)+40))+ ((char)(r.nextInt(27)+40))+ ((char)(r.nextInt(27)+40))+ ((char)(r.nextInt(27)+40))+ ((char)(r.nextInt(27)+40))+ ((char)(r.nextInt(27)+40));

				gc.fillText(s, dis_x + 50+5*r.nextDouble(), y + dis_y + 32 + 5*r.nextDouble() + 20 * i);
				new ProgressBar(selectedThing.getTeam(), r.nextDouble(), dis_x + width - 60, dis_y + 22 + 20 * i, 50, 10)
						.setTrigger(.5)
						.render(gc);
			}
		}
	}

	private void renderBuildingGeneralInfo(GraphicsContext gc, double dis_x, double dis_y){
		setColor(gc);
		gc.fillText("Overall", dis_x + 10, y + 15 + dis_y);
		gc.fillText("Heath", dis_x + 10, y + dis_y + 32);
		new ProgressBar(selectedThing.getTeam(), (selectedThing.getHealth()) / 100, dis_x + width - 110, dis_y + 22, 100, 10)
				.setTrigger(.5)
				.render(gc);
	}

	private void renderFactoryInfo(GraphicsContext gc, double dis_x, int dis_y) {
		setColor(gc);
		gc.fillText("Factory Status", dis_x + 10, y + 15 + dis_y);
		gc.fillText("Completion", dis_x + 10, y + dis_y + 32);
		new ProgressBar(selectedThing.getTeam(), ((Factory)selectedThing).getBuildPercentage(), dis_x + width - 110, dis_y + 22, 100, 10)
				.setTiggerDirection(false)
				.setTrigger(.9)
				.render(gc);

		new Button(selectedThing.getTeam(), "Add to Queue", dis_x + 10, dis_y + 40, 100, 20).handlePossibleClick(wasClicked, button -> {
			Factory f = (Factory)selectedThing;
			f.setNumberToBuild(f.getNumberToBuild() + 1);
		}).render(gc);
	}

	public void tick(long time_elapsed){
		if(time_elapsed <= 0)
			time_elapsed = 1;
		x = App.world_width-width;
		height = App.world_height;

		List<Thing> thingList = App.getGame().getThings().parallelStream().filter(Thing::isSelected).collect(Collectors.toList());
		if(thingList.size() != 1)
			selectedThing = null;
		else selectedThing = thingList.get(0);

		if(selectedThing != null){
			offset -= 200/time_elapsed;
			if(offset < 0)
				offset = 0;
		} else {
			offset += 200/time_elapsed;
			if(offset > width+1)
				offset = width+1;
		}
	}

	public Thing getSelectedThing() {
		return selectedThing;
	}

	public RightSideBar setSelectedThing(Thing selectedThing) {
		this.selectedThing = selectedThing;
		return this;
	}

	public double getWidth() {
		return width;
	}

	public RightSideBar setWidth(double width) {
		this.width = width;
		return this;
	}

	public double getHeight() {
		return height;
	}

	public RightSideBar setHeight(double height) {
		this.height = height;
		return this;
	}

	public double getX() {
		return x;
	}

	public RightSideBar setX(double x) {
		this.x = x;
		return this;
	}

	public double getY() {
		return y;
	}

	public RightSideBar setY(double y) {
		this.y = y;
		return this;
	}

	public double getOffset() {
		return offset;
	}

	public RightSideBar setOffset(double offset) {
		this.offset = offset;
		return this;
	}
}
