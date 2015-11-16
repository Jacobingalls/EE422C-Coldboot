package edu.utexas.ece.jacobingalls;

import edu.utexas.ece.jacobingalls.robots.Robot;
import edu.utexas.ece.jacobingalls.robots.blocks.Block;
import edu.utexas.ece.jacobingalls.robots.gui.ProgressBar;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;

public class RightSideBar {

	double width = 200;
	double height = 400;

	double x = 500;
	double y = 0;

	Thing selectedThing = null;

	double offset = height;

	public void render(GraphicsContext gc){
		double dis_x = x + offset;

		gc.setFill(Color.rgb(0, 0, 0, .8));
		gc.fillRect(dis_x, 0, width, height);

		if(selectedThing == null){
			gc.setStroke(Color.rgb(50, 50, 50));
			gc.setFill(Color.GRAY);
		} else {
			gc.setStroke(selectedThing.team.getTeamColor());
			gc.setFill(selectedThing.team.getTeamColor());
		}
		gc.strokeLine(dis_x,y, dis_x, y+height);


		renderGeneralList(gc, dis_x, 0);
		renderDamagedList(gc, dis_x, 100);
	}

	private void renderGeneralList(GraphicsContext gc, double dis_x, double dis_y){
		gc.fillText("Overall", dis_x + 10, y + 15 + dis_y);
		if(selectedThing != null && selectedThing instanceof Robot) {
			gc.fillText("Heath", dis_x + 10, y + dis_y + 32);
			new ProgressBar(selectedThing.team, (((Robot)selectedThing).getRealHealth()) / 100, dis_x + width - 100, dis_y + 22, 100, 10)
					.setTrigger(.5)
					.render(gc);

			gc.fillText("CPU Heath", dis_x + 10, y + dis_y + 32+20);
			new ProgressBar(selectedThing.team, (((Robot)selectedThing).getCPUHealth()) / 100, dis_x + width - 100, dis_y + 42, 100, 10)
					.setTrigger(.5)
					.render(gc);
		}
	}

	private void renderDamagedList(GraphicsContext gc, double dis_x, double dis_y){
		if(selectedThing != null) {
			gc.fillText("Most Damaged", dis_x + 10, y + 15 + dis_y);
			if(selectedThing instanceof Robot){
				Robot robot = (Robot)selectedThing;
				List<Block> blocks = robot.getBlocks().parallelStream().sorted((a,b) -> (int)(a.getHealth() - b.getHealth())).limit(5).collect(Collectors.toList());
				for (int i = 0; i < blocks.size(); i++) {
					gc.fillText(blocks.get(i).getRobotX()+"x"+blocks.get(i).getRobotY(), dis_x + 10, y + dis_y + 32+20*i);
					gc.fillText(blocks.get(i).getClass().getSimpleName(), dis_x + 50, y + dis_y + 32+20*i);
					new ProgressBar(selectedThing.team,(blocks.get(i).getHealth())/100, dis_x + width - 50,  dis_y+ 22 + 20*i, 50, 10)
							.setTrigger(.5)
							.render(gc);
				}
			}
		}
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
