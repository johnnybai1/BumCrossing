package bumcrossing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Game extends JPanel {

    // Constants
    static final String TITLE = "Bum Crossing";
    static final int WIDTH = 1920/2;
    static final int HEIGHT = 1080/2;
    final static int[] CARS_Y = {69, 69*2, 69*3, 69*4, 69*5, 69*6}; // Defines our lanes
    static final int UPDATES_PER_SEC = 60;  // number of game update per second
    static final long UPDATE_PERIOD_NSEC = 1000000000L / UPDATES_PER_SEC;  // nanoseconds

    // Enum for game states
    static enum GameState {
        INIT, PLAY, PAUSE, LOSE, DESTROY
    }
    static GameState state;

    // Game objects
    private Player player;
    private Cart cart;
    private Car[] cars;
    private Bonus bonus;

    private int score;
    private int count = 0;

    // Custom drawing panel
    private GameCanvas canvas;

    public Game() {
        // Initialize game objects
        gameInit();
        // UI Components
        canvas = new GameCanvas();
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(canvas);

        gameStart();
    }
    // ------ Game related code below -------

    // Initialize game objects: runs once
    public void gameInit() {
        state = GameState.INIT;
        player = new Player();
        cart = new Cart();
        cars = new Car[6];
        generateLevel();
    }

    private void generateLevel(int seed) {
        Random rng = new Random(seed);
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(rng.nextInt(WIDTH), CARS_Y[i]);
        }
        bonus = new Bonus(300, 200);
    }

    private void generateLevel() {
        generateLevel((int) (Math.random() * 10));
    }

    // To start and re-start the game.
    public void gameStart() {
        // Create a new thread
        Thread gameThread =  new Thread() {
            // Override run() to provide the running behavior of this thread.
            @Override
            public void run() {
                gameLoop();
            }
        };
        // Start the thread. start() calls run(), which in turn calls gameLoop().
        gameThread.start();
    }

    private void gameLoop() {
        state = GameState.PLAY;

        long start, elapsed, remain;   // in msec
        // Regenerate the game objects for a new game

        // Game loop
        while (state != GameState.DESTROY) { // while (not game over)
            start = System.nanoTime();
            if (state != GameState.PAUSE) { // if (game is not paused
                // Update the state and position of all the game objects,
                // detect collisions and provide responses.
                if (state != GameState.LOSE) {
                    gameUpdate();
                }
            }
            // Refresh the display
            repaint();
            // Delay timer to provide the necessary delay to meet the target rate
            elapsed = System.nanoTime() - start;
            remain = (UPDATE_PERIOD_NSEC - elapsed) / 1000000;  // in milliseconds
            if (remain < 17) remain = 17;   // set a minimum
            try {
                // Provides the necessary delay and also yields control so that other thread can do work.
                Thread.sleep(remain);
            } catch (InterruptedException ex) { }
        }
    }

    // Shutdown the game: cleanup code that runs once
    public void gameShutdown() {

    }

    private void processBonus() {
        if (bonus.consumed(cart) || bonus.consumed(player)) {
            bonus.hide();
            score = score + 100;
            count = count + 1;
        }
    }

    // Check and process collision events. On GameState.LOSE, we stop updating
    private void processCollision(Car car) {
        Rectangle playerBound = player.getBounds();
        Rectangle cartBound = cart.getBounds();
        Rectangle carBound = car.getBounds();
        if (player.isMovingForward()) {
            // Only check if we're crossing
            if (carBound.intersects(cartBound)) {
                // If a car hits our cart...
                cart.setDx(0);  // stop the cart
                cart.setDy(0);
                car.setDx(0);   // stop the car
                if (cart.isConnected()) {
                    // If its connected, lose
                    state = GameState.LOSE;
                }
            }
            if (carBound.intersects(playerBound)) {
                // If a car hits our player, we lose
                state = GameState.LOSE;
            }
        }
    }

    // Update the game
    public void gameUpdate() {
        player.update();
        cart.update(player);
        for (int i = 0; i < cars.length; i++) {
            cars[i].update();
            processCollision(cars[i]);
        }
        progress();
        processBonus();
    }

    private void restart() {
        score = 0;
        count = 0;
        player = new Player();
        cart = new Cart();
        generateLevel();
        state = GameState.PLAY;
    }

    private void progress() {
        if (player.crossed()) {
            count = count + 1;
            if (cart.isConnected()) {
                score = score + count * 10;
            }
            else score = score + 10;
            generateLevel();
            player.regenerate();
            cart.regenerate();
        }
    }

    // Render the world after update
    private void gameDraw(Graphics2D g2d) {
        g2d.setColor(Color.CYAN);
        g2d.drawString(Integer.toString(score), 25, 25);

        player.draw(g2d);
        cart.draw(g2d);
        //cart.draw(g2d, player);
        for (int i = 0; i < cars.length; i++) {
            cars[i].draw(g2d);
        }
        bonus.draw(g2d);

        // Draw rectangle bounds
        g2d.setColor(Color.WHITE);
        Rectangle playerRect = player.getBounds();
        Rectangle cartRect = cart.getBounds();
        g2d.drawRect((int) playerRect.getX(), (int) playerRect.getY(), (int) playerRect.getWidth(), (int) playerRect.getHeight());
        g2d.drawRect((int) cartRect.getX(), (int) cartRect.getY(), (int) cartRect.getWidth(), (int) cartRect.getHeight());
        for (int i = 0; i < cars.length; i++) {
            Rectangle carRect = cars[i].getBounds();
            g2d.drawRect((int) carRect.getX(), (int) carRect.getY(), (int) carRect.getWidth(), (int) carRect.getHeight());
        }

    }

    // Key-Events
    public void gameKeyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_P: {
                if (state == GameState.PLAY) {
                    state = GameState.PAUSE;
                }
                else state = GameState.PLAY;
                break;
            }
            case KeyEvent.VK_R: {
                // restart
                restart();
                break;
            }
            case KeyEvent.VK_LEFT: {
                player.moveLeft();
                break;
            }
            case KeyEvent.VK_RIGHT: {
                player.moveRight();
                break;
            }
            case KeyEvent.VK_SPACE: {
                player.moveForward(2);
                break;
            }
            case KeyEvent.VK_S: {
                if (player.isMovingForward() && cart.isConnected()) {
                    cart.shoot(player);
                }
                break;
            }
            case KeyEvent.VK_1: {
                player.moveForward(1);
                break;
            }
            case KeyEvent.VK_2: {
                player.moveForward(2);
                break;
            }
        }
    }

    public void gameKeyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case (KeyEvent.VK_LEFT): {
                player.setMovingLeft(false);
                break;
            }
            case (KeyEvent.VK_RIGHT): {
                player.setMovingRight(false);
                break;
            }
        }
        player.stopX();
    }

    // ------ Custom drawing panel ------
    class GameCanvas extends JPanel implements KeyListener {

        private Image background = GameIO.loadImage("background_landscape.png", 2);

        public GameCanvas() {
            setFocusable(true);
            requestFocus();
            addKeyListener(this);
            setVisible(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            super.paintComponent(g2d);
            // Draw game objects
            g2d.drawImage(background, 0, 0, this);
            gameDraw(g2d);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // Not used
        }

        @Override
        public void keyPressed(KeyEvent e) {
            gameKeyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            gameKeyReleased(e);
        }
    }

    // main
    public static void main(String[] args) {
        // Use the event dispatch thread to build the UI for thread-safety.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame(TITLE);
                // Set the content-pane of the JFrame to an instance of main JPanel
                frame.setContentPane(new Game());  // main JPanel as content pane
                //frame.setJMenuBar(menuBar);          // menu-bar (if defined)
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null); // center the application window
                frame.setVisible(true);            // show it
            }
        });
    }

}