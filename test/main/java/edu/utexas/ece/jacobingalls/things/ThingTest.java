package edu.utexas.ece.jacobingalls.things;

import edu.utexas.ece.jacobingalls.Game;
import edu.utexas.ece.jacobingalls.player.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class ThingTest {

	static Thing thing;
	static long number = 0;
	static Random random = new Random();

	@BeforeClass
	public static void setUpClass() {
		thing = new Thing(new Player(Color.GREEN)) {
			@Override
			public double getWidth() {
				return 20;
			}

			@Override
			public double getHeight() {
				return 20;
			}

			@Override
			public void render(GraphicsContext gc) {

			}

			@Override
			public void tick(long time_elapsed) {
				number = time_elapsed;
			}

			@Override
			public boolean isColliding(double x, double y) {
				return false;
			}

			@Override
			protected boolean clicked(double x, double y) {
				return false;
			}

			@Override
			protected void died(){
				super.died();
				number = -1;
			}
		};
	}

	@Test
	public void testTick() throws Exception {
		long r = random.nextLong();
		thing.tick(r);
		Assert.assertEquals(r, number);
	}

	@Test
	public void testDamage() throws Exception {
		int dmg = 1;
		double intH = thing.getHealth();
		thing.damage(0,0, dmg);     //damage is notifying where the thing was damaged
		Assert.assertEquals((int)intH-dmg, (int)thing.getHealth());
	}

	@Test
	public void testDied() throws Exception {
		thing.damage(0,0,thing.getHealth());
		Assert.assertEquals(-1, number);
	}

	@Test
	public void testGetPoint2D() throws Exception {

	}

	@Test
	public void testGetTeam() throws Exception {

	}

	@Before
	public void setUp() throws Exception {

	}
}