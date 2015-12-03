package edu.utexas.ece.jacobingalls;

import edu.utexas.ece.jacobingalls.player.Player;
import edu.utexas.ece.jacobingalls.things.buildings.RobotFactory;
import edu.utexas.ece.jacobingalls.things.buildings.TeamBase;
import edu.utexas.ece.jacobingalls.things.robots.AIRobot;
import edu.utexas.ece.jacobingalls.things.robots.Blueprint;
import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by jacobingalls on 12/2/15.
 */
public class GameTest {

	Game game;

	@Before
	public void setUp() throws Exception {
		game = new Game(new Player(Color.GREEN), new Player(Color.RED));
		Game.game = game;
	}

	@Test
	public void testTick() throws Exception {
		game.getThings().addAll(Arrays.asList(new TeamBase(game.getPlayer()), new TeamBase(game.getAiplayer())));
		assertFalse(game.gameOver);
		game.tick(1);
		assertFalse(game.gameOver);

		game.getThings().add(new RobotFactory(game.getPlayer(), 100,100,
				team -> Blueprint.SMALL_FIGHTER.build(AIRobot.class, team).get()
		));

		game.getPlayer().getFactories().findFirst().get().setNumberToBuild(1);
		assertFalse(game.getPlayer().getFactories().findFirst().get().isBuilding());
		assertFalse(game.getPlayer().getFactories().findFirst().get().getBuildPercentage() > 0);

		for (int i = 0; i < 100; i++) {
			game.tick(16);
		}

		assertTrue(game.getPlayer().getFactories().findFirst().get().isBuilding());
		assertTrue(game.getPlayer().getFactories().findFirst().get().getBuildPercentage() > 0);

		game.getPlayer().getBases().forEach(teamBase -> teamBase.damage(0,0,teamBase.getHealth() + 1));
		game.tick(1);

		assertTrue(game.gameOver);
	}

	@Test
	public void testConfigureWorld() throws Exception {
		assertTrue(game.getThings().isEmpty());
		game.configureWorld();
		assertFalse(game.getThings().isEmpty());
	}
}