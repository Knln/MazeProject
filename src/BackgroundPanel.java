import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class BackgroundPanel extends JPanel {
    
    /**
     * Some serial ID
     */
    private static final long serialVersionUID = -9139266324799167727L;
    
    private String backgroundImage;
    
    public BackgroundPanel() {
        super();
    }
    
    public void setBackgroundImage(String image) {
        backgroundImage = image;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            try {
                g.drawImage(ImageIO.read(new File(backgroundImage)), 0, 0, null);
            } catch (IOException e) {
                System.out.println("Error: " + backgroundImage + " not found");
            }
        }
    }

}
