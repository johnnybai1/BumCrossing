package bumcrossing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GameIO {

    public static Image loadImage(String file, int scale) {
        Image image = null;
        try {
            image = ImageIO.read(new File("./data/" + file));
            image = image.getScaledInstance(image.getWidth(null) / scale, image.getHeight(null) / scale, Image.SCALE_SMOOTH);
        }
        catch (IOException e) {
            System.out.println("IOException in loadImage(): " + e);
            e.printStackTrace();
        }
        return image;
    }

    public static Image loadImage(String file) {
        return loadImage(file, 1);
    }

}
