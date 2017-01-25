package bumcrossing;

import javax.swing.*;
import java.awt.*;

public class GameApplet extends JApplet {

    public void init() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    Game mainPanel = new Game();
                    Container cp = getContentPane();
                    cp.setLayout(new FlowLayout());
                    cp.add(mainPanel);
                }
            });
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();

        }
    }
}
