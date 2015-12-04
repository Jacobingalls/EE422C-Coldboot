package edu.utexas.ece.jacobingalls;

import edu.utexas.ece.jacobingalls.gui.RightSideBar;
import edu.utexas.ece.jacobingalls.player.AIPlayer;
import edu.utexas.ece.jacobingalls.player.Player;
import edu.utexas.ece.jacobingalls.things.robots.Robot;
import edu.utexas.ece.jacobingalls.things.Thing;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class ColdBootGui extends Application
{
    public static boolean displayFPS = true;

    public static int target_fps = 60;

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

    private static boolean shift = false;

    private static GraphicsContext graphicsContext;

    private static TickThread tickThread;

    public static void main(String[] args) {
        launch(ColdBootGui.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Game.game = new Game(new Player(Color.GREEN), new AIPlayer(Color.RED, AIPlayer.PlayStyle.SIMPLE));
        graphicsContext = setupStage(primaryStage);

        // Mouse management
        setupMouseActions(primaryStage.getScene());

        // Keybindings
        setupKeyboardActions(primaryStage.getScene());

        // Begin the background thread
        tickThread = new TickThread();
        tickThread.start();

        // Temp
        configureWorld();

        // Finally, allow the user to see the Game.game.
        primaryStage.show();
    }

    @Override
    public void stop(){
        if(tickThread != null)
            tickThread.running = false;
    }

    private GraphicsContext setupStage(Stage stage){

        // Set the stage window up
        stage.setTitle("Cold Boot");
        stage.setHeight(Game.game.getWorldHeight());
        stage.setWidth(Game.game.getWorldWidth());

        // Create the canvas
        Canvas canvas = new Canvas(Game.game.getWorldWidth(), Game.game.getWorldHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,Game.game.getWorldWidth(), Game.game.getWorldHeight());

        // Add the canvas to a pane what will span the entire scene
        GridPane pane = new GridPane();
        pane.add(canvas, 0, 0);

        // Create the scene we will be using. It will fill the entire stage.
        Scene scene = new Scene(pane,Game.game.getWorldWidth(),Game.game.getWorldHeight());
        stage.setScene(scene);

        // When the width is changed update the width right now (so it looks smooth)
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            Game.game.setWorldWidth(newSceneWidth.intValue());
            canvas.setWidth(Game.game.getWorldWidth());
            tick();
        });

        // When the height is changed update the width right now (so it looks smooth)
        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            Game.game.setWorldHeight(newSceneHeight.intValue());
            canvas.setHeight(Game.game.getWorldHeight());
            tick();
        });

        return gc;
    }

    private void configureWorld(){
        Game.game.configureWorld();
    }





    private String fpsStr = "---";
    private long last_tick = System.currentTimeMillis();
    private synchronized void tick(){
        long time_elapsed = System.currentTimeMillis() - last_tick;
        last_tick = System.currentTimeMillis();

        tick(time_elapsed);
        render(graphicsContext);
    }

    private void tick(long time_elapsed){
        // Update FPS Meter
        if(time_elapsed > 0)
            fpsStr = Math.round((1e3/time_elapsed))+"";

        //Update Viewport
        updateViewport(time_elapsed);

        //Update Mouse Pos
        mouseXViewport = mouseX-viewportX;
        mouseYViewport = mouseY-viewportY;

        // Tick the Game.game
        Game.game.tick(time_elapsed);
    }

    private void render(GraphicsContext gc){
        //clear screen
        gc.setFill(Color.BLACK);
        gc.fillRect(0 , 0,Game.game.getWorldWidth(), Game.game.getWorldHeight());


        // Create Parallax Background
        renderParallaxBackground(gc);

        if(displayFPS) {
            gc.setFill(Color.YELLOW);
            gc.fillText(fpsStr, Game.game.getWorldWidth() - 20, 10, 20);
        }

        Game.game.getThings().forEach(thing -> thing.render(gc));

        if(dragging){
            Game.game.getThings().forEach(thing -> thing.setHovering(false));
            getThingsInDragArea().forEach(thing -> thing.setHovering(true));

            double x = mouseX;
            double y = mouseY;
            double width = mouseXDrag - mouseX;
            double height = mouseYDrag - mouseY;

            if(width < 0){ x = mouseXDrag; width = -width;  }
            if(height < 0){ y = mouseYDrag; height = -height; }

            gc.setStroke(Color.GRAY);
            gc.strokeRect(x, y, width, height);
        } else {
            Game.game.getThings().forEach(thing -> thing.setHovering(false));
            List<Thing> hovered = Game.game.getThings().parallelStream().filter(thing -> thing.isCollidingRoughBox(mouseXViewport, mouseYViewport)).collect(Collectors.toList());
            if(hovered.size() >= 1) {
                hovered.get(hovered.size() - 1).setHovering(true);
            }
        }

        Game.game.getRightSideBar().render(gc);

        gc.setFill(Color.GRAY);
        gc.fillText(getKeyboardText(), 10, Game.game.getWorldHeight()-10);


//        TODO implement resource management
//        gc.setFill(Color.WHITE);
//        gc.fillText("I: " + (Math.round(getGame.game().getPlayer().getIron() * 10.0)/10.0) + " ("+getGame.game().getPlayer().getIronReplenishRate()+")", 10, 30);
//
//        gc.setFill(getGame.game().getPlayer().getTeamColor());
//        gc.fillText("E: " + (Math.round(getGame.game().getPlayer().getEnergy() * 10.0)/10.0) + " ("+getGame.game().getPlayer().getEnergyReplenishRate()+")", 120, 30);
//
//        gc.setFill(getGame.game().getPlayer().getTeamAlternate1Color());
//        gc.fillText("G: " + (Math.round(getGame.game().getPlayer().getGold() * 10.0)/10.0) + " ("+getGame.game().getPlayer().getGoldReplenishRate()+")", 240, 30);


        if(Game.game.gameOver){
            double xp = Game.game.getWorldWidth()/2-100;
            double yp = Game.game.getWorldHeight()/2-100;
            gc.setFill(Game.game.gameOverColor.darker().darker().darker());
            gc.setStroke(Game.game.gameOverColor);
            gc.fillRect(xp, yp, 200,200);
            gc.strokeRect(xp, yp, 200,200);

            gc.setFill(Game.game.gameOverColor);
            TextAlignment textAlignment = gc.getTextAlign();
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(Game.game.gameOverString, xp+100, yp+110);
            gc.setTextAlign(textAlignment);
        }


    }

    private void renderParallaxBackground(GraphicsContext gc) {
        renderParallaxBackgroundPart(gc, 25, .25, Color.rgb(10,10,10));
        renderParallaxBackgroundPart(gc, 50, .5, Color.rgb(20,20,20));
        renderParallaxBackgroundPart(gc, 100, 1, Color.rgb(35,35,35));
    }

    private void renderParallaxBackgroundPart(GraphicsContext gc, int boxSize, double parallax, Color color) {
        int divisionsX = (Game.game.getWorldWidth()/boxSize)+2;
        int divisionsY = (Game.game.getWorldHeight()/boxSize)+2;


        gc.setStroke(color);
        double xOffset = (viewportX*parallax) % boxSize;
        double yOffset = (viewportY*parallax) % boxSize;

        for (int i = -1; i < divisionsX; i++) {
            gc.strokeLine(i*boxSize+xOffset, 0, i*boxSize+xOffset, Game.game.getWorldHeight());
        }

        for (int i = -1; i < divisionsY; i++) {
            gc.strokeLine(0, i*boxSize+yOffset, Game.game.getWorldWidth(), i*boxSize+yOffset);
        }
    }

    private class TickThread extends Thread{
        public boolean running = true;
        private int fps;
        private int max_tick_len;
        private long prev_tick = System.currentTimeMillis();
        private long next_tick = System.currentTimeMillis() + max_tick_len;

        public void tick() {
            prev_tick = System.currentTimeMillis();
            next_tick = prev_tick + max_tick_len;

            Platform.runLater(ColdBootGui.this::tick);
        }

        public void run() {
            try {
                while (running) {

                    if(fps != target_fps){
                        fps= target_fps;
                        max_tick_len = (int)(Math.floor(1e3/(fps)));
                    }

                    tick();

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
        if(viewpointOverride && time_elapsed > 0 && Game.game.getRightSideBar().getSelectedThing() != null){
            double desiredViewportX = -Game.game.getRightSideBar().getSelectedThing().getXCenter()+Game.game.getWorldWidth()/2;
            double desiredViewportY = -Game.game.getRightSideBar().getSelectedThing().getYCenter()+Game.game.getWorldHeight()/2;

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

    private String getKeyboardText(){
        if(Game.game.getPlayer().getSelectedThings().count() > 0) {
            if (viewpointOverride) {
                return "c: Affix Camera, rClick: send unit(s)";
            } else {
                return "c: Follow Unit, rClick: send unit(s)";
            }
        } else {
            if(shift)
                return "B: Select All Bases, F: Select All Factories, R: Select All Robots";
            else
                return "b: Cycle Base, f: Cycle Factory, r: Cycle Robot";
        }
    }

    private void setupKeyboardActions(Scene scene){
        scene.setOnKeyReleased(event -> {
            if(!event.isShiftDown())
                shift = false;
        });

        scene.setOnKeyPressed(event -> {
            if (event.isShiftDown())
                shift = true;

            if (event.getCode().isLetterKey()) {
                if (event.getCode().equals(KeyCode.C)) {
                    viewpointOverride = !viewpointOverride;
                } else if (event.getCode().equals(KeyCode.B)) {

                    if (!event.isShiftDown())
                        Game.game.getPlayer().selectNextBase();
                    else {
                        Game.game.getPlayer().getSelectedThings().forEach(thing -> thing.setSelected(false));
                        Game.game.getPlayer().getBases().forEach(factory -> factory.setSelected(true));
                    }

                    if (!event.isControlDown()) {
                        viewpointOverride = true;
                    }
                } else if (event.getCode().equals(KeyCode.F)) {

                    if (!event.isShiftDown())
                        Game.game.getPlayer().selectNextFactory();
                    else {
                        Game.game.getPlayer().getSelectedThings().forEach(thing -> thing.setSelected(false));
                        Game.game.getPlayer().getFactories().forEach(factory -> factory.setSelected(true));
                    }

                    if (!event.isControlDown()) {
                        viewpointOverride = true;
                    }
                } else if (event.getCode().equals(KeyCode.R)) {

                    if (!event.isShiftDown())
                        Game.game.getPlayer().selectNextRobot();
                    else {
                        Game.game.getPlayer().getSelectedThings().forEach(thing -> thing.setSelected(false));
                        Game.game.getPlayer().getRobots().forEach(robot -> robot.setSelected(true));
                    }

                    if (!event.isControlDown()) {
                        viewpointOverride = true;
                    }
                }
            }
        });
    }

    private void setupMouseActions(Scene scene){
        scene.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });

        scene.setOnMouseClicked(event -> {
            
            if(movingViewPort) {
                viewpointOverride = false;
                movingViewPort = false;
            }else if(event.getButton() == MouseButton.PRIMARY) {
                RightSideBar rightSideBar = Game.game.getRightSideBar();
                if(rightSideBar.getOffset() < 10 && mouseX >= rightSideBar.getX() && mouseX <= rightSideBar.getX() + rightSideBar.getWidth()
                        && mouseX >= rightSideBar.getY() && mouseY <= rightSideBar.getY() + rightSideBar.getHeight()){
                    rightSideBar.wasClicked = true;
                } else {
                    Game.game.getThings().forEach(thing -> thing.setSelected(false));
                    if (dragging) {
                        getThingsInDragArea().forEach(thing -> thing.setSelected(true));
                    } else {
                        List<Thing> hovered = Game.game.getThings().parallelStream().filter(thing -> thing.isCollidingRoughBox(mouseXViewport, mouseYViewport)).collect(Collectors.toList());
                        if (hovered.size() >= 1)
                            hovered.get(hovered.size() - 1).click(mouseXViewport, mouseYViewport);
                    }
                    mouseXDrag = event.getX();
                    mouseYDrag = event.getY();
                    dragging = false;
                }
            } else if(event.getButton() == MouseButton.SECONDARY){
                List<Robot> selectedRobots = Game.game.getThings().parallelStream()
                        .filter(Thing::isSelected)
                        .filter(thing -> thing.getTeam().equals(Game.game.getPlayer()))
                        .filter(thing -> thing instanceof Robot)
                        .map(thing -> (Robot) thing)
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
        return Game.game.getThings().parallelStream().filter(thing -> thing.getXCenter() >= x1
                && thing.getYCenter() >= y1
                && thing.getXCenter() <= x2
                && thing.getYCenter() <= y2).collect(Collectors.toList());
    }
}

