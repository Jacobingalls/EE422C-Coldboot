package edu.utexas.ece.jacobingalls.things.robots;

import edu.utexas.ece.jacobingalls.player.Team;
import edu.utexas.ece.jacobingalls.things.robots.blocks.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Blueprint {

	private String name;

	public static Blueprint BIG_GUN = new Blueprint("BigGun")
			.addBlock(CPUBlock.class,        0,  0)
			.addBlock(CPUBlock.class, 0, -1)
			.addBlock(MotorBlock.class, 1, 2)
			.addBlock(MotorBlock.class, 1, 3)
			.addBlock(MotorBlock.class, 1, -3)
			.addBlock(MotorBlock.class, 1, -4)
			.addBlock(MotorBlock.class, 0, 2)
			.addBlock(MotorBlock.class, 0, 3)
			.addBlock(MotorBlock.class, 0, -3)
			.addBlock(MotorBlock.class, 0, -4)
			.addBlock(MotorBlock.class, -1, 2)
			.addBlock(MotorBlock.class, -1, 3)
			.addBlock(MotorBlock.class, -1, -3)
			.addBlock(MotorBlock.class, -1, -4)

			.addBlock(MedBlock.class, 0, 1)
			.addBlock(MedBlock.class, 0, -2)
			.addBlockCol(ReactorBlock.class, 1, -2, 2)
			.addBlockCol(ReactorBlock.class, -1, -2, 2)
			.addBlockRec(GunBlock.class, 2, -4, 3, 8);

	public static Blueprint MEDIUM_FIGHTER = new Blueprint("MediumFighter")
			.addBlock(CPUBlock.class, 0, 0)
			.addBlock(ReactorBlock.class,0, 1)
			.addBlock(ReactorBlock.class, 0, -1)
			.addBlock(MotorBlock.class,0, 2)
			.addBlock(MotorBlock.class,0, -2)
			.addBlock(MedBlock.class,1, 0)
			.addBlock(ReactorBlock.class,-1, 0)
			.addBlock(GunBlock.class, -1, 2)
			.addBlock(GunBlock.class,1, 2)
			.addBlock(GunBlock.class,1, 1)
			.addBlock(GunBlock.class,-1, 1)
			.addBlock(GunBlock.class,-1, -2)
			.addBlock(GunBlock.class,1, -2)
			.addBlock(GunBlock.class,1, -1)
			.addBlock(GunBlock.class,-1, -1);

	public static Blueprint SMALL_FIGHTER = new Blueprint("SmallFighter")
			.addBlock(CPUBlock.class, 0, 0)
			.addBlock(MotorBlock.class, 0, 4)
			.addBlock(MotorBlock.class, 1, 4)
			.addBlock(GunBlock.class, 1, 1)
			.addBlock(GunBlock.class, 1, 2)
			.addBlock(GunBlock.class, 1, 3)
			.addBlock(MedBlock.class, 0, 1)
			.addBlock(ReactorBlock.class, 0, 2)
			.addBlock(ReactorBlock.class, 0, 3);




	public Blueprint(){name = "Unnamed "+new Random().nextInt(100);}
	public Blueprint(String name){this();setName(name);}

	private List<Supplier<Block>> blockSuppliers = new ArrayList<>();


	public Supplier<Robot> build(Supplier<Robot> robotSupplier){
		return () -> robotSupplier.get().addBlocks(blockSuppliers
				.parallelStream()
				.map(Supplier::get)
				.collect(Collectors.toList()));
	}


	public Supplier<Robot> build(Class<? extends Robot> robotClass, Team team){
		Supplier<Robot> robotSupplier = () -> {
			Robot robot = null;
			try {
				robot =  (Robot)robotClass.newInstance().setTeam(team);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				robot = new Robot(team);
			}
			return robot;
		};

		return build(robotSupplier);
	}

	@SafeVarargs
	public final Blueprint addBlocks(Supplier<Block>... suppliers){
		Collections.addAll(blockSuppliers, suppliers);
		return this;
	}

	public Blueprint addBlocks(List<Supplier<Block>> suppliers){
		blockSuppliers.addAll(suppliers);
		return this;
	}

	public Blueprint addBlock(Class<? extends Block> blockClass, int x, int y){
		Supplier<Block> blockSupplier = () -> {
			Block block = null;
			try {
				block =  blockClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				block = new Block();
			}
			return block.setRobotXY(x,y);
		};

		return addBlocks(blockSupplier);
	}

	public Blueprint addBlockRow(Class<? extends Block> blockClass, int startX, int endX, int y){
		for (int x = startX; x < endX; x++) {
			addBlock(blockClass, x, y);
		}
		return  this;
	}

	public Blueprint addBlockCol(Class<? extends Block> blockClass, int x, int startY, int endY){
		for (int y = startY; y < endY; y++) {
			addBlock(blockClass, x, y);
		}
		return  this;
	}

	public Blueprint addBlockRec(Class<? extends Block> blockClass, int x, int y, int w, int h){
		for (int i = y; i < y+h; i++) {
			addBlockRow(blockClass, x, x+w, i);
		}
		return this;
	}

	public String getName() {
		return name;
	}

	public Blueprint setName(String name) {
		this.name = name;
		return this;
	}
}
