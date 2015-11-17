package edu.utexas.ece.jacobingalls;

import edu.utexas.ece.jacobingalls.buildings.RobotFactory;
import edu.utexas.ece.jacobingalls.robots.AIRobot;
import edu.utexas.ece.jacobingalls.robots.Robot;
import edu.utexas.ece.jacobingalls.robots.blocks.*;
import edu.utexas.ece.jacobingalls.robots.projectiles.Projectile;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App extends Application
{
    public static boolean displayFPS = true;

    public static int target_fps = 60;
    public static int world_width = 800;
    public static int world_height = 600;

    public static double viewportX = 0;
    public static double viewportY = 0;
    private static double viewportXVelocity = 0;
    private static double viewportYVelocity = 0;
    public static boolean movingViewPort = false;
    public static boolean viewpointOverride = false;

    public static double mouseX = 0;
    public static double mouseY = 0;
    public static double mouseXViewport = 0;
    public static double mouseYViewport = 0;

    private static boolean dragging = false;
    private static double mouseXDrag = 0;
    private static double mouseYDrag = 0;

    public static GraphicsContext graphicsContext;

    public static TickThread tickThread;

    public static List<Thing> things = new LinkedList<>();
    public static Queue<Thing> thingsWaiting = new ConcurrentLinkedQueue<>();

    public static RightSideBar rightSideBar = new RightSideBar();

    public static Player player = new Player(Team.GREEN);

    public static void main(String[] args) {
        launch(App.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("EE 422C -- Project 6");
        primaryStage.setHeight(world_height);
        primaryStage.setWidth(world_width);

        Canvas canvas = new Canvas(world_width, world_height);
        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0,0,world_width, world_height);

        GridPane pane = new GridPane();
        pane.add(canvas, 0, 0);

        Scene scene = new Scene(pane,world_width,world_height);
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            world_width = newSceneWidth.intValue();
            canvas.setWidth(world_width);
            tick();
        });
        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            world_height = newSceneHeight.intValue();
            canvas.setHeight(world_height);
            tick();
        });
        primaryStage.setScene(scene);

        tickThread = new TickThread();
        tickThread.start();


        /*
         *  Temp
         */
        things.add(new RobotFactory(Team.GREEN, 100, 100, team -> new AIRobot(team).addBlock(
                new CPUBlock().setRobotXY(0, 0),
                new Block().setRobotXY(1, 0),
                new GunBlock().setRobotXY(0, 1),
                new GunBlock().setRobotXY(0, 2),
                new GunBlock().setRobotXY(0, 3),
                new MedBlock().setRobotXY(1, 1),
                new ReactorBlock().setRobotXY(1, 2),
                new ReactorBlock().setRobotXY(1, 3)
        )).setNumberToBuild(3).setX(10).setY(10));

        things.add(new RobotFactory(Team.GREEN, 100, 300, team -> new AIRobot(team).addBlock(
                new CPUBlock(),
                new ReactorBlock().setRobotXY(0, 1),
                new ReactorBlock().setRobotXY(0, -1),
                new ReactorBlock().setRobotXY(0, 2),
                new ReactorBlock().setRobotXY(0, -2),
                new MedBlock().setRobotXY(1, 0),
                new MedBlock().setRobotXY(-1, 0),

                new GunBlock().setRobotXY(-1, 2),
                new GunBlock().setRobotXY(1, 2),
                new GunBlock().setRobotXY(1, 1),
                new GunBlock().setRobotXY(-1, 1),
                new GunBlock().setRobotXY(-1, -2),
                new GunBlock().setRobotXY(1, -2),
                new GunBlock().setRobotXY(1, -1),
                new GunBlock().setRobotXY(-1, -1)
        )).setNumberToBuild(1).setX(10).setY(300));


        things.add(new RobotFactory(Team.RED, 500, 300, team -> new AIRobot(team).addBlock(
                new CPUBlock().setRobotXY(0, 0),
                new GunBlock().setRobotXY(1, 0),
                new GunBlock().setRobotXY(0, 2),
                new Block().setRobotXY(0, 1),
                new Block().setRobotXY(0, -1),
                new GunBlock().setRobotXY(0, -2),
                new MedBlock().setRobotXY(1, 2),
                new ReactorBlock().setRobotXY(1, 1),
                new ReactorBlock().setRobotXY(1, -1),
                new Block().setRobotXY(1, -2)
        )).setNumberToBuild(10).setX(1600).setY(10));

        things.add(new RobotFactory(Team.RED, 100, 300, team -> new AIRobot(team).addBlock(
                new CPUBlock(),
                new ReactorBlock().setRobotXY(0, 1),
                new ReactorBlock().setRobotXY(0, -1),
                new ReactorBlock().setRobotXY(0, 2),
                new ReactorBlock().setRobotXY(0, -2),
                new MedBlock().setRobotXY(1, 0),
                new MedBlock().setRobotXY(-1, 0),

                new GunBlock().setRobotXY(-1, 2),
                new GunBlock().setRobotXY(1, 2),
                new GunBlock().setRobotXY(1, 1),
                new GunBlock().setRobotXY(-1, 1),
                new GunBlock().setRobotXY(-1, -2),
                new GunBlock().setRobotXY(1, -2),
                new GunBlock().setRobotXY(1, -1),
                new GunBlock().setRobotXY(-1, -1)
        )).setNumberToBuild(1).setX(1600).setY(300));

        /*
            Mouse management
         */
        scene.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });

        scene.setOnMouseClicked(event -> {
            viewpointOverride = true;
            if(movingViewPort) {
                viewpointOverride = false;
                movingViewPort = false;
            }else if(event.getButton() == MouseButton.PRIMARY) {
                if(rightSideBar.offset < 10 && mouseX >= rightSideBar.x && mouseX <= rightSideBar.x + rightSideBar.width
                        && mouseX >= rightSideBar.y && mouseY <= rightSideBar.y + rightSideBar.height){
                    rightSideBar.wasClicked = true;
                } else {
                    things.forEach(thing -> thing.setSelected(false));
                    if (dragging) {
                        getThingsInDragArea().forEach(thing -> thing.setSelected(true));
                    } else {
                        List<Thing> hovered = things.parallelStream().filter(thing -> thing.isCollidingRoughBox(mouseXViewport, mouseYViewport)).collect(Collectors.toList());
                        if (hovered.size() >= 1)
                            hovered.get(hovered.size() - 1).click(mouseXViewport, mouseYViewport);
                    }
                    mouseXDrag = event.getX();
                    mouseYDrag = event.getY();
                    dragging = false;
                }
            } else if(event.getButton() == MouseButton.SECONDARY){
                List<Robot> selectedRobots = things.parallelStream().filter(Thing::isSelected)
                        .filter(thing -> thing.getTeam().equals(player.getTeam()))
                        .filter(thing -> thing instanceof Robot)
                        .map(thing -> (Robot)thing)
                        .collect(Collectors.toList());
                selectedRobots.forEach(robot -> robot.setTargetLocation(event.getX()-viewportX, event.getY()-viewportY));
            }
        });

        scene.setOnDragDetected(event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                movingViewPort = true;
                viewpointOverride = false;
                viewportXVelocity = 0;
                viewportYVelocity = 0;
                dragging = false;
            } else {
                mouseXDrag = event.getX();
                mouseYDrag = event.getY();
                dragging = true;
                movingViewPort = false;
            }
        });

        scene.setOnMouseDragged(event -> {
            if(movingViewPort){
                double xDelta =  event.getX() - mouseXDrag;
                double yDelta =  event.getY() - mouseYDrag;
                viewportX += xDelta;
                viewportY += yDelta;
            }
            mouseXDrag = event.getX();
            mouseYDrag = event.getY();
        });

        primaryStage.show();
    }

    private List<Thing> getThingsInDragArea(){
        double x = mouseXViewport;
        double y = mouseYViewport;
        double width = mouseXDrag - viewportX - x;
        double height = mouseYDrag - viewportY - y;

        if (width < 0) {  x = mouseXDrag - viewportX;  width = -width;  }
        if(height < 0){ y = mouseYDrag -viewportY; height = -height; }

        final double x1 = x;
        final double y1 = y;
        final double x2 = x+width;
        final double y2 = y+height;
        return things.parallelStream().filter(thing -> thing.getXCenter() >= x1
                && thing.getYCenter() >= y1
                && thing.getXCenter() <= x2
                && thing.getYCenter() <= y2).collect(Collectors.toList());
    }

    private String fpsStr = "---";
    private long last_tick = System.currentTimeMillis();
    private synchronized void tick(){
        long time_elapsed = System.currentTimeMillis() - last_tick;
        last_tick = System.currentTimeMillis();

        if(time_elapsed > 0)
            fpsStr = Math.round((1e3/time_elapsed))+"";

        //Update Viewport
        updateViewport(time_elapsed);

        //Update Mouse Pos
        mouseXViewport = mouseX-viewportX;
        mouseYViewport = mouseY-viewportY;

//        if(!things.parallelStream().filter(thing -> thing.getTeam().equals(Team.GREEN)).findAny().isPresent()){
//            for (int i = 0; i < 4; i++) {
//                Robot goodGuy = (Robot) new Robot(Team.GREEN)
//                        .addBlock(new CPUBlock().setRobotXY(0, 0),
//                                new GunBlock().setRobotXY(1, 0),
//                                new GunBlock().setRobotXY(0, 1),
//                                new Block().setRobotXY(-1, 1))
//                        .setMaxVelocity(50)
//                        .setAcceleration(100);
//                things.add(goodGuy.setTargetLocation(100, 100*i + 50).setXCenter(0).setYCenter(100 * i + 50));
//            }
//        }
//
//
//        if(!things.parallelStream().filter(thing -> thing.getTeam().equals(Team.RED)).findAny().isPresent()){
//            things.add(new Robot(Team.RED)
//                    .addBlock(new CPUBlock().setRobotXY(0, 0),
//                            new CPUBlock().setRobotXY(0, 2),
//                            new CPUBlock().setRobotXY(0, -2),
//                            new GunBlock().setRobotXY(-1, 4),
//                            new GunBlock().setRobotXY(-1, 3),
//                            new GunBlock().setRobotXY(-1, 2),
//                            new GunBlock().setRobotXY(-1, 1),
//                            new GunBlock().setRobotXY(-1, 0),
//                            new GunBlock().setRobotXY(-1, -1),
//                            new GunBlock().setRobotXY(-1, -2),
//                            new GunBlock().setRobotXY(-1, -3),
//                            new GunBlock().setRobotXY(-1, -4),
//                            new GunBlock().setRobotXY(-2, 4),
//                            new GunBlock().setRobotXY(-2, 3),
//                            new GunBlock().setRobotXY(-2, 2),
//                            new GunBlock().setRobotXY(-2, 1),
//                            new GunBlock().setRobotXY(-2, 0),
//                            new GunBlock().setRobotXY(-2, -1),
//                            new GunBlock().setRobotXY(-2, -2),
//                            new GunBlock().setRobotXY(-2, -3),
//                            new GunBlock().setRobotXY(-2, -4),
//                            new GunBlock().setRobotXY(-3, 4),
//                            new GunBlock().setRobotXY(-3, 3),
//                            new GunBlock().setRobotXY(-3, 2),
//                            new GunBlock().setRobotXY(-3, 1),
//                            new GunBlock().setRobotXY(-3, 0),
//                            new GunBlock().setRobotXY(-3, -1),
//                            new GunBlock().setRobotXY(-3, -2),
//                            new GunBlock().setRobotXY(-3, -3),
//                            new GunBlock().setRobotXY(-3, -4)
//
//
//                    )
//                    .setTargetLocation(500, 200)
//                    .setMaxVelocity(500)
//                    .setAcceleration(100)
//                    .setX(700).setY(300));
//
//        }

        while (!thingsWaiting.isEmpty())
            things.add(thingsWaiting.poll());

        things.forEach(thing -> thing.tick(time_elapsed));

        things.parallelStream().filter(thing -> thing instanceof Projectile)
                .map(thing -> (Projectile)thing)
                .forEach(projectile -> {
                    List<Thing> hitThings = things.parallelStream().filter(thing -> thing.getHealth() > 0)
                            .filter(thing -> !thing.getTeam().equals(projectile.getTeam()))
                            .filter(thing -> thing.isColliding(projectile.getX(), projectile.getY()))
                            .collect(Collectors.toList());
                    if(!hitThings.isEmpty()){
                        projectile.setHealth(-1);
                        hitThings.forEach(thing -> thing.damage(projectile.getX(), projectile.getY(), projectile.getDamage()));
                    }
                });

        things = things.parallelStream().filter(thing -> thing.getHealth() > 0).collect(Collectors.toList());
        rightSideBar.tick(time_elapsed);

        //clear screen
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0 , 0,world_width, world_height);


        if(displayFPS) {
            graphicsContext.setFill(Color.YELLOW);
            graphicsContext.fillText(fpsStr, world_width - 20, 10, 20);
        }

        things.forEach(thing -> thing.render(graphicsContext));

        if(dragging){
            things.forEach(thing -> thing.setHovering(false));
            getThingsInDragArea().forEach(thing -> thing.setHovering(true));

            double x = mouseX;
            double y = mouseY;
            double width = mouseXDrag - mouseX;
            double height = mouseYDrag - mouseY;

            if(width < 0){ x = mouseXDrag; width = -width;  }
            if(height < 0){ y = mouseYDrag; height = -height; }

            graphicsContext.setStroke(Color.GRAY);
            graphicsContext.strokeRect(x, y, width, height);
        } else {
            things.forEach(thing -> thing.setHovering(false));
            List<Thing> hovered = things.parallelStream().filter(thing -> thing.isCollidingRoughBox(mouseXViewport, mouseYViewport)).collect(Collectors.toList());
            if(hovered.size() >= 1) {
                hovered.get(hovered.size() - 1).setHovering(true);
            }
        }

        rightSideBar.render(graphicsContext);
    }

    @Override
    public void stop(){

        if(tickThread != null)
            tickThread.running = false;
    }

    private class TickThread extends Thread{
        public boolean running = true;
        private int fps;
        private int max_tick_len;
        private long prev_tick = System.currentTimeMillis();
        private long next_tick = System.currentTimeMillis() + max_tick_len;

        public void render() {
            long n = System.currentTimeMillis();
            prev_tick = n;
            next_tick = prev_tick + max_tick_len;

            Platform.runLater(App.this::tick);
        }

        public void run() {
            try {
                while (running) {

                    if(fps != target_fps){
                        fps= target_fps;
                        max_tick_len = (int)(Math.floor(1e3/(fps)));
                    }

                    render();

                    long l =  next_tick - prev_tick - 1;
                    if(l > 1)
                        Thread.sleep(l);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateViewport(long time_elapsed){
        if(viewpointOverride && time_elapsed > 0 && rightSideBar.selectedThing != null){
            double desiredViewportX = -rightSideBar.selectedThing.getXCenter()+world_width/2;
            double desiredViewportY = -rightSideBar.selectedThing.getYCenter()+world_height/2;

            if(viewportX > desiredViewportX-1 && viewportX < desiredViewportX+1 && viewportY > desiredViewportY-1 && viewportY < desiredViewportY+1){
                viewportX = desiredViewportX;
                viewportY = desiredViewportY;
            } else {
                double acc = 1000;
                double maxV = 100000;
                double stop_distance_x = (viewportXVelocity*viewportXVelocity)/(2 * acc);
                double stop_distance_y = (viewportYVelocity*viewportYVelocity)/(2 * acc);

                if(viewportXVelocity > 0)
                    stop_distance_x = viewportX + stop_distance_x + 1;
                else
                    stop_distance_x = viewportX - stop_distance_x - 1;

                if(viewportYVelocity > 0)
                    stop_distance_y = viewportY + stop_distance_y + 1;
                else
                    stop_distance_y = viewportY - stop_distance_y - 1;

                double desiredVelocityX = 0;
                if(desiredViewportX > viewportX && stop_distance_x > desiredViewportX)
                    desiredVelocityX = 0;
                else if(desiredViewportX < viewportX && stop_distance_x < desiredViewportX)
                    desiredVelocityX = 0;
                else if(desiredViewportX > viewportX)
                    desiredVelocityX = maxV;
                else if(desiredViewportX < viewportX)
                    desiredVelocityX = -maxV;

                double desiredVelocityY = 0;
                if(desiredViewportY > viewportY && stop_distance_y > desiredViewportY)
                    desiredVelocityY = 0;
                else if(desiredViewportY < viewportY && stop_distance_y < desiredViewportY)
                    desiredVelocityY = 0;
                else if(desiredViewportY > viewportY)
                    desiredVelocityY = maxV;
                else if(desiredViewportY < viewportY)
                    desiredVelocityY = -maxV;


                double tacc = time_elapsed/1000.0;
                if(viewportXVelocity < desiredVelocityX)
                    viewportXVelocity += acc * tacc;
                else if(viewportXVelocity > desiredVelocityX)
                    viewportXVelocity -= acc * tacc;

                if(viewportXVelocity > maxV)
                    viewportXVelocity = maxV;
                else if(viewportXVelocity < -maxV)
                    viewportXVelocity = -maxV;

                if(desiredVelocityY > viewportYVelocity)
                    viewportYVelocity += acc * tacc;
                else if(desiredVelocityY < viewportYVelocity)
                    viewportYVelocity -= acc * tacc;

                if(viewportYVelocity > maxV)
                    viewportYVelocity = maxV;
                else if(viewportYVelocity < -maxV)
                    viewportYVelocity = -maxV;

                viewportX += viewportXVelocity * tacc;
                viewportY += viewportYVelocity * tacc;
            }
        }
    }


}

