package bumcrossing;

import java.awt.*;

public class Bonus {

    static final int WIDTH = 15;
    static final int HEIGHT = 15;

    private int x, y;
    private Image image;

    public Bonus(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public boolean consumed(Sprite sprite) {
        return sprite.getBounds().contains(getBounds());
    }

    public void hide() {
        x = -100;
        y = -100;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x, y, WIDTH, HEIGHT);
    }

}
