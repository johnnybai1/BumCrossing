package bumcrossing;

import java.awt.*;

public class Cart extends Sprite {

    static final int INIT_X = Player.INIT_X - 5;
    static final int INIT_Y = Player.INIT_Y - 40;
    static final int WIDTH = 70;
    static final int HEIGHT = 80;

    private boolean connected;

    public Cart() {
        this(Player.INIT_X - 5, Player.INIT_Y - 40);
    }

    public Cart(int x, int y) {
        super(x, y);
        image = GameIO.loadImage("cart.png", 2);
        connected = true;
        setWidth(image.getWidth(null));
        setHeight(image.getHeight(null));
    }

    public void regenerate() {
        if (connected) {
            y = INIT_Y;
        }
        // Hide our cart off screen
        y = Game.HEIGHT + 100;
        x = Game.WIDTH + 100;
    }

    @Override
    public void update() {

    }

    public void update(Player player) {
        if (isConnected()) {
            x = player.getX() - 5;
            y = player.getY() - 40;
        }
        else {
            x = x + dx;
        }
    }

    public void draw(Graphics2D g2d, Player player) {
        if (isConnected()) {
            g2d.drawImage(image, player.getX() - 5, player.getY() - 40, null);
        }
        else super.draw(g2d);
    }

    public void shoot(Player player) {
        x = player.getX() - 5;
        y = player.getY() - 40;
        dy = 0;
        dx = -MOVESPEED;
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }
}
