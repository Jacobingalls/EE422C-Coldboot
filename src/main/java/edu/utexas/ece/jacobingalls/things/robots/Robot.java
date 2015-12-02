package edu.utexas.ece.jacobingalls.things.robots;

import edu.utexas.ece.jacobingalls.ColdBootGui;
import edu.utexas.ece.jacobingalls.Game;
import edu.utexas.ece.jacobingalls.player.Team;
import edu.utexas.ece.jacobingalls.things.TargetBasedMovingThing;
import edu.utexas.ece.jacobingalls.things.blocks.Block;
import edu.utexas.ece.jacobingalls.things.blocks.CPUBlock;
import edu.utexas.ece.jacobingalls.things.blocks.MotorBlock;
import edu.utexas.ece.jacobingalls.things.blocks.ReactorBlock;
import edu.utexas.ece.jacobingalls.things.Thing;
import edu.utexas.ece.jacobingalls.utils.Tuple;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 *
 */
public class Robot extends TargetBasedMovingThing {
	double tick = 0;

	double selectSeconds = 0;
	double selectSecondThreshold = .25;
	double selectMax = 10;
	double selectMin = 3;

	double width;
	double height;

	boolean wasDamaged = false;

	double currentEnergyCost = 0;
	double currentEnergyProduction = 0;
	double currentMass = 0;
	double currentTorque = 0;

	private List<Block> blocks = new CopyOnWriteArrayList<>();


	/**
	 * Creates a new robot with a partuclar team
	 * @param team the team to set the robot to
	 */
	public Robot(Team team){
		super(team);
	}

	public Robot(){
		this(Team.NO_TEAM);
	}

	/**
	 * Modifies the robot by adding the specified blocks
	 *
	 * @param blocks the blocks to add the the robot
	 * @return the robot with the blocks added
	 */
	public Robot addBlocks(List<Block> blocks){
		blocks.parallelStream().forEach(block -> {
			block.setTeam(team);
		});

		this.blocks.addAll(blocks);
		recalculateDimensions();
		recalculateEnergy();
		recalculateMass();
		return this;
	}
	public Robot addBlock(Block... blocks){
		for(Block b: blocks){
			b.setTeam(team);
		}

		Collections.addAll(this.blocks, blocks);
		recalculateDimensions();
		recalculateEnergy();
		recalculateMass();
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

	private void recalculateEnergy(){
		currentEnergyCost = blocks.parallelStream().mapToDouble(Thing::getEnergyRunningCost).sum();
		currentEnergyProduction = blocks.parallelStream().filter(block -> block instanceof ReactorBlock)
				.mapToDouble(block -> ((ReactorBlock)block).getEnergyProduction()).sum() + 5;

		if(getEnergyUtilization() <= 0) {
			this.blocks.forEach(block -> block.setPowered(false));
		} else if(getEnergyUtilization() <= 1){
			this.blocks.forEach(block -> block.setPowered(true));
		} else if(getEnergyUtilization() > 0) {
			this.blocks.forEach(block -> block.setPowered(true));
			Random r = new Random();
			this.blocks.parallelStream()
					.map(block -> new Tuple<>(r.nextInt(), block))
					.sorted((a,b) -> a.a.compareTo(b.a))
					.limit(Math.round(this.blocks.size() * (getEnergyUtilization() - 1)))
					.forEach(block -> block.b.setPowered(false));
		}
	}

	private void recalculateMass(){
		currentMass = blocks.parallelStream().mapToDouble(Thing::getMass).sum();
		List<MotorBlock> motors = blocks.parallelStream().filter(block -> block instanceof MotorBlock)
				.map(block -> ((MotorBlock) block)).collect(Collectors.toList());

		currentTorque = motors.parallelStream().mapToDouble(MotorBlock::getStrength).sum();

		double maxSpeed = (500.0 * motors.size())/(blocks.size()/2);
		double maxAcceleration = (1 - getTorqueUtilization()/4) * maxSpeed * 2;

		if( getTorqueUtilization() < 0){
			maxAcceleration = 10;
			maxSpeed = 50;
		} else if(getTorqueUtilization() > 2) {
			maxAcceleration = 0;
		} else if(getTorqueUtilization() > 1) {
			maxAcceleration = (maxSpeed - (getTorqueUtilization() - 1)*maxSpeed);
		}

		setAcceleration(maxAcceleration);
		setMaxVelocity(maxSpeed);
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

			if(Game.game.getPlayer().equals(getTeam())) {
				double tgtX = getTargetLocationX() + ColdBootGui.viewportX;
				double tgtY = getTargetLocationY() + ColdBootGui.viewportY;
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
		}
	}

	@Override
	public void tick(long time_elapsed) {
		super.tick(time_elapsed);

		this.blocks.forEach(block -> block.setRobot(this));
		getBlocks().parallelStream().forEach(block -> block.setBeingHealed(false));

		tick =(tick + time_elapsed) % 1000;
		if(tick <= time_elapsed){
			recalculateEnergy();
			recalculateMass();
		}

		if(wasDamaged) {
			blocks = blocks.parallelStream().filter(thing -> thing.getHealth() > 0).collect(Collectors.toList());
			if(blocks.size() == 0)
				setHealth(-1);
			recalculateDimensions();
			recalculateEnergy();
			recalculateMass();
			wasDamaged = false;

			if(blocks.parallelStream().filter(block -> block instanceof CPUBlock).count() <= 0)
				setHealth(-1);
		}

		if((isHovered() || isSelected()) && selectSeconds >= selectSecondThreshold){
			selectSeconds = selectSecondThreshold;
		} else if(isHovered() || isSelected()) {
			selectSeconds += time_elapsed / 1000.0;
		} else if(selectSeconds < 0){
			selectSeconds = 0;
		} else if(selectSeconds > 0){
			selectSeconds -= time_elapsed/1000.0;
		}

		blocks.parallelStream().forEach(block -> block
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
		Optional<Block> ob = blocks.parallelStream().filter(block -> block.isCollidingRoughBox(ColdBootGui.mouseX, ColdBootGui.mouseY)).findFirst();
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

	@Override
	public double getEnergyInitialCost(){
		double me = super.getEnergyInitialCost();
		double blockCost = blocks.parallelStream().mapToDouble(Block::getEnergyInitialCost).sum();

		return me + blockCost;
	}

	public double getCurrentEnergyCost() {
		return currentEnergyCost;
	}

	public Robot setCurrentEnergyCost(double currentEnergyCost) {
		this.currentEnergyCost = currentEnergyCost;
		return this;
	}

	public double getCurrentEnergyProduction() {
		return currentEnergyProduction;
	}

	public Robot setCurrentEnergyProduction(double currentEnergyProduction) {
		this.currentEnergyProduction = currentEnergyProduction;
		return this;
	}

	public double getEnergyUtilization() {
		if(currentEnergyProduction > 0)
			return currentEnergyCost/currentEnergyProduction;
		else
			return -1;
	}

	public double getCurrentTorque() {
		return currentTorque;
	}

	public Robot setCurrentTorque(double currentTorque) {
		this.currentTorque = currentTorque;
		return this;
	}

	public double getCurrentMass() {
		return currentMass;
	}

	public Robot setCurrentMass(double currentMass) {
		this.currentMass = currentMass;
		return this;
	}

	public  double getTorqueUtilization(){
		if(currentTorque > 0)
			return currentMass/currentTorque;
		else
			return -1;
	}
}
