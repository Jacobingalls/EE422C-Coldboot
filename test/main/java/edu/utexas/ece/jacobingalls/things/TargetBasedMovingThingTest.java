package edu.utexas.ece.jacobingalls.things;

import edu.utexas.ece.jacobingalls.player.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jacob Ingalls on 12/1/2015.
 */
public class TargetBasedMovingThingTest {

    static TargetBasedMovingThing targetBasedMovingThing;
    static Player player = new Player(Color.GREEN);

    @Before
    public void setUp() throws Exception {
        targetBasedMovingThing = new TargetBasedMovingThing(player) {
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
        targetBasedMovingThing.setTargetLocationX(100);
        targetBasedMovingThing.setTargetLocationY(100);
        targetBasedMovingThing.setDesiredVelocityX(100);
        targetBasedMovingThing.setDesiredVelocityY(100);
        targetBasedMovingThing.setAcceleration(10);
        targetBasedMovingThing.setMaxVelocity(100);

        Assert.assertEquals(0, (int)targetBasedMovingThing.getX());
        Assert.assertEquals(0, (int)targetBasedMovingThing.getY());

        for (int i = 0; i < 1000; i++) {
            targetBasedMovingThing.tick(16);
        }

        Assert.assertEquals(100, (int)targetBasedMovingThing.getX());
        Assert.assertEquals(100, (int)targetBasedMovingThing.getY());

        targetBasedMovingThing.setTargetLocationX(1000);
        targetBasedMovingThing.setTargetLocationY(-500);

        for (int i = 0; i < 10000; i++) {
            targetBasedMovingThing.tick(16);
        }

        Assert.assertEquals(1000, Math.round(targetBasedMovingThing.getX()));
        Assert.assertEquals(-500, Math.round(targetBasedMovingThing.getY()));

    }
}