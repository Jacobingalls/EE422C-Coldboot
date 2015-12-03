package edu.utexas.ece.jacobingalls.things;

import edu.utexas.ece.jacobingalls.player.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MovingThingTest {

	static MovingThing movingThing;
	static Player player = new Player(Color.GREEN);

	@Before
	public void setUp() throws Exception {
		movingThing = new MovingThing(player) {
			@Override
			public double getWidth() { return 0; }

			@Override
			public double getHeight() { return 0; }

			@Override
			public void render(GraphicsContext gc) { }

			@Override
			public boolean isColliding(double x, double y) { return false; }

			@Override
			protected boolean clicked(double x, double y) { return false; }
		};
	}

	@Test
	public void testTick() throws Exception {
		movingThing.setDesiredVelocityX(100);
		movingThing.setDesiredVelocityY(50);
		movingThing.setCurrentVelocityX(0);
		movingThing.setCurrentVelocityY(0);
		movingThing.setAcceleration(1);


		Assert.assertEquals(0, (int)movingThing.getCurrentVelocityX());
		Assert.assertEquals(0, (int)movingThing.getCurrentVelocityY());

		movingThing.tick(1000);

		Assert.assertEquals(1, (int)movingThing.getCurrentVelocityX());
		Assert.assertEquals(1, (int)movingThing.getCurrentVelocityY());

		movingThing.tick(9000);

		Assert.assertEquals(10, (int)movingThing.getCurrentVelocityX());
		Assert.assertEquals(10, (int)movingThing.getCurrentVelocityY());

		movingThing.tick(40000);

		Assert.assertEquals(50, (int)movingThing.getCurrentVelocityX());
		Assert.assertEquals(50, (int)movingThing.getCurrentVelocityY());

		movingThing.tick(50000);

		Assert.assertEquals(100, (int)movingThing.getCurrentVelocityX());
		Assert.assertEquals(50, (int)movingThing.getCurrentVelocityY());

		movingThing.setDesiredVelocityX(-100);
		movingThing.setDesiredVelocityY(-50);
		movingThing.tick(50000);

		Assert.assertEquals(50, (int)movingThing.getCurrentVelocityX());
		Assert.assertEquals(0, (int)movingThing.getCurrentVelocityY());

		movingThing.tick(50000);

		Assert.assertEquals(0, (int)movingThing.getCurrentVelocityX());
		Assert.assertEquals(-50, (int)movingThing.getCurrentVelocityY());

	}
}