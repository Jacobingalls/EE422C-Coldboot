package edu.utexas.ece.jacobingalls.things;

import edu.utexas.ece.jacobingalls.player.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
	}
}