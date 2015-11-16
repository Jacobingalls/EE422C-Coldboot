package edu.utexas.ece.jacobingalls.robots;

import edu.utexas.ece.jacobingalls.App;
import edu.utexas.ece.jacobingalls.TargetBasedMovingThing;
import edu.utexas.ece.jacobingalls.Team;
import edu.utexas.ece.jacobingalls.Thing;
import edu.utexas.ece.jacobingalls.robots.blocks.Block;
import edu.utexas.ece.jacobingalls.robots.blocks.CPUBlock;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Robot extends TargetBasedMovingThing {
	double t = 0;

	double selectSeconds = 0;
	double selectSecondThreshold = .25;
	double selectMax = 10;
	double selectMin = 3;

	double width;
	double height;

	boolean wasDamaged = false;

	private List<Block> blocks = new ArrayList<>();


	public Robot(Team team){
		super(team);
	}

	public Robot addBlocks(List<Block> blocks){
		blocks.parallelStream().forEach(block -> {
			block.setTeam(team);
		});

		this.blocks.addAll(blocks);
		recalculateDimensions();
		return this;
	}
	public Robot addBlock(Block... blocks){
		for(Block b: blocks){
			b.setTeam(team);
		}

		Collections.addAll(this.blocks, blocks);
		recalculateDimensions();
		return this;
	}

	public List<Block> getBlocks(){return blocks;}

	private void recalculateDimensions(){
		int max_x = blocks.isEmpty()? 0 : blocks.get(0).getRobotX();
		int max_y = blocks.isEmpty()? 0 : blocks.get(0).getRobotY();
		int min_x = blocks.isEmpty()? 0 : blocks.get(0).getRobotX();
		int min_y = blocks.isEmpty()? 0 : blocks.get(0).getRobotY();

		for(Block b: blocks){
			if(max_x < b.getRobotX()){ max_x = b.getRobotX(); }
			if(min_x > b.getRobotX()){ min_x = b.getRobotX(); }

			if(max_y < b.getRobotY()){ max_y = b.getRobotY(); }
			if(min_y > b.getRobotY()){ min_y = b.getRobotY(); }
		}

		if(min_x != 0){
			int shift = -min_x;
			blocks.forEach(block -> block.setRobotX(block.getRobotX()+shift));
		}

		if(min_y != 0){
			int shift = -min_y;
			blocks.forEach(block -> block.setRobotY(block.getRobotY()+shift));
		}

		width =  max_x - min_x + 1;
		height = max_y - min_y + 1;
	}

	@Override
	public double getWidth() {
		return width * Block.size;
	}

	@Override
	public double getHeight() {
		return height * Block.size;
	}

	@Override
	public void render(GraphicsContext gc) {
		blocks.forEach(block -> block.render(gc));

		if(selectSeconds > 0) {
			double percent = selectSeconds / selectSecondThreshold;
			double spacing = selectMin + Math.abs((selectMax - selectMin) *  Math.cos(percent * Math.PI / 2));
			double spacingi = 1.5* (selectMax - spacing);

			Color p1 = team.getTeamAlternate1Color();
			if(isSelected()){ p1 = team.getTeamAlternate2Color();}

			gc.setStroke(p1.deriveColor(0,1,1,percent));
			gc.strokeRect(getXViewport() - spacing, getYViewport() - spacing, getWidth() + (spacing * 2), getHeight() + (spacing * 2));

			// center
			gc.strokeLine(getXCenterViewport()-spacingi, getYCenterViewport(), getXCenterViewport()+spacingi, getYCenterViewport());
			gc.strokeLine(getXCenterViewport(), getYCenterViewport()-spacingi, getXCenterViewport(), getYCenterViewport()+spacingi);

			if(App.player.getTeam().equals(getTeam())) {
				double tgtX = getTargetLocationX() + App.viewportX;
				double tgtY = getTargetLocationY() + App.viewportY;
				double len = lineLength(tgtX, tgtY, getXCenterViewport(), getYCenterViewport());
				if (Math.abs(len) > 1) {
					if (len <= 100)
						gc.setStroke(p1.deriveColor(0, 1, 1, len / 100));

					// destination
					gc.strokeLine(tgtX - spacingi, tgtY - spacingi, tgtX + spacingi, tgtY + spacingi);
					gc.strokeLine(tgtX + spacingi, tgtY - spacingi, tgtX - spacingi, tgtY + spacingi);

					// connecting line
					if (percent > .1) {

						double x_diff = tgtX - getXCenterViewport();
						double y_diff = tgtY - getYCenterViewport();
						gc.strokeLine(tgtX - (x_diff * (1 - percent)), tgtY - (y_diff * (1 - percent)), getXCenterViewport(), getYCenterViewport());
					}

				}
			}

//			Optional<Block> ob = blocks.parallelStream().filter(Thing::isHovered).findFirst();
//			if(ob.isPresent()){
//				Block b = ob.get();
//				gc.setStroke(p1.deriveColor(0,1,1,percent));
//				gc.strokeText(b.getClass().getSimpleName(), getX() + getWidth() + 10+spacing, getY()+8);
//				gc.strokeText("Health: "+Math.round(b.getHealth() * 100), getX() + getWidth() + 10+spacing, getY() + 20);
//			}

		}
	}

	@Override
	public void tick(long time_elapsed) {
		super.tick(time_elapsed);

		if(wasDamaged) {
			blocks = blocks.parallelStream().filter(thing -> thing.getHealth() > 0).collect(Collectors.toList());
			if(blocks.size() == 0)
				setHealth(-1);
			recalculateDimensions();
			wasDamaged = false;

			if(blocks.parallelStream().filter(block -> block instanceof CPUBlock).count() <= 0)
				setHealth(-1);
		}

		t=(t+time_elapsed/(double)10000)%1;

		if((isHovered() || isSelected()) && selectSeconds >= selectSecondThreshold){
			selectSeconds = selectSecondThreshold;
		} else if(isHovered() || isSelected()) {
			selectSeconds += time_elapsed / 1000.0;
		} else if(selectSeconds < 0){
			selectSeconds = 0;
		} else if(selectSeconds > 0){
			selectSeconds -= time_elapsed/1000.0;
		}

		blocks.forEach(block -> block
				.setX(getX() + block.getRobotX() * block.getWidth())
				.setY(getY() - (1 - height + block.getRobotY()) * block.getHeight())
				.tick(time_elapsed));
	}

	@Override
	public boolean isColliding(double x, double y) {
		return blocks.parallelStream().anyMatch(block -> block.isColliding(x,y));
	}

	@Override
	protected boolean clicked(double x, double y) {
		setSelected(!isSelected());
		return true;
	}

	@Override
	public Thing setHovering(boolean hovering) {
		super.setHovering(hovering);

		blocks.parallelStream().forEach(block -> block.setHovering(false));
		Optional<Block> ob = blocks.parallelStream().filter(block -> block.isCollidingRoughBox(App.mouseX, App.mouseY)).findFirst();
		if(ob.isPresent()){
			ob.get().setHovering(true);
		}
		return this;
	}

	@Override
	public void damage(double x, double y, double damage){
		wasDamaged = true;
		blocks.parallelStream().filter(block -> block.isColliding(x, y)).forEach(block -> block.damage(x, y, damage));
	}

	public double getRealHealth() {
		double h = 0;
		double mh = 0;
		for (Block block : blocks) {
			h += block.getHealth();
			mh += block.getMaxHeath();
		}
		return 100 * h / mh;
	}

	public double getCPUHealth(){
		double h = 0;
		double mh = 0;
		for (Block block : blocks) {
			if(block instanceof CPUBlock) {
				h += block.getHealth();
				mh += block.getMaxHeath();
			}
		}
		return 100 * h / mh;
	}
}
