package bumcrossing;

public class Player extends Sprite {

    static final int INIT_X = Game.WIDTH/2;
    static final int INIT_Y = Game.HEIGHT - 50/2;
    static final int HEIGHT = 50;
    static final int WIDTH = 50;

    protected boolean alive;
    private boolean movingLeft, movingRight, movingForward;

    public Player() {
        this(INIT_X, INIT_Y);
    }

    public Player(int x, int y) {
        super(x, y);
        alive = true;
        image = GameIO.loadImage("character.png", 2);
        setWidth(image.getWidth(null));
        setHeight(image.getHeight(null));
    }

    public void regenerate() {
        y = INIT_Y;
        movingForward = false;
    }

    @Override
    public void update() {
        if (dx != 0) {
            x = x + dx;
        }
        if (x + dx >= Game.WIDTH - 100) {
            x = Game.WIDTH - 100;
        }
        y = y + dy;
        if (y + dy <= 40) {
            y = 40;
            dy = 0;
        }
    }

    public void moveLeft() {
        if (!movingForward) {
            dx = -MOVESPEED;
            movingLeft = true;
        }
    }

    public void moveRight() {
        if (!movingForward) {
            dx = MOVESPEED;
            movingRight = true;
        }
    }

    public void moveForward(int dy) {
        movingLeft = false;
        movingRight = false;
        movingForward = true;
        dx = 0;
        this.dy = -MOVESPEED * dy;
    }

    public void stopX() {
        if (!movingLeft  && !movingRight) {
            dx = 0;
        }
        if (movingLeft && !movingRight) {
            moveLeft();
        }
        if (!movingLeft && movingRight) {
            moveRight();
        }
    }


    public boolean crossed() {
        return y == 40;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isMovingForward() {
        return movingForward;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this. movingRight = movingRight;
    }

    public void setMovingForward(boolean movingForward) {
        this.movingForward = movingForward;
    }

    public void makeAlive() {
        alive = true;
    }

    public void makeDead() {
        alive = false;
    }



}
