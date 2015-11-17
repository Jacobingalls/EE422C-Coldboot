package edu.utexas.ece.jacobingalls;

import edu.utexas.ece.jacobingalls.buildings.Building;
import edu.utexas.ece.jacobingalls.buildings.Factory;
import edu.utexas.ece.jacobingalls.robots.Robot;
import edu.utexas.ece.jacobingalls.robots.blocks.Block;
import edu.utexas.ece.jacobingalls.robots.gui.Button;
import edu.utexas.ece.jacobingalls.robots.gui.ProgressBar;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RightSideBar {

	double width = 200;
	double height = 400;

	double x = 500;
	double y = 0;

	public boolean wasClicked = false;

	Thing selectedThing = null;

	double offset = height;

	public void render(GraphicsContext gc) {
		double dis_x = x + offset;

		gc.setFill(Color.rgb(0, 0, 0, .8));
		gc.fillRect(dis_x, 0, width, height);

		setColor(gc);
		gc.strokeLine(dis_x, y, dis_x, y + height);

		if (selectedThing != null) {
			if (selectedThing instanceof Robot) {
				renderRobotGeneralList(gc, dis_x, 0);
				renderRobotDamagedList(gc, dis_x, 100);
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
			gc.setStroke(selectedThing.team.getTeamColor());
			gc.setFill(selectedThing.team.getTeamColor());
		}
	}

	private void renderRobotGeneralList(GraphicsContext gc, double dis_x, double dis_y){
		setColor(gc);
		gc.fillText("Overall", dis_x + 10, y + 15 + dis_y);
		gc.fillText("Heath", dis_x + 10, y + dis_y + 32);
		new ProgressBar(selectedThing.team, (((Robot)selectedThing).getRealHealth()) / 100, dis_x + width - 110, dis_y + 22, 100, 10)
				.setTrigger(.5)
				.render(gc);

		gc.fillText("CPU Heath", dis_x + 10, y + dis_y + 32 + 20);
		new ProgressBar(selectedThing.team, (((Robot)selectedThing).getCPUHealth()) / 100, dis_x + width - 110, dis_y + 42, 100, 10)
				.setTrigger(.5)
				.render(gc);

		gc.fillText("Power Util.", dis_x + 10, y + dis_y + 52 + 20);
		double util = ((Robot)selectedThing).getEnergyUtilization();
		new ProgressBar(selectedThing.team, util, dis_x + width - 110, dis_y + 62, 100, 10)
				.setTiggerDirection(false)
				.setTrigger(.75)
				.render(gc);

	}
	private void renderRobotDamagedList(GraphicsContext gc, double dis_x, double dis_y){
		setColor(gc);
		gc.fillText("Most Damaged", dis_x + 10, y + 15 + dis_y);

		Robot robot = (Robot)selectedThing;
		List<Block> blocks = robot.getBlocks().parallelStream()
//					.filter(block -> block.getHealthPercentage() < 1)
				.sorted((a, b) -> (int) (a.getHealth() - b.getHealth())).limit(5)
				.collect(Collectors.toList());
		for (int i = 0; i < blocks.size(); i++) {
			Block b = blocks.get(i);
			setColor(gc);
			if(App.player.getTeam().equals(selectedThing.getTeam())) {
				if(b.isBeingHealed())
					gc.setFill(b.team.getTeamAlternate2Color());

				gc.fillText(b.getRobotX() + "x" + blocks.get(i).getRobotY(), dis_x + 10, y + dis_y + 32 + 20 * i);
				gc.fillText(b.getClass().getSimpleName(), dis_x + 50, y + dis_y + 32 + 20 * i);

				new ProgressBar(selectedThing.team, (b.getHealth()) / 100, dis_x + width - 60, dis_y + 22 + 20 * i, 50, 10)
//						.setText(blocks.get(i).isBeingHealed() ? "H" : "")
						.setTrigger(.5)
						.render(gc);
			} else {
				Random r = new Random();
				gc.fillText(r.nextInt(9) + "x" + r.nextInt(9), dis_x + 10+5*r.nextDouble(), y + 5*r.nextDouble()+ dis_y + 32 + 20 * i);
				String s = "" + ((char)(r.nextInt(27)+40))+ ((char)(r.nextInt(27)+40))+ ((char)(r.nextInt(27)+40))+ ((char)(r.nextInt(27)+40))+ ((char)(r.nextInt(27)+40))+ ((char)(r.nextInt(27)+40));

				gc.fillText(s, dis_x + 50+5*r.nextDouble(), y + dis_y + 32 + 5*r.nextDouble() + 20 * i);
				new ProgressBar(selectedThing.team, r.nextDouble(), dis_x + width - 60, dis_y + 22 + 20 * i, 50, 10)
						.setTrigger(.5)
						.render(gc);
			}
		}
	}

	private void renderBuildingGeneralInfo(GraphicsContext gc, double dis_x, double dis_y){
		setColor(gc);
		gc.fillText("Overall", dis_x + 10, y + 15 + dis_y);
		gc.fillText("Heath", dis_x + 10, y + dis_y + 32);
		new ProgressBar(selectedThing.team, (selectedThing.getHealth()) / 100, dis_x + width - 110, dis_y + 22, 100, 10)
				.setTrigger(.5)
				.render(gc);
	}

	private void renderFactoryInfo(GraphicsContext gc, double dis_x, int dis_y) {
		setColor(gc);
		gc.fillText("Factory Status", dis_x + 10, y + 15 + dis_y);
		gc.fillText("Completion", dis_x + 10, y + dis_y + 32);
		new ProgressBar(selectedThing.team, ((Factory)selectedThing).getBuildPercentage(), dis_x + width - 110, dis_y + 22, 100, 10)
				.setTiggerDirection(false)
				.setTrigger(.9)
				.render(gc);

		new Button(selectedThing.team, "Add to Queue", dis_x + 10, dis_y + 40, 100, 20).handlePossibleClick(wasClicked, button -> {
			Factory f = (Factory)selectedThing;
			f.setNumberToBuild(f.getNumberToBuild() + 1);
		}).render(gc);
	}

	public void tick(long time_elapsed){
		if(time_elapsed <= 0)
			time_elapsed = 1;
		x = App.world_width-width;
		height = App.world_height;

		List<Thing> thingList = App.things.parallelStream().filter(Thing::isSelected).collect(Collectors.toList());
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

}
