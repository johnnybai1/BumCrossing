package bumcrossing;

public class Car extends Sprite {

    final static int START = -100;

    public Car(int x, int y) {
        super(x, y);
        image = GameIO.loadImage("car.png", 2);
        setWidth(image.getWidth(null));
        setHeight(image.getHeight(null));
        dx = 2*MOVESPEED;
    }


    @Override
    public void update() {
        x = x + dx;
        if (x + dx >= Game.WIDTH) {
            x = START;
        }
    }
}
